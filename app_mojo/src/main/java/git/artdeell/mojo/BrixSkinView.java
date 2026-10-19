package git.artdeell.mojo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/** Self-contained Minecraft skin model preview. Drag horizontally to rotate, vertically to tilt. */
public class BrixSkinView extends View {
    private Bitmap skin;
    private float yaw = -18f, pitch = 5f, lastX, lastY;
    // BRIX_SKIN_DETAIL_FIX: no filtered texel seams and no accidental anti-aliased sprite edges.
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Matrix matrix = new Matrix();
    private final java.util.HashMap<String,Bitmap> pieces = new java.util.HashMap<>();
    private final ArrayList<Quad> faces = new ArrayList<>();
    private float zoom=1f;
    public float getZoom(){return zoom;}
    public void setZoom(float value){zoom=Math.min(1.5f,Math.max(.65f,value));invalidate();}
    public void rotateBy(float degrees){yaw+=degrees;invalidate();}
    private boolean slim = false;
    private int pose = 0;
    public void setPose(int value) {pose=value;invalidate();}
    private static final class Quad {
        float depth;
        float[] dest;
        Rect uv;
        int color;
        Quad(float depth, float[] dest, Rect uv, int color) {this.depth=depth;this.dest=dest;this.uv=uv;this.color=color;}
    }
    public BrixSkinView(Context c) {super(c); init();}
    public BrixSkinView(Context c, AttributeSet a) {super(c,a);init();}
    private void init(){
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        paint.setFilterBitmap(false); // Minecraft texels must remain sharp, never blurred.
        paint.setDither(false);
        try{android.graphics.BitmapFactory.Options opts=new android.graphics.BitmapFactory.Options();opts.inScaled=false;
            skin=android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.brix_steve_skin,opts);}catch(Exception ignored){}
    }
    public void setSkin(Bitmap bmp) {
        if(bmp == null || bmp.getWidth() != bmp.getHeight() || bmp.getWidth() < 64 || bmp.getWidth() % 64 != 0) return;
        for(Bitmap piece:pieces.values()){if(piece!=null && !piece.isRecycled())piece.recycle();}
        pieces.clear();skin=bmp;invalidate();
    }
    public void setSlim(boolean value) {slim=value;invalidate();}
    private float[] project(float x, float y, float z) {
        double a=Math.toRadians(yaw), b=Math.toRadians(pitch);
        float xr=(float)(x*Math.cos(a)+z*Math.sin(a));
        float zr=(float)(-x*Math.sin(a)+z*Math.cos(a));
        float yr=(float)(y*Math.cos(b)-zr*Math.sin(b));
        float zz=(float)(y*Math.sin(b)+zr*Math.cos(b));
        float scale = Math.min(getWidth()/25f,getHeight()/38f) * 0.89f * zoom;
        float perspective=65f/(65f+zz);
        return new float[]{getWidth()/2f+xr*scale*perspective, getHeight()/2f+yr*scale*perspective,-zz};
    }
    private static Rect uv(int x,int y,int w,int h) {return new Rect(x,y,x+w,y+h);}
    private void add(float[][] corners,Rect texture,int fallback){
        float[] d=new float[8];float z=0;
        for(int i=0;i<4;i++){float[] p=project(corners[i][0],corners[i][1],corners[i][2]);d[i*2]=p[0];d[i*2+1]=p[1];z+=p[2];}
        float cross=(d[2]-d[0])*(d[5]-d[1])-(d[3]-d[1])*(d[4]-d[0]);
        if(cross <= .5f) return; // discard back-facing faces, so textures never mirror through the body
        faces.add(new Quad(z/4f,d,texture,fallback));
    }
    private void cuboid(float x,float y,float z,float w,float h,float d, int ux,int uy, int fw,int fh,int side,int top, int fallback) {
        float l=x-w/2,r=x+w/2,t=y,b=y+h,f=z-d/2,bk=z+d/2;
        // Map each visible cuboid side to the respective UV rectangles from a standard 64x64 skin.
        add(new float[][]{{l,t,f},{r,t,f},{r,b,f},{l,b,f}}, uv(ux,uy,fw,fh),fallback);
        add(new float[][]{{r,t,bk},{l,t,bk},{l,b,bk},{r,b,bk}}, uv(ux+fw+side,uy,fw,fh),fallback);
        add(new float[][]{{l,t,bk},{l,t,f},{l,b,f},{l,b,bk}},uv(ux-side,uy,side,fh),fallback);
        add(new float[][]{{r,t,f},{r,t,bk},{r,b,bk},{r,b,f}},uv(ux+fw,uy,side,fh),fallback);
        add(new float[][]{{l,t,bk},{r,t,bk},{r,t,f},{l,t,f}},uv(ux,uy-top,fw,top),fallback);
        add(new float[][]{{l,b,f},{r,b,f},{r,b,bk},{l,b,bk}},uv(ux+fw,uy-top,fw,top),fallback);
    }
    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        faces.clear();
        boolean valid=skin!=null && skin.getWidth()>=64 && skin.getHeight()>=64;
        int factor=valid ? skin.getWidth()/64 : 1;
        // Official Java skin layout UV coordinates, includes both newer 64x64 arms and legs.
        cuboid(0,-17,0,8,8,8,8,8,8,8,8,8,Color.rgb(210,161,122));
        cuboid(0,-9,0,8,12,4,20,20,8,12,4,4,Color.rgb(55,177,174));
        int armWidth=slim?3:4;
        if(pose==1){
            cuboid(-10,-14,0,12,armWidth,4,44,20,armWidth,12,4,4,Color.rgb(53,172,175));
            cuboid(10,-14,0,12,armWidth,4,36,52,armWidth,12,4,4,Color.rgb(53,172,175));
        }else{
            cuboid(-6,-9,0,armWidth,12,4,44,20,armWidth,12,4,4,Color.rgb(53,172,175));
            cuboid(6,-9,0,armWidth,12,4,36,52,armWidth,12,4,4,Color.rgb(53,172,175));
        }
        cuboid(-2,3,0,4,12,4,4,20,4,12,4,4,Color.rgb(61,70,161));
        cuboid(2,3,0,4,12,4,20,52,4,12,4,4,Color.rgb(61,70,161));
        // Minecraft Java 64x64 skin's second layer: hat, jacket, sleeves and pants.
        // Transparent pixels remain transparent; outer shells sit fractionally above the base.
        cuboid(0,-17,0,8.6f,8.6f,8.6f,40,8,8,8,8,8,Color.TRANSPARENT);
        cuboid(0,-9,0,8.35f,12.35f,4.35f,20,36,8,12,4,4,Color.TRANSPARENT);
        if(pose==0){
            cuboid(-6,-9,0,armWidth+.35f,12.35f,4.35f,52,52,armWidth,12,4,4,Color.TRANSPARENT);
            cuboid(6,-9,0,armWidth+.35f,12.35f,4.35f,44,36,armWidth,12,4,4,Color.TRANSPARENT);
        }
        cuboid(-2,3,0,4.35f,12.35f,4.35f,4,52,4,12,4,4,Color.TRANSPARENT);
        cuboid(2,3,0,4.35f,12.35f,4.35f,4,36,4,12,4,4,Color.TRANSPARENT);
        Collections.sort(faces,(a,b)->Float.compare(a.depth,b.depth));
        for(Quad q:faces){
            Path clip=new Path();clip.moveTo(q.dest[0],q.dest[1]);
            for(int i=1;i<4;i++)clip.lineTo(q.dest[i*2],q.dest[i*2+1]);clip.close();
            c.save();c.clipPath(clip);
            if(valid && q.uv.right<=64 && q.uv.bottom<=64 && q.uv.left>=0 && q.uv.top>=0){
                Rect s=new Rect(q.uv.left*factor,q.uv.top*factor,q.uv.right*factor,q.uv.bottom*factor);
                String key=s.left+":"+s.top+":"+s.width()+":"+s.height();
                Bitmap sub=pieces.get(key);
                if(sub==null){sub=Bitmap.createBitmap(skin,s.left,s.top,s.width(),s.height());pieces.put(key,sub);}
                float[] src={0,0,sub.getWidth(),0,sub.getWidth(),sub.getHeight(),0,sub.getHeight()};
                if(matrix.setPolyToPoly(src,0,q.dest,0,4)){
                    paint.setColor(Color.WHITE);paint.setAlpha(255);paint.setFilterBitmap(false);c.drawBitmap(sub,matrix,paint);
                }

            }else{
                if(Color.alpha(q.color)!=0){paint.setColor(q.color);c.drawPath(clip,paint);}
            }
            c.restore();
        }
    }
    @Override public boolean onTouchEvent(MotionEvent event){
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN: lastX=event.getX();lastY=event.getY();return true;
            case MotionEvent.ACTION_MOVE:
                yaw+=(event.getX()-lastX)*.5f;
                pitch=Math.max(-30,Math.min(30,pitch+(event.getY()-lastY)*.3f));
                lastX=event.getX();lastY=event.getY();invalidate();return true;
            case MotionEvent.ACTION_UP: performClick();return true;
        }
        return true;
    }
    @Override public boolean performClick(){super.performClick();return true;}
}

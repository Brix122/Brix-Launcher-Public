package git.artdeell.mojo.input.handleview;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import git.artdeell.mojo.R;
public class DrawerPullButton extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    public DrawerPullButton(Context c){super(c);setClickable(true);}
    public DrawerPullButton(Context c,@Nullable AttributeSet a){super(c,a);setClickable(true);}
    @Override protected void onDraw(Canvas c){super.onDraw(c);
      float cx=getWidth()/2f,cy=getHeight()/2f,sz=Math.min(getWidth(),getHeight());
      p.setColor(0xE90E121A);c.drawRoundRect(new RectF(1,1,getWidth()-1,getHeight()-1),sz*.22f,sz*.22f,p);
      p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(Math.max(2,sz*.04f));p.setColor(0xFFFF3B51);
      c.drawRoundRect(new RectF(2,2,getWidth()-2,getHeight()-2),sz*.22f,sz*.22f,p);p.setStyle(Paint.Style.FILL);
      android.graphics.drawable.Drawable icon=ContextCompat.getDrawable(getContext(),R.drawable.brix_ingame_menu);
      if(icon!=null){int q=(int)(sz*.64f);icon.setBounds((int)cx-q/2,(int)cy-q/2,(int)cx+q/2,(int)cy+q/2);icon.draw(c);}
    }
    @Override protected void onLayout(boolean changed,int l,int t,int r,int b){super.onLayout(changed,l,t,r,b);setTranslationX(0f);}
}

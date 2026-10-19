package git.artdeell.mojo.instances;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import git.artdeell.mojo.Tools;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** Actual installed per-version panorama, or a real installed mod/project icon.
 *  Different versions sometimes SHARE the same official menu panorama; a name badge
 *  distinguishes them without fabricating a screenshot. No BRIX logo in profile rows. */
public final class BrixVersionCover {
    private static final ConcurrentHashMap<String,Bitmap> CACHE=new ConcurrentHashMap<>();
    private static final Set<String> PENDING=ConcurrentHashMap.newKeySet();
    private static final Handler UI=new Handler(Looper.getMainLooper());
    private BrixVersionCover(){}

    public static Drawable icon(Resources res, DisplayInstance profile, Runnable updated) {
        String id=profile.versionId==null?"unknown":profile.versionId;
        String name=profile.name==null?"":profile.name;
        // Preserve intentionally chosen profile thumbnails, including modpack logos.
        File custom=profile.getInstanceIconLocation();
        if(custom!=null && custom.isFile() && custom.length()<4_000_000L){
            Bitmap b=BitmapFactory.decodeFile(custom.getAbsolutePath());
            if(b!=null)return new BitmapDrawable(res,scaleAndFrame(b,id,false));
        }
        String folder=profile.mInstanceRoot==null?"":profile.mInstanceRoot.getAbsolutePath();
        String key=id+"|"+name+"|"+folder;
        Bitmap cached=CACHE.get(key);
        if(cached!=null)return new BitmapDrawable(res,cached);
        if(PENDING.add(key))new Thread(()->{
            Bitmap source=findModIcon(profile);
            boolean mod=source!=null;
            if(source==null)source=readInstalledPanorama(id);
            Bitmap done=source==null?fallback(id,name):scaleAndFrame(source,id,!mod);
            CACHE.put(key,done);PENDING.remove(key);
            if(updated!=null)UI.post(updated);
        },"BrixVersionCover").start();
        return new BitmapDrawable(res,fallback(id,name));
    }
    private static String safe(String id){return id!=null && id.matches("[a-zA-Z0-9_+., -]{1,180}")?id:null;}
    private static Bitmap readInstalledPanorama(String id){
        String current=safe(id);if(current==null)return null;
        // Follow Fabric/Forge inheritsFrom metadata back to the exact installed vanilla JAR.
        for(int hop=0;hop<7;hop++){
            File home=new File(Tools.DIR_HOME_VERSION,current), jar=new File(home,current+".jar");
            if(jar.isFile()){
                Bitmap b=loadEntry(jar,new String[]{
                        "assets/minecraft/textures/gui/title/background/panorama_0.png",
                        "assets/minecraft/textures/gui/title/background/panorama_1.png"});
                if(b!=null)return b;
            }
            File json=new File(home,current+".json");
            if(!json.isFile()||json.length()>1_000_000L)break;
            try{
                byte[] bytes;
                try(java.io.FileInputStream input=new java.io.FileInputStream(json)){bytes=readBounded(input,1_000_000);}
                String parent=safe(new JSONObject(new String(bytes,StandardCharsets.UTF_8)).optString("inheritsFrom"));
                if(parent==null||parent.equals(current))break;current=parent;
            }catch(Exception e){break;}
        }
        return null;
    }
    private static Bitmap findModIcon(DisplayInstance profile){
        if(!(profile instanceof Instance))return null;
        Instance i=(Instance)profile;
        // Only inspect actual local files. Modrinth website image is not evidence the mod is installed.
        File dir=new File(i.getGameDirectory(),"mods");
        File[] jars=dir.listFiles(f->f.isFile()&&f.getName().toLowerCase(java.util.Locale.ROOT).endsWith(".jar")&&f.length()<120_000_000L);
        if(jars==null||jars.length==0)return null;
        final String n=(i.name==null?"":i.name).toLowerCase(java.util.Locale.ROOT);
        Arrays.sort(jars,Comparator.comparingInt(f->n.contains("sodium")&&f.getName().toLowerCase(java.util.Locale.ROOT).contains("sodium")?0:1));
        for(int x=0;x<Math.min(jars.length,24);x++){
            try(ZipFile zip=new ZipFile(jars[x])){
                String icon=null; ZipEntry meta=zip.getEntry("fabric.mod.json");
                if(meta!=null&&meta.getSize()<200_000L){
                    try(InputStream in=zip.getInputStream(meta)){
                        byte[] content=readBounded(in,200_000);
                        JSONObject data=new JSONObject(new String(content,StandardCharsets.UTF_8));
                        Object value=data.opt("icon");
                        if(value instanceof String)icon=(String)value;
                        else if(value instanceof JSONObject){JSONObject sizes=(JSONObject)value;icon=sizes.optString("128",sizes.optString("64",sizes.optString("32",null)));}
                    }
                }
                String[] candidates={icon,"assets/sodium/icon.png","assets/sodium/icon_128.png","icon.png"};
                for(String candidate:candidates){Bitmap b=loadFromZip(zip,candidate);if(b!=null)return b;}
            }catch(Exception ignored){}
        }
        return null;
    }
    private static Bitmap loadEntry(File jar,String[] paths){try(ZipFile z=new ZipFile(jar)){for(String path:paths){Bitmap b=loadFromZip(z,path);if(b!=null)return b;}}catch(Exception ignored){}return null;}
    private static byte[] readBounded(InputStream in,int limit)throws java.io.IOException{
        java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();byte[] buf=new byte[4096];int n;
        while((n=in.read(buf))!=-1){if(out.size()+n>limit)throw new java.io.IOException("Input too large");out.write(buf,0,n);}return out.toByteArray();
    }
    private static Bitmap loadFromZip(ZipFile z,String path){
        if(path==null||path.startsWith("/")||path.contains("..")||!path.toLowerCase(java.util.Locale.ROOT).endsWith(".png"))return null;
        ZipEntry e=z.getEntry(path);if(e==null||e.getSize()>3_000_000L)return null;
        try(InputStream in=z.getInputStream(e)){
            BitmapFactory.Options opts=new BitmapFactory.Options();opts.inScaled=false;opts.inSampleSize=2;
            Bitmap b=BitmapFactory.decodeStream(in,null,opts);
            if(b!=null&&b.getWidth()>0&&b.getHeight()>0&&b.getWidth()<=2048&&b.getHeight()<=2048)return b;
        }catch(Exception ignored){}
        return null;
    }
    private static Bitmap scaleAndFrame(Bitmap original,String version,boolean panorama){
        Bitmap result=Bitmap.createBitmap(120,120,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(result);
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        c.drawColor(0xFF181D29);
        int side=Math.min(original.getWidth(),original.getHeight());
        // Mod icon keeps entire square; panorama uses its true middle scene without fake UI buttons.
        Rect source=new Rect((original.getWidth()-side)/2,(original.getHeight()-side)/2,(original.getWidth()+side)/2,(original.getHeight()+side)/2);
        c.drawBitmap(original,source,new Rect(0,0,120,120),p);
        p.setColor(0xAF0A101A);c.drawRect(0,89,120,120,p);
        drawLabel(c,version,Color.WHITE);
        return result;
    }
    private static Bitmap fallback(String version,String profileName){
        Bitmap b=Bitmap.createBitmap(120,120,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(b);
        int hash=version.hashCode();int c1=Color.rgb(28+(hash&15),47+((hash>>>8)&31),61+((hash>>>16)&31));
        c.drawColor(c1);Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xFF57777B);c.drawRect(0,72,120,120,p);
        p.setColor(0xFF83A36A);for(int k=0;k<6;k++)c.drawRect(k*24,65+(k%2)*6,k*24+18,77+(k%2)*6,p);
        p.setColor(0xFF324D52);c.drawRect(19,31,45,79,p);c.drawRect(65,19,95,76,p);
        p.setColor(0xC4091018);c.drawRect(0,89,120,120,p);
        drawLabel(c,version,Color.WHITE);return b;
    }
    private static void drawLabel(Canvas c,String version,int color){
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);p.setColor(color);p.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);p.setTextSize(17f);
        String s=version==null?"Unknown":version;
        if(s.length()>13)s=s.substring(0,12)+"…";
        c.drawText(s,6,110,p);
    }
}

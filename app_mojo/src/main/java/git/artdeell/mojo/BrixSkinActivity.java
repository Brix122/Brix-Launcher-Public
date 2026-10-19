package git.artdeell.mojo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import git.artdeell.mojo.authenticator.accounts.Account;
import git.artdeell.mojo.authenticator.accounts.Accounts;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Preview local PNG and, when Microsoft Minecraft authentication is present, publish to Minecraft Services. */
public class BrixSkinActivity extends BaseActivity {
    private static final int PICK_SKIN=5312;
    private BrixSkinView preview;
    private TextView message;
    private boolean slim=false;
    private Button upload;
    private int d(float x){return (int)(getResources().getDisplayMetrics().density*x+.5f);}
    private File skinFile(){File dir=new File(getFilesDir(),"brix-skins");dir.mkdirs();return new File(dir,"preview.png");}
    private Button btn(String text,LinearLayout container){
        Button b=new Button(this);b.setText(text);b.setAllCaps(false);b.setTextColor(Color.WHITE);
        b.setBackgroundResource(R.drawable.brix_final_primary);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,d(41));lp.bottomMargin=d(8);container.addView(b,lp);return b;
    }
    @Override protected void onCreate(Bundle state){super.onCreate(state);
        LinearLayout shell=new LinearLayout(this);shell.setOrientation(LinearLayout.HORIZONTAL);
        shell.setPadding(d(10),d(9),d(10),d(9));shell.setBackgroundColor(getResources().getColor(R.color.brix_page));
        LinearLayout display=new LinearLayout(this);display.setOrientation(LinearLayout.VERTICAL);
        display.setPadding(d(12),d(7),d(12),d(9));display.setBackgroundResource(R.drawable.brix_glass_stage);
        LinearLayout.LayoutParams displayLp=new LinearLayout.LayoutParams(0,-1,1.35f);shell.addView(display,displayLp);
        TextView displayTitle=new TextView(this);displayTitle.setText("CHARACTER PREVIEW  •  DRAG TO ROTATE");
        displayTitle.setTextSize(12);displayTitle.setTextColor(getResources().getColor(R.color.brix_subtext));displayTitle.setTypeface(null,1);display.addView(displayTitle);
        preview=new BrixSkinView(this);preview.setContentDescription("Rotate Minecraft skin model");
        display.addView(preview,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout modelActions=new LinearLayout(this);modelActions.setGravity(Gravity.CENTER);
        Button left=new Button(this);left.setText("↶");left.setTextSize(19);left.setTextColor(getResources().getColor(R.color.brix_text));left.setBackgroundResource(R.drawable.brix_glass_card);
        Button right=new Button(this);right.setText("↷");right.setTextSize(19);right.setTextColor(getResources().getColor(R.color.brix_text));right.setBackgroundResource(R.drawable.brix_glass_card);
        Button zoomIn=new Button(this);zoomIn.setText("+");zoomIn.setTextColor(getResources().getColor(R.color.brix_text));zoomIn.setBackgroundResource(R.drawable.brix_glass_card);
        Button zoomOut=new Button(this);zoomOut.setText("−");zoomOut.setTextColor(getResources().getColor(R.color.brix_text));zoomOut.setBackgroundResource(R.drawable.brix_glass_card);
        for(Button action:new Button[]{left,right,zoomOut,zoomIn}){
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(d(51),d(36));params.setMargins(d(4),0,d(4),0);modelActions.addView(action,params);
        }
        left.setOnClickListener(v->preview.rotateBy(-35));right.setOnClickListener(v->preview.rotateBy(35));
        zoomIn.setOnClickListener(v->preview.setZoom(preview.getZoom()+.12f));zoomOut.setOnClickListener(v->preview.setZoom(preview.getZoom()-.12f));
        display.addView(modelActions);
        android.widget.ScrollView scroll=new android.widget.ScrollView(this);scroll.setFillViewport(false);scroll.setVerticalScrollBarEnabled(false);
        LinearLayout.LayoutParams sideLP=new LinearLayout.LayoutParams(0,-1,1f);sideLP.leftMargin=d(9);shell.addView(scroll,sideLP);
        LinearLayout side=new LinearLayout(this);side.setOrientation(LinearLayout.VERTICAL);
        side.setPadding(d(14),d(12),d(14),d(12));side.setBackgroundResource(R.drawable.brix_glass_panel);scroll.addView(side);
        TextView title=new TextView(this);title.setText("SKIN STUDIO");title.setTextSize(22);title.setTextColor(getResources().getColor(R.color.brix_text));title.setTypeface(null,1);side.addView(title);
        message=new TextView(this);message.setText("Java Edition skin • drag to rotate • 64×64 / 128×128 PNG, legacy 64×32.");message.setTextColor(getResources().getColor(R.color.brix_subtext));message.setTextSize(12);message.setPadding(0,d(5),0,d(10));side.addView(message);
        Button choose=btn("IMPORT SKIN PNG",side);choose.setOnClickListener(v->{Intent pick=new Intent(Intent.ACTION_GET_CONTENT);pick.setType("image/png");pick.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(Intent.createChooser(pick,"Choose Minecraft skin PNG"),PICK_SKIN);});
        Button shape=btn("MODEL: CLASSIC",side);shape.setOnClickListener(v->{slim=!slim;preview.setSlim(slim);shape.setText(slim?"MODEL: SLIM":"MODEL: CLASSIC");});
        Button poseButton=btn("POSE: STANDING",side);
        poseButton.setOnClickListener(v->{boolean raised=poseButton.getText().toString().contains("STANDING");preview.setPose(raised?1:0);poseButton.setText(raised?"POSE: ARMS OUT":"POSE: STANDING");});
        upload=btn("APPLY IN-GAME  /  ELY.BY",side);upload.setOnClickListener(v->openElySkinSite());
        TextView note=new TextView(this);note.setText("Ely.by authentication opens in your browser. A compatible skin system is required for in-game skins; PNG import changes this preview only.");note.setTextColor(getResources().getColor(R.color.brix_subtext));note.setTextSize(10);note.setPadding(0,d(5),0,d(8));side.addView(note);
        Button back=btn("←  BACK",side);back.setBackgroundResource(R.drawable.brix_glass_card);back.setTextColor(getResources().getColor(R.color.brix_text));back.setOnClickListener(v->finish());
        setContentView(shell);
        File f=skinFile();if(f.exists())preview.setSkin(BitmapFactory.decodeFile(f.getAbsolutePath()));
    }
    @Override protected void onActivityResult(int req,int result,Intent data){super.onActivityResult(req,result,data);
        if(req!=PICK_SKIN || result!=Activity.RESULT_OK || data==null)return;
        try(InputStream in=getContentResolver().openInputStream(data.getData())){
            BitmapFactory.Options opts=new BitmapFactory.Options();
            opts.inScaled=false; // BRIX_SKIN_IMPORT_VALIDATION: never density-scale imported skin pixels
            Bitmap image=BitmapFactory.decodeStream(in,null,opts);
            if(image==null || image.getWidth()!=64 && image.getWidth()!=128 ||
                    (image.getHeight()!=image.getWidth() && !(image.getWidth()==64 && image.getHeight()==32))){
                message.setText("Invalid skin. Use Java 64×64, legacy 64×32 or 128×128 PNG (modded textures).");return;
            }
            boolean legacy=image.getHeight()==32;
            if(legacy)image=convertLegacySkin(image);
            try(FileOutputStream out=new FileOutputStream(skinFile())){image.compress(Bitmap.CompressFormat.PNG,100,out);}
            preview.setSkin(image);message.setText(legacy?"Legacy skin loaded. Missing left-limb pixels mirrored for the preview.":"Imported skin loaded • pixel-sharp 3D preview. Drag to rotate.");
        }catch(Exception e){message.setText("Skin import failed: "+e.getMessage());}
    }
    /** The older Java format has only right-arm/right-leg UVs. Expand to modern 64×64 atlas. */
    private static Bitmap convertLegacySkin(Bitmap input){
        Bitmap modern=Bitmap.createBitmap(64,64,Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas=new android.graphics.Canvas(modern);
        android.graphics.Paint paint=new android.graphics.Paint();paint.setFilterBitmap(false);
        canvas.drawBitmap(input,0,0,paint);
        // Copy old arm and leg cubes to the newer left-limb atlas. No resizing or filtering.
        // Mirror the old right limbs into the modern left-limb UV slots, nearest-neighbour.
        canvas.save();canvas.scale(-1f,1f,40,56);
        canvas.drawBitmap(input,new android.graphics.Rect(40,16,56,32),new android.graphics.Rect(32,48,48,64),paint);
        canvas.restore();
        canvas.save();canvas.scale(-1f,1f,24,56);
        canvas.drawBitmap(input,new android.graphics.Rect(0,16,16,32),new android.graphics.Rect(16,48,32,64),paint);
        canvas.restore();
        return modern;
    }
    private void openElySkinSite(){
        new AlertDialog.Builder(this).setTitle("Apply your skin with Ely.by")
            .setMessage("Your imported PNG is saved locally. Sign in on Ely.by to add/select your skin. The game must use a compatible Ely.by skin system for the skin to appear in-game. This does not silently inject a skin into an offline game. Continue?")
            .setNegativeButton("Cancel",null)
            .setNeutralButton("Skin upload page",(dlg,which)->startActivity(
                new Intent(Intent.ACTION_VIEW,Uri.parse("https://ely.by/skins/add"))))
            .setPositiveButton("Ely.by login",(dlg,which)->{
                Intent browser=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.ely.by/site/login"));
                startActivity(browser);
            }).show();
    }
    private void applySkin(){
        Account account=Accounts.getCurrent();
        if(account==null || account.authType != git.artdeell.mojo.authenticator.AuthType.MICROSOFT || account.isLocal() || account.accessToken==null || account.accessToken.equals("0")){
            new AlertDialog.Builder(this).setTitle("Minecraft Account Required")
                .setMessage("Sign in with a licensed Microsoft Minecraft Java account first. Local skin preview does not automatically change the in-game skin.")
                .setPositiveButton("OK",null).show();return;
        }
        File f=skinFile();if(!f.exists()){message.setText("Import a 64 x 64 PNG first.");return;}
        new AlertDialog.Builder(this).setTitle("Publish this skin?")
            .setMessage("Upload the selected skin to the official Minecraft Services API for your signed-in Microsoft account?")
            .setNegativeButton("Cancel",null)
            .setPositiveButton("UPLOAD",(dialog,which)->{
                upload.setEnabled(false);message.setText("Uploading to Minecraft Services...");
                new Thread(()->{
                    String result;HttpURLConnection conn=null;
                    try {
                        String boundary="----BrixSkinBoundary"+System.currentTimeMillis();
                        conn=(HttpURLConnection)new URL("https://api.minecraftservices.com/minecraft/profile/skins").openConnection();
                        conn.setRequestMethod("POST");conn.setConnectTimeout(15000);conn.setReadTimeout(20000);conn.setDoOutput(true);
                        conn.setRequestProperty("Authorization","Bearer "+account.accessToken);
                        conn.setRequestProperty("Content-Type","multipart/form-data; boundary="+boundary);
                        String prefix="--"+boundary+"\r\nContent-Disposition: form-data; name=\"variant\"\r\n\r\n"+(slim?"slim":"classic")+"\r\n";
                        String fileheader="--"+boundary+"\r\nContent-Disposition: form-data; name=\"file\"; filename=\"skin.png\"\r\nContent-Type: image/png\r\n\r\n";
                        try(OutputStream out=conn.getOutputStream();java.io.FileInputStream in=new java.io.FileInputStream(f)){
                            out.write(prefix.getBytes(StandardCharsets.UTF_8));out.write(fileheader.getBytes(StandardCharsets.UTF_8));
                            byte[] buf=new byte[8192];int len;while((len=in.read(buf))!=-1)out.write(buf,0,len);
                            out.write(("\r\n--"+boundary+"--\r\n").getBytes(StandardCharsets.UTF_8));
                        }
                        int status=conn.getResponseCode();
                        result=status>=200 && status<300?"Skin uploaded successfully. Rejoin Minecraft to see it.":"Minecraft Services rejected upload (HTTP "+status+"). Sign in again if your token expired.";
                    }catch(Exception ex){result="Upload failed: "+ex.getClass().getSimpleName()+". Check network and account.";}
                    finally{if(conn!=null)conn.disconnect();}
                    final String msg=result;runOnUiThread(()->{upload.setEnabled(true);message.setText(msg);});
                },"BrixSkinUpload").start();
            }).show();
    }
}

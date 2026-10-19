package git.artdeell.mojo.game;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import git.artdeell.mojo.BrixTheme;
import git.artdeell.mojo.CallbackBridge;
import git.artdeell.mojo.R;
import git.artdeell.mojo.game.platform.Platform;

/** Separate BRIX modules panel and controls editor. Each module operates on real Minecraft input. */
public final class BrixGamePanel {
    private static final Handler HANDLER=new Handler(Looper.getMainLooper());
    private static final long[] TOUCHES=new long[128];private static int cursor;
    private static TextView cpsLabel, fpsLabel;private static GameActivity currentGame;
    private static final Runnable TICK=new Runnable(){@Override public void run(){
        if((cpsLabel==null&&fpsLabel==null)||currentGame==null)return;
        long now=android.os.SystemClock.uptimeMillis();int count=0;
        synchronized(TOUCHES){for(long t:TOUCHES)if(t!=0&&now-t<1000)count++;}
        if(cpsLabel!=null)cpsLabel.setText("BRIX  •  "+count+" CPS");
        if(fpsLabel!=null){int fps=BrixFrameTracker.recentFps();
            fpsLabel.setText(fps>=0?("BRIX  •  "+fps+" FPS"):"BRIX  •  FPS unavailable");}
        HANDLER.postDelayed(this,250);
    }};
    private BrixGamePanel(){}
    public static void registerPrimaryClick(){synchronized(TOUCHES){TOUCHES[cursor++%TOUCHES.length]=android.os.SystemClock.uptimeMillis();}}
    private static int dp(GameActivity c,int v){return (int)(v*c.getResources().getDisplayMetrics().density+.5f);}
    private static GradientDrawable bg(int color,int border,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);d.setStroke(1,border);return d;}
    public static void stopHud(){HANDLER.removeCallbacks(TICK);
        if(cpsLabel!=null&&cpsLabel.getParent() instanceof android.view.ViewGroup)
            ((android.view.ViewGroup)cpsLabel.getParent()).removeView(cpsLabel);
        if(fpsLabel!=null&&fpsLabel.getParent() instanceof android.view.ViewGroup)
            ((android.view.ViewGroup)fpsLabel.getParent()).removeView(fpsLabel);
        cpsLabel=null;fpsLabel=null;currentGame=null;BrixFrameTracker.clear();}
    public static void toggleHud(GameActivity a){if(cpsLabel!=null){
            if(cpsLabel.getParent() instanceof android.view.ViewGroup)
                ((android.view.ViewGroup)cpsLabel.getParent()).removeView(cpsLabel);
            cpsLabel=null;if(fpsLabel==null){HANDLER.removeCallbacks(TICK);currentGame=null;}return;}
        FrameLayout content=a.findViewById(R.id.content_frame);if(content==null)return;
        cpsLabel=new TextView(a);cpsLabel.setTextColor(Color.WHITE);cpsLabel.setTextSize(13);cpsLabel.setTypeface(null,Typeface.BOLD);
        cpsLabel.setPadding(dp(a,12),dp(a,9),dp(a,12),dp(a,9));cpsLabel.setBackground(bg(0xDD11151D,0xFFEA233F,dp(a,12)));
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(-2,-2,Gravity.TOP|Gravity.END);lp.topMargin=dp(a,54);lp.rightMargin=dp(a,14);
        content.addView(cpsLabel,lp);currentGame=a;HANDLER.removeCallbacks(TICK);TICK.run();
    }
    /** Actual presented-frame FPS overlay. No F3/debug menu; unsupported surfaces show N/A. */
    public static void toggleFpsHud(GameActivity a){
        if(fpsLabel!=null){
            if(fpsLabel.getParent() instanceof android.view.ViewGroup)
                ((android.view.ViewGroup)fpsLabel.getParent()).removeView(fpsLabel);
            fpsLabel=null;if(cpsLabel==null){HANDLER.removeCallbacks(TICK);currentGame=null;}return;
        }
        FrameLayout content=a.findViewById(R.id.content_frame);if(content==null)return;
        fpsLabel=new TextView(a);fpsLabel.setTextColor(Color.WHITE);fpsLabel.setTextSize(13);
        fpsLabel.setTypeface(null,Typeface.BOLD);
        fpsLabel.setPadding(dp(a,12),dp(a,9),dp(a,12),dp(a,9));
        fpsLabel.setBackground(bg(0xE2121821,0xFFEA233F,dp(a,12)));
        fpsLabel.setText("BRIX  •  measuring FPS...");
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(-2,-2,Gravity.TOP|Gravity.END);
        lp.topMargin=dp(a,96);lp.rightMargin=dp(a,14);
        content.addView(fpsLabel,lp);currentGame=a;
        HANDLER.removeCallbacks(TICK);TICK.run();
    }
    private static void combo(int key){
        // Hold F3 while tapping another key, as on the physical keyboard.
        int mods=CallbackBridge.getCurrentMods();
        Platform.PLATFORM.sendKeyEvent(KeyEvent.KEYCODE_F3,true,mods);
        Platform.PLATFORM.sendKeyEvent(key,true,mods);
        Platform.PLATFORM.sendKeyEvent(key,false,mods);
        Platform.PLATFORM.sendKeyEvent(KeyEvent.KEYCODE_F3,false,mods);
    }
    private static void runModule(GameActivity a,int index){switch(index){
        case 0:toggleFpsHud(a);break;
        case 1:toggleHud(a);break;
        case 2:CallbackBridge.sendKeyPress(KeyEvent.KEYCODE_F2);break;
        case 3:CallbackBridge.sendKeyPress(KeyEvent.KEYCODE_F5);break;
        case 4:CallbackBridge.sendKeyPress(KeyEvent.KEYCODE_F1);break;
        case 5:CallbackBridge.sendKeyPress(KeyEvent.KEYCODE_E);break;
        case 6:CallbackBridge.sendKeyPress(KeyEvent.KEYCODE_T);break;
        case 7:CallbackBridge.sendKeyPress(KeyEvent.KEYCODE_ESCAPE);break;
        case 8:GameActivity.toggleMouse(a);break;
        case 9:a.brixToggleControls();break;
        case 10:combo(KeyEvent.KEYCODE_B);break;
        case 11:combo(KeyEvent.KEYCODE_G);break;
        case 12:combo(KeyEvent.KEYCODE_H);break;
        case 13:a.brixOpenLog();break;
        case 14:a.brixOpenControlsEditor();break;
    }}
    private static void add(GameActivity a,LinearLayout column,Dialog dialog,String label,String desc,Runnable action,int fg,int muted,int bg,int border){
        LinearLayout tile=new LinearLayout(a);tile.setOrientation(LinearLayout.VERTICAL);tile.setPadding(dp(a,15),dp(a,10),dp(a,10),dp(a,10));tile.setBackground(bg(bg,border,dp(a,13)));
        TextView main=new TextView(a);main.setText(label);main.setTextSize(14);main.setTypeface(null,Typeface.BOLD);main.setTextColor(fg);tile.addView(main);
        TextView detail=new TextView(a);detail.setText(desc);detail.setTextColor(muted);detail.setTextSize(11);tile.addView(detail);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(a,7);column.addView(tile,lp);
        tile.setOnClickListener(v->{dialog.dismiss();action.run();});
    }
    private static void show(GameActivity a,boolean tools){
        boolean dark=BrixTheme.dark(a);int surface=dark?0xFF151820:0xFFF6F7FB,tile=dark?0xFF212630:Color.WHITE,fg=dark?Color.WHITE:0xFF18202B,muted=dark?0xFFAFB7C6:0xFF5A6270,border=dark?0xFF394151:0xFFD5DAE5;
        Dialog dialog=new Dialog(a);dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout outer=new LinearLayout(a);outer.setOrientation(LinearLayout.VERTICAL);outer.setPadding(dp(a,16),dp(a,14),dp(a,16),dp(a,16));outer.setBackground(bg(surface,border,dp(a,21)));
        TextView header=new TextView(a);header.setText(tools?"BRIX  /  LIVE MODULES":"BRIX  /  CONTROL BUILDER");header.setTextColor(0xFFED243A);header.setTextSize(20);header.setTypeface(null,Typeface.BOLD);outer.addView(header);
        TextView subtitle=new TextView(a);subtitle.setText(tools?"Real gameplay functions • configure visibility in Control Builder":"Touch layout, save files, binding controls");subtitle.setTextColor(muted);subtitle.setTextSize(12);subtitle.setPadding(0,dp(a,3),0,dp(a,10));outer.addView(subtitle);
        ScrollView scroll=new ScrollView(a);LinearLayout rows=new LinearLayout(a);rows.setOrientation(LinearLayout.VERTICAL);scroll.addView(rows);outer.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        if(tools){int shown=0;for(int i=0;i<BrixModuleRegistry.IDS.length;i++)if(BrixModuleRegistry.enabled(a,i)){
            final int idx=i;add(a,rows,dialog,String.format(java.util.Locale.ROOT,"%02d  %s",i+1,BrixModuleRegistry.NAMES[i]),BrixModuleRegistry.DESCRIPTIONS[i],()->runModule(a,idx),fg,muted,tile,border);shown++;}
            if(shown==0)add(a,rows,dialog,"All modules hidden","Open BRIX Control Builder to enable modules",()->a.brixOpenControlsEditor(),fg,muted,tile,border);
        }else{
            add(a,rows,dialog,"Edit touch controls","Move, resize and assign existing game buttons",a::brixOpenControlsEditor,fg,muted,tile,border);
            add(a,rows,dialog,"Add touch button","Open existing control editor",a::brixAddButton,fg,muted,tile,border);
            add(a,rows,dialog,"Add joystick","Add real virtual joystick",a::brixAddJoystick,fg,muted,tile,border);
            add(a,rows,dialog,"Save controls","Save active controlmap",a::brixSaveControls,fg,muted,tile,border);
            add(a,rows,dialog,"More touch settings","Existing input preferences",a::brixOpenQuickSettings,fg,muted,tile,border);
        }
        dialog.setContentView(outer);dialog.show();Window w=dialog.getWindow();if(w!=null){w.setBackgroundDrawableResource(android.R.color.transparent);w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);WindowManager.LayoutParams lp=w.getAttributes();lp.dimAmount=.42f;w.setAttributes(lp);int width=Math.min(dp(a,460),(int)(a.getResources().getDisplayMetrics().widthPixels*.43f));w.setLayout(Math.max(dp(a,310),width),(int)(a.getResources().getDisplayMetrics().heightPixels*.88f));w.setGravity(tools?Gravity.START|Gravity.CENTER_VERTICAL:Gravity.END|Gravity.CENTER_VERTICAL);}
    }
    public static void showTools(GameActivity a){show(a,true);}public static void showControls(GameActivity a){show(a,false);}
}

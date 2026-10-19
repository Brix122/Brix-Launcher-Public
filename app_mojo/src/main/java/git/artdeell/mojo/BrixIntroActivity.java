package git.artdeell.mojo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BrixIntroActivity extends Activity {
  private int page=0; private TextView title,description,step; private Button next;
  private ImageView logo;
  public static void openIfNeeded(Activity a) {
    if(!a.getSharedPreferences("brix_intro",MODE_PRIVATE).getBoolean("completed_v2",false))
      a.startActivity(new Intent(a,BrixIntroActivity.class));
  }
  private int dp(int n) {return (int)(getResources().getDisplayMetrics().density*n+.5f);}
  private GradientDrawable shape(int col,int radius) {GradientDrawable d=new GradientDrawable();d.setColor(col);d.setCornerRadius(dp(radius));return d;}
  private TextView text(String value,int size,int color,boolean bold) {TextView v=new TextView(this);v.setText(value);v.setTextColor(color);v.setTextSize(size);if(bold)v.setTypeface(null,Typeface.BOLD);return v;}
  @Override public void onCreate(Bundle state) {super.onCreate(state);getWindow().getDecorView().setSystemUiVisibility(5894);setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    LinearLayout shell=new LinearLayout(this);shell.setOrientation(LinearLayout.HORIZONTAL);shell.setPadding(dp(24),dp(18),dp(24),dp(18));shell.setBackgroundColor(0xFFF6F7FA);
    LinearLayout art=new LinearLayout(this);art.setOrientation(LinearLayout.VERTICAL);art.setGravity(Gravity.CENTER);art.setPadding(dp(14),dp(12),dp(14),dp(12));art.setBackground(shape(0xFF14090E,22));shell.addView(art,new LinearLayout.LayoutParams(0,-1,1));
    logo=new ImageView(this);logo.setImageResource(R.drawable.brix_bl_logo);logo.setScaleType(ImageView.ScaleType.FIT_CENTER);art.addView(logo,new LinearLayout.LayoutParams(dp(172),dp(172)));
    TextView brand=text("BRIX LAUNCHER",19,Color.WHITE,true);brand.setGravity(Gravity.CENTER);art.addView(brand);
    LinearLayout detail=new LinearLayout(this);detail.setOrientation(LinearLayout.VERTICAL);detail.setGravity(Gravity.CENTER_VERTICAL);detail.setPadding(dp(32),dp(12),dp(10),dp(12));shell.addView(detail,new LinearLayout.LayoutParams(0,-1,1.2f));
    step=text("01 / 03",13,0xFFE50914,true);detail.addView(step);title=text("",28,0xFF17171B,true);title.setPadding(0,dp(10),0,dp(12));detail.addView(title);
    description=text("",16,0xFF5D606A,false);description.setLineSpacing(dp(4),1);detail.addView(description);
    LinearLayout nav=new LinearLayout(this);nav.setGravity(Gravity.END);nav.setPadding(0,dp(24),0,0);detail.addView(nav);
    Button skip=new Button(this);skip.setText("Skip");skip.setTextColor(0xFF17171B);skip.setAllCaps(false);nav.addView(skip);skip.setOnClickListener(v->finishIntro());
    next=new Button(this);next.setTextColor(Color.WHITE);next.setAllCaps(false);next.setBackground(shape(0xFFE50914,12));LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(dp(150),dp(48));bp.leftMargin=dp(12);nav.addView(next,bp);next.setOnClickListener(v->{if(page>=2)finishIntro();else {page++;render();}});
    setContentView(shell);render();
  }
  private void render(){String[] headlines={"Welcome to Brix Launcher","Create your first Java world","Control every adventure"};String[] desc={"A premium Java Edition workspace for Android. Choose your game version, customize your setup and jump in.","Sign in with your Minecraft account, create a game instance, choose a compatible Java runtime and select PLAY.","Configure touch controls, renderer, RAM and game files in Settings. Keep your game updated and use the Tools area for quick access."};title.setText(headlines[page]);description.setText(desc[page]);step.setText("0"+(page+1)+" / 03   •   GETTING STARTED");next.setText(page==2?"START":"NEXT");logo.setAlpha(0.25f);logo.setScaleX(.88f);logo.setScaleY(.88f);logo.animate().alpha(1).scaleX(1).scaleY(1).setDuration(550).start();}
  private void finishIntro(){getSharedPreferences("brix_intro",Context.MODE_PRIVATE).edit().putBoolean("completed_v2",true).apply();finish();}
}

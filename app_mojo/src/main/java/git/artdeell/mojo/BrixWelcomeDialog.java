package git.artdeell.mojo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class BrixWelcomeDialog {
  private static final int RED = Color.rgb(229,9,20);
  private static final int DARK = Color.rgb(25,26,31);
  private static int dp(Context c, int v) { return Math.round(c.getResources().getDisplayMetrics().density*v); }
  private static GradientDrawable bg(int color, int radius, Context c) {
    GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(c,radius)); return d;
  }
  public static void showIfFirstRun(Activity a) {
    SharedPreferences prefs = a.getSharedPreferences("brix_launcher_welcome",Context.MODE_PRIVATE);
    if (prefs.getBoolean("intro_done_v1",false) || a.isFinishing()) return;
    Dialog dialog = new Dialog(a);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    LinearLayout root = new LinearLayout(a); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(a,25),dp(a,18),dp(a,25),dp(a,18)); root.setBackground(bg(Color.WHITE,20,a));
    TextView brand = new TextView(a); brand.setText("â—†  BRIX LAUNCHER"); brand.setTextColor(RED); brand.setTypeface(null,Typeface.BOLD); brand.setTextSize(16); root.addView(brand);
    TextView title = new TextView(a); title.setTextColor(DARK); title.setTypeface(null,Typeface.BOLD); title.setTextSize(26); title.setPadding(0,dp(a,11),0,dp(a,5)); root.addView(title);
    TextView body = new TextView(a); body.setTextColor(Color.rgb(88,90,99)); body.setTextSize(15); body.setLineSpacing(dp(a,3),1); root.addView(body);
    TextView index = new TextView(a); index.setTextColor(RED); index.setTextSize(12); index.setPadding(0,dp(a,16),0,dp(a,8)); root.addView(index);
    LinearLayout actions = new LinearLayout(a); actions.setGravity(Gravity.END); root.addView(actions);
    Button skip = new Button(a); skip.setText("SKIP"); skip.setTextColor(DARK); skip.setAllCaps(false); actions.addView(skip);
    Button next = new Button(a); next.setTextColor(Color.WHITE); next.setAllCaps(false); next.setBackground(bg(RED,12,a)); LinearLayout.LayoutParams nextParams=new LinearLayout.LayoutParams(dp(a,146),dp(a,48)); nextParams.leftMargin=dp(a,8); actions.addView(next,nextParams);
    final int[] page = {0};
    Runnable render = () -> {
      if(page[0] == 0) { title.setText("Your Java world, anywhere."); body.setText("Welcome to Brix Launcher. A customizable Java Edition launcher for Android, with a fresh red-and-white workspace."); }
      else if(page[0] == 1) { title.setText("Set up your game"); body.setText("1. Add a Minecraft account.\n2. Create or select a game instance.\n3. Choose a version, then tap PLAY.\nGame ownership may be required."); }
      else { title.setText("Make it yours"); body.setText("Use Settings for Java runtime, graphics, RAM and video options. Customize touch controls to match your device before playing."); }
      index.setText("GETTING STARTED   â€¢   " + (page[0]+1) + " / 3"); next.setText(page[0] == 2 ? "START PLAYING" : "NEXT");
    };
    View.OnClickListener done = v -> { prefs.edit().putBoolean("intro_done_v1",true).apply(); dialog.dismiss(); };
    skip.setOnClickListener(done);
    next.setOnClickListener(v -> { if(page[0] >= 2) done.onClick(v); else { page[0]++; render.run(); } });
    render.run(); dialog.setContentView(root); dialog.setCancelable(false);
    Window w=dialog.getWindow(); if(w!=null) { w.setBackgroundDrawableResource(android.R.color.transparent); w.setLayout(Math.min(dp(a,680),a.getResources().getDisplayMetrics().widthPixels-dp(a,35)),WindowManager.LayoutParams.WRAP_CONTENT); }
    dialog.show(); w=dialog.getWindow(); if(w!=null) w.setLayout(Math.min(dp(a,680),a.getResources().getDisplayMetrics().widthPixels-dp(a,35)),WindowManager.LayoutParams.WRAP_CONTENT);
  }
}
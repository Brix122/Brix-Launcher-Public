package git.artdeell.mojo;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/** BRIX startup identity sequence, shown on every normal launcher opening. */
public final class BrixSplashActivity extends Activity {
    private final Handler handler=new Handler(Looper.getMainLooper());
    private int dp(float n){return (int)(getResources().getDisplayMetrics().density*n+.5f);}
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
            View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        FrameLayout root=new FrameLayout(this);root.setBackgroundColor(Color.WHITE);
        LinearLayout stack=new LinearLayout(this);stack.setGravity(Gravity.CENTER);
        stack.setOrientation(LinearLayout.VERTICAL);stack.setPadding(dp(18),dp(12),dp(18),dp(12));
        ImageView logo=new ImageView(this);logo.setImageResource(R.drawable.brix_bl_logo);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams imageParams=new LinearLayout.LayoutParams(dp(95),dp(95));imageParams.bottomMargin=dp(9);
        stack.addView(logo,imageParams);
        logo.setAlpha(0f);logo.setScaleX(.82f);logo.setScaleY(.82f);
        logo.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(850)
             .setInterpolator(new DecelerateInterpolator()).start();
        TextView welcome=new TextView(this);welcome.setText("");welcome.setTextColor(0xFFDD0028);
        welcome.setTextSize(18);welcome.setGravity(Gravity.CENTER);welcome.setTypeface(null,Typeface.NORMAL);
        stack.addView(welcome);
        TextView name=new TextView(this);name.setText("BRIX LAUNCHER");name.setTextColor(0xFFE40029);
        name.setTextSize(31);name.setTypeface(null,Typeface.BOLD);name.setGravity(Gravity.CENTER);
        stack.addView(name);name.setAlpha(0f);name.setTranslationY(dp(9));
        name.animate().alpha(1f).translationY(0).setDuration(720).setStartDelay(620).start();
        TextView stage=new TextView(this);stage.setText("Initializing launcher");stage.setTextColor(0xFFCE1534);
        stage.setTextSize(12);stage.setGravity(Gravity.CENTER);stage.setPadding(0,dp(15),0,dp(7));stack.addView(stage);
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        bar.setIndeterminate(false);bar.setMax(100);
        bar.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFFE90030));
        bar.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFDDE3));
        stack.addView(bar,new LinearLayout.LayoutParams(dp(260),dp(5)));
        FrameLayout.LayoutParams bodyParams=new FrameLayout.LayoutParams(-1,-1,Gravity.CENTER);
        root.addView(stack,bodyParams);setContentView(root);
        final String line="WELCOME TO";
        for(int i=0;i<line.length();i++){
            final int pos=i+1;
            handler.postDelayed(()->welcome.setText(line.substring(0,pos)),180L+i*72L);
        }
        ValueAnimator progress=ValueAnimator.ofInt(0,100);progress.setDuration(4000);
        progress.setInterpolator(new android.view.animation.LinearInterpolator());
        progress.addUpdateListener(a->bar.setProgress((int)a.getAnimatedValue()));progress.start();
        handler.postDelayed(()->stage.setText("Preparing game workspace"),1500);
        handler.postDelayed(()->stage.setText("Almost ready"),2950);
        handler.postDelayed(()->{
            if(!isFinishing()){
                startActivity(new Intent(this,TestStorageActivity.class));finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        },4000);
    }
    @Override protected void onDestroy(){super.onDestroy();handler.removeCallbacksAndMessages(null);}
}

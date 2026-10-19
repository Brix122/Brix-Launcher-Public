package git.artdeell.mojo;
import android.os.Bundle;
import android.graphics.Color;
import android.content.Intent;
import android.view.Gravity;
import android.widget.*;
import android.view.View;
public class BrixCreditsActivity extends BaseActivity {
  @Override public boolean setFullscreen(){ return true; }
  int d(int n){return (int)(getResources().getDisplayMetrics().density*n+.5f);}
  TextView label(String s,int size,int col){ TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(col);return v; }
  @Override protected void onCreate(Bundle b){super.onCreate(b);
    LinearLayout root=new LinearLayout(this);root.setPadding(d(25),d(16),d(25),d(16));root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(0xFF0A0B0F);
    TextView back=label("‹   BACK",17,0xFFF0364E);root.addView(back);back.setOnClickListener(v->finish());
    ScrollView sc=new ScrollView(this);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
    LinearLayout body=new LinearLayout(this);body.setGravity(Gravity.CENTER_HORIZONTAL);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(0,d(10),0,d(10));sc.addView(body);
    ImageView logo=new ImageView(this);logo.setImageResource(R.drawable.brix_bl_logo);body.addView(logo,new LinearLayout.LayoutParams(d(100),d(100)));
    TextView name=label("BRIX LAUNCHER",30,Color.WHITE);name.setGravity(Gravity.CENTER);body.addView(name);
    TextView by=label("Made by: BrixTools",18,0xFFFF465B);by.setGravity(Gravity.CENTER);body.addView(by);
    TextView desc=label("Custom Brix launcher UI • Android Java Edition workspace",15,0xFFB5BAC5);desc.setPadding(0,d(14),0,d(14));desc.setGravity(Gravity.CENTER);body.addView(desc);
    TextView source=label("Brix website: brix4earn.com",16,Color.WHITE);source.setGravity(Gravity.CENTER);body.addView(source);
    TextView upstream=label("Open-source base: MojoLauncher and its upstream contributors. The Brix visual redesign is not a claim of authorship of the underlying game runtime. License notices must remain with redistributed builds.",13,0xFFAEB2BF);
    upstream.setPadding(d(10),d(20),d(10),0);upstream.setGravity(Gravity.CENTER);body.addView(upstream);
    setContentView(root);
  }
}
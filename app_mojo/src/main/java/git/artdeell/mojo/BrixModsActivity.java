package git.artdeell.mojo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;

/** Real Modrinth live catalog, no fake mod entries or automatic incompatible jar installs. */
public final class BrixModsActivity extends BaseActivity {
    private WebView web;
    private int dp(int v){return (int)(getResources().getDisplayMetrics().density*v+.5f);}
    private Button action(LinearLayout row,String text,Runnable run){
        Button b=new Button(this);b.setText(text);b.setAllCaps(false);
        b.setTextColor(getResources().getColor(R.color.brix_text));
        b.setTextSize(12);b.setBackgroundResource(R.drawable.brix_final_tile);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(47),1);lp.rightMargin=dp(6);
        row.addView(b,lp);b.setOnClickListener(v->run.run());return b;
    }
    private void page(String category){
        // Website itself is authoritative about versions, licenses, loaders and install availability.
        String url;
        switch(category){
            case "fps":url="https://modrinth.com/mods?g=categories:performance";break;
            case "utility":url="https://modrinth.com/mods?g=categories:utility";break;
            case "modpacks":url="https://modrinth.com/modpacks";break;
            default:url="https://modrinth.com/mods";
        }
        web.loadUrl(url);
    }
    @SuppressLint("SetJavaScriptEnabled") @Override protected void onCreate(@Nullable Bundle b){
        super.onCreate(b);
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(12),dp(8),dp(12),dp(8));root.setBackgroundColor(getResources().getColor(R.color.brix_page));
        LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);root.addView(header);
        TextView label=new TextView(this);label.setText("⚡  FPS / USEFUL MODS");label.setTextSize(19);
        label.setTypeface(null,1);label.setTextColor(getResources().getColor(R.color.brix_text));
        header.addView(label,new LinearLayout.LayoutParams(0,dp(44),1));
        Button back=new Button(this);back.setText("BACK");back.setAllCaps(false);back.setTextColor(Color.WHITE);
        back.setBackgroundResource(R.drawable.brix_final_primary);header.addView(back);back.setOnClickListener(v->finish());
        LinearLayout categories=new LinearLayout(this);root.addView(categories);
        action(categories,"FPS",()->page("fps"));action(categories,"UTILITY",()->page("utility"));
        action(categories,"ALL MODS",()->page("all"));action(categories,"MODPACKS",()->page("modpacks"));
        TextView info=new TextView(this);info.setText("Live Modrinth catalog • Check game version and Fabric/Forge/NeoForge support before installing.");
        info.setTextColor(getResources().getColor(R.color.brix_subtext));info.setTextSize(12);info.setPadding(0,dp(7),0,dp(7));root.addView(info);
        web=new WebView(this);web.setBackgroundColor(Color.WHITE);
        WebSettings settings=web.getSettings();settings.setJavaScriptEnabled(true);settings.setDomStorageEnabled(true);
        web.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView view,String url){
                if(url!=null&&(url.startsWith("https://modrinth.com/")||url.startsWith("https://www.modrinth.com/")))return false;
                if(url!=null&&url.startsWith("https://")){startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));return true;}
                return true;
            }
        });
        root.addView(web,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);page("fps");
    }
    @Override public void onBackPressed(){if(web!=null&&web.canGoBack())web.goBack();else super.onBackPressed();}
    @Override protected void onDestroy(){if(web!=null)web.destroy();super.onDestroy();}
}

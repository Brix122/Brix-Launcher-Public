package git.artdeell.mojo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class CreditsActivity extends BaseActivity {
    @Override
    public boolean setFullscreen() {
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        webView.setBackgroundColor(Color.parseColor("#050608"));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        String html = "<!doctype html><html><head><meta name='viewport' content='width=device-width, initial-scale=1' />"
                + "<style>body{background:#050608;color:#fff;font-family:sans-serif;padding:24px;}"
                + ".card{background:#111317;border:1px solid #252932;border-radius:22px;padding:22px;max-width:860px;margin:0 auto;}"
                + "h1{margin:0 0 8px 0;color:#ff2234;font-size:28px;}h2{margin-top:18px;color:#fff;font-size:20px;}"
                + "p,li{color:#b8beca;line-height:1.6;font-size:15px;}a{color:#ff4958;text-decoration:none;}"
                + ".badge{display:inline-block;background:#1b1f27;color:#fff;border:1px solid #2e3442;padding:6px 12px;border-radius:999px;margin-bottom:10px;}"
                + "</style></head><body><div class='card'>"
                + "<div class='badge'>BRIX LAUNCHER CREDITS</div>"
                + "<h1>Made by :- BrixTools</h1>"
                + "<p>Official source branding, premium launcher styling and custom BRIX UI patch build.</p>"
                + "<h2>Credits</h2>"
                + "<ul><li>Launcher name: Brix Launcher</li><li>Custom patch / redesign: BrixTools</li><li>Premium dark UI inspired workflow</li></ul>"
                + "<h2>Official Source</h2>"
                + "<p><a href='https://brix4earn.com'>https://brix4earn.com</a></p>"
                + "<h2>Notes</h2>"
                + "<p>This page is the in-app credits webpage requested from Quick Actions.</p>"
                + "</div></body></html>";
        webView.loadDataWithBaseURL("https://brix4earn.com", html, "text/html", "utf-8", null);
        setContentView(webView);
    }
}
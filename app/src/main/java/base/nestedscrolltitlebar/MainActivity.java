package base.nestedscrolltitlebar;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final String webUrl = "http://m.bilibili.com";
    WebView wvWeb;
    TextView tvWebTitle, tvWebUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        wvWeb = (WebView) findViewById(R.id.wvWeb);
        initView();
        initWeb();
        wvWeb.loadUrl(webUrl);
    }

    private void initView() {
        tvWebTitle = (TextView) findViewById(R.id.tvWebTitle);
        tvWebUrl = (TextView) findViewById(R.id.tvWebUrl);
    }

    private void initWeb() {
        wvWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        wvWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                initTitle(title);
                super.onReceivedTitle(view, title);
            }
        });
    }

    private void initTitle(String title) {
        tvWebTitle.setText(title);
        tvWebUrl.setText(wvWeb.getUrl());
    }
}

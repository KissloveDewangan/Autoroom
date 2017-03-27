package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import in.co.opensoftlab.yourstore.R;

/**
 * Created by dewangankisslove on 25-03-2017.
 */

public class Feedback extends AppCompatActivity {

    WebView web;

    public boolean checkinternetservice() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        web = (WebView) findViewById(R.id.webView3);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        if (checkinternetservice())
            web.loadUrl("https://kisslove.typeform.com/to/CyUy0q");
        else {
            Toast.makeText(getApplicationContext(), "Please connect to the Internet!", Toast.LENGTH_SHORT).show();
        }

        web.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }
}
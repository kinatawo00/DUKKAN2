package com.farsca.kartlar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Thin shell: the whole app lives in assets/www (page, fonts, recorded audio)
 * and runs offline inside this WebView.
 */
public class MainActivity extends Activity {

    private static final String PAGE = "file:///android_asset/www/index.html";

    private WebView web;
    private final Handler main = new Handler(Looper.getMainLooper());

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean night = (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        int startBg = night ? Color.parseColor("#171512") : Color.parseColor("#F5F0E3");
        getWindow().getDecorView().setBackgroundColor(startBg);
        paintBars(startBg, startBg);

        web = new WebView(this);
        web.setBackgroundColor(startBg);
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        web.setVerticalScrollBarEnabled(false);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setTextZoom(100);
        s.setSupportZoom(false);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (uri != null && uri.toString().startsWith("file:///android_asset/")) {
                    return false;
                }
                if (uri != null && ("https".equals(uri.getScheme()) || "http".equals(uri.getScheme()))) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    } catch (Exception ignored) {
                        // no browser installed
                    }
                }
                return true;
            }
        });
        web.addJavascriptInterface(new Bridge(), "Android");
        setContentView(web);
        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState);
        } else {
            web.loadUrl(PAGE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        // Let the page close the settings sheet or step back to Kartlar first.
        web.evaluateJavascript(
                "(window.onAndroidBack && window.onAndroidBack()) ? 'y' : 'n'",
                value -> {
                    if (value == null || !value.contains("y")) {
                        finish();
                    }
                });
    }

    @Override
    protected void onPause() {
        web.evaluateJavascript("document.querySelectorAll('audio').forEach(a=>a.pause())", null);
        super.onPause();
    }

    private void paintBars(int status, int nav) {
        getWindow().setStatusBarColor(status);
        getWindow().setNavigationBarColor(nav);
        View decor = getWindow().getDecorView();
        int flags = decor.getSystemUiVisibility();
        flags = Color.luminance(status) > 0.5f
                ? flags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                : flags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        flags = Color.luminance(nav) > 0.5f
                ? flags | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                : flags & ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        decor.setSystemUiVisibility(flags);
    }

    private class Bridge {
        /** Paints the system bars to match the page; icons flip with the colour's brightness. */
        @JavascriptInterface
        public void setBars(final String top, final String bottom) {
            main.post(() -> {
                try {
                    int status = Color.parseColor(top);
                    int nav = Color.parseColor(bottom);
                    paintBars(status, nav);
                    web.setBackgroundColor(status);
                } catch (IllegalArgumentException ignored) {
                    // bad colour string from the page: keep the current bars
                }
            });
        }
    }
}

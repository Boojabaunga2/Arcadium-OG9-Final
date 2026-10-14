package com.arcadium.og9;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.net.Uri;
import java.io.InputStream;
import java.net.URLConnection;

public final class MainActivity extends Activity {
    private WebView web;
    private volatile int held = 0;
    private static final String HOST = "appassets.androidplatform.net";
    // Bit positions are fixed in assets/controller.js.
    private static int bit(int code) {
        switch (code) {
            case KeyEvent.KEYCODE_DPAD_UP: return 0;
            case KeyEvent.KEYCODE_DPAD_DOWN: return 1;
            case KeyEvent.KEYCODE_DPAD_LEFT: return 2;
            case KeyEvent.KEYCODE_DPAD_RIGHT: return 3;
            case KeyEvent.KEYCODE_BUTTON_A: return 4;
            case KeyEvent.KEYCODE_BUTTON_B: return 5;
            case KeyEvent.KEYCODE_BUTTON_X: return 6;
            case KeyEvent.KEYCODE_BUTTON_Y: return 7;
            case KeyEvent.KEYCODE_BUTTON_START: return 8;
            case KeyEvent.KEYCODE_BUTTON_SELECT: return 9;
            case KeyEvent.KEYCODE_BUTTON_L1: return 10;
            case KeyEvent.KEYCODE_BUTTON_R1: return 11;
            default: return -1;
        }
    }
    public class ButtonBridge {
        @JavascriptInterface public int getHeld() { return held; }
    }
    private void fullscreen() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
    @Override public void onCreate(Bundle state) {
        super.onCreate(state); fullscreen();
        web = new WebView(this); web.setBackgroundColor(0xff030815);
        web.setFocusable(true); web.setFocusableInTouchMode(true);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true);
        s.setAllowFileAccess(false); s.setAllowContentAccess(false);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setUseWideViewPort(true); s.setLoadWithOverviewMode(true);
        web.addJavascriptInterface(new ButtonBridge(), "ArcadiumNative");
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Uri u = request.getUrl();
                if (!"https".equals(u.getScheme()) || !HOST.equals(u.getHost()))
                    return super.shouldInterceptRequest(view, request);
                try {
                    String path = u.getEncodedPath();
                    if (path == null || "/".equals(path)) path = "/index.html";
                    path = Uri.decode(path.substring(1));
                    if (path.startsWith("/") || path.contains("..") || path.contains("\\")) return null;
                    InputStream input = getAssets().open(path);
                    String mime = URLConnection.guessContentTypeFromName(path);
                    if (path.endsWith(".html")) mime = "text/html";
                    if (path.endsWith(".js")) mime = "application/javascript";
                    if (mime == null) mime = "application/octet-stream";
                    return new WebResourceResponse(mime, "UTF-8", input);
                } catch (Exception ex) { return null; }
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
                Uri uri = req.getUrl();
                return !("https".equals(uri.getScheme()) && HOST.equals(uri.getHost()));
            }
        });
        setContentView(web);
        web.loadUrl("https://" + HOST + "/index.html");
        web.requestFocus();
    }
    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        int index = bit(event.getKeyCode());
        if (index >= 0) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) held |= (1 << index);
            else if (event.getAction() == KeyEvent.ACTION_UP) held &= ~(1 << index);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    @Override public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        if (focus) fullscreen(); else held = 0;
    }
    @Override public void onBackPressed() {
        web.evaluateJavascript("(function(){var g=document.getElementById('gameView');if(g&&g.style.display==='flex'){if(typeof back==='function')back();}else history.back();})()",null);
    }
    @Override protected void onDestroy() {
        held = 0;
        if (web != null) { web.destroy(); web = null; }
        super.onDestroy();
    }
}

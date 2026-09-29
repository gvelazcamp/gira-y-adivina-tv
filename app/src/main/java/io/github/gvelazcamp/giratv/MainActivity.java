package io.github.gvelazcamp.giratv;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** TV screen only. Players send answers from their phones. No Javascript bridge. */
public final class MainActivity extends Activity {
    private WebView web;
    private boolean errorVisible;
    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        openGame();
    }
    private void openGame() {
        if (web != null) { web.destroy(); web = null; }
        errorVisible = false;
        web = new WebView(this);
        web.setBackgroundColor(Color.rgb(22,11,36));
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setSupportMultipleWindows(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return !allowed(request.getUrl());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return !allowed(Uri.parse(url));
            }
            @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) showError();
            }
            @Override public void onReceivedHttpError(WebView view, WebResourceRequest request,
                    android.webkit.WebResourceResponse response) {
                if (request.isForMainFrame()) showError();
            }
            @Override public void onPageFinished(WebView view, String url) { view.requestFocus(); }
        });
        setContentView(web);
        web.requestFocus();
        web.loadUrl(BuildConfig.GAME_URL);
    }
    private boolean allowed(Uri uri) {
        Uri game = Uri.parse(BuildConfig.GAME_URL);
        String path = uri.getPath();
        return "https".equals(uri.getScheme()) && game.getHost().equals(uri.getHost())
            && path != null && path.startsWith("/gira-y-adivina-rioplatense/");
    }
    private void showError() {
        if (errorVisible || isFinishing()) return;
        errorVisible = true;
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(64,64,64,64);
        box.setBackgroundColor(Color.rgb(22,11,36));
        TextView message = new TextView(this);
        message.setText("No pudimos abrir Sala TV. Revisá la conexión a internet y volvé a intentar.");
        message.setTextColor(Color.WHITE); message.setTextSize(24);
        box.addView(message);
        Button retry = new Button(this); retry.setText("Volver a intentar");
        retry.setOnClickListener(v -> openGame()); box.addView(retry);
        Button exit = new Button(this); exit.setText("Salir");
        exit.setOnClickListener(v -> finish()); box.addView(exit);
        setContentView(box); retry.requestFocus();
    }
    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        if (!errorVisible && web != null && event.getAction() == KeyEvent.ACTION_DOWN) {
            String direction = switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_UP -> "up";
                case KeyEvent.KEYCODE_DPAD_DOWN -> "down";
                case KeyEvent.KEYCODE_DPAD_LEFT -> "left";
                case KeyEvent.KEYCODE_DPAD_RIGHT -> "right";
                case KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> "select";
                default -> null;
            };
            if (direction != null) {
                web.evaluateJavascript("window.gyaTVRemote && window.gyaTVRemote('" + direction + "')", null);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
    @Override public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Sala TV")
            .setMessage("Salir cerrará la sala y la partida actual.")
            .setPositiveButton("Seguir jugando", (d,w) -> { if(web != null) web.requestFocus(); })
            .setNeutralButton("Nueva sala", (d,w) -> openGame())
            .setNegativeButton("Salir", (d,w) -> finish()).show();
    }
    @Override protected void onPause() { super.onPause(); if(web != null) web.onPause(); }
    @Override protected void onResume() { super.onResume(); if(web != null) web.onResume(); }
    @Override protected void onDestroy() { if(web != null) { web.destroy(); web = null; } super.onDestroy(); }
}

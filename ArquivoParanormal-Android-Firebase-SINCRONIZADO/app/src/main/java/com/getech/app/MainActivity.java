package com.getech.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private PermissionRequest pendingCamera;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setAllowFileAccessFromFileURLs(true);
        s.setAllowUniversalAccessFromFileURLs(true);
        s.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                String u=r.getUrl().toString();
                return !u.startsWith("file:///android_asset/") && !u.startsWith("https://") && !u.startsWith("http://");
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onPermissionRequest(final PermissionRequest req) {
                runOnUiThread(() -> {
                    boolean camera=false;
                    for(String r:req.getResources()) if(PermissionRequest.RESOURCE_VIDEO_CAPTURE.equals(r)) camera=true;
                    if(camera && checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                        pendingCamera=req; requestPermissions(new String[]{Manifest.permission.CAMERA},1001);
                    } else req.grant(req.getResources());
                });
            }
        });

        webView.loadUrl("file:///android_asset/site/public/pages/index.html");

        getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true){
            @Override public void handleOnBackPressed(){ if(webView.canGoBack()) webView.goBack(); else finish(); }
        });
    }

    @Override public void onRequestPermissionsResult(int c,String[] p,int[] g){
        super.onRequestPermissionsResult(c,p,g);
        if(c==1001 && pendingCamera!=null){
            if(g.length>0 && g[0]==PackageManager.PERMISSION_GRANTED) pendingCamera.grant(pendingCamera.getResources());
            else pendingCamera.deny();
            pendingCamera=null;
        }
    }
}

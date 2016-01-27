package com.example.acer.myrecycleviewtext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class VideoPlay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*WebView webView = (WebView) findViewById(R.id.id_webview);
        Intent intent  = getIntent();
        if(intent!=null){
            Bundle b = intent.getExtras();
            if(b.containsKey("weiboId")){
                String weiboId = b.getString("weiboId");
            }
        }
        //WebView加载web资源
        webView.loadUrl(url);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true;
            }
        });*/
    }
}

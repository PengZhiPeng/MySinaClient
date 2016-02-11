package com.example.acer.myrecycleviewtext.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.acer.myrecycleviewtext.AccessTokenKeeper;
import com.example.acer.myrecycleviewtext.Constants;
import com.example.acer.myrecycleviewtext.R;
import com.example.acer.myrecycleviewtext.bean.UrlsTransfer;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.ShortUrlAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VideoPlay extends AppCompatActivity {
    private static final String TAG = VideoPlay.class.getName();
    //当前 Token 信息
    private Oauth2AccessToken mAccessToken;
    private String url_long;
    private WebView webView;
    private FrameLayout video_fullView;// 全屏时视频加载view
    private View xCustomView;
    private WebChromeClient.CustomViewCallback xCustomViewCallback;
    private myWebChromeClient myWebChromeClient;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setMessage("正在加载...");
        dialog.show();

        video_fullView = (FrameLayout) findViewById(R.id.video_fullView);

        initWebView();

//      获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        Intent intent = getIntent();
        if (intent != null) {
            String[] url = {intent.getStringExtra("url")};
            ShortUrlAPI shortUrlAPI = new ShortUrlAPI(this, Constants.APP_KEY, mAccessToken);
            if (mAccessToken != null && mAccessToken.isSessionValid())
                shortUrlAPI.expand(url, mListener);
        }
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.id_webview);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (dialog.isShowing())
                    dialog.cancel();
            }
        });
        myWebChromeClient = new myWebChromeClient();
        webView.setWebChromeClient(myWebChromeClient);//自定义

        WebSettings ws = webView.getSettings();
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕
        ws.setLoadWithOverviewMode(true);
        ws.setJavaScriptEnabled(true);
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            if (!TextUtils.isEmpty(s)) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.optJSONArray("urls");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        int length = jsonArray.length();
                        for (int ix = 0; ix < length; ix++) {
                            UrlsTransfer urlsTransfer = UrlsTransfer.parse(jsonArray.getJSONObject(ix));
                            if (urlsTransfer.isResult()) {
                                url_long = urlsTransfer.getUrl_long();
//                                Log.i("urls", url_long);
                                //WebView加载web资源
                                webView.loadUrl(url_long);

                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(VideoPlay.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        webView.resumeTimers();
//      设置为横屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
        webView.pauseTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        video_fullView.removeAllViews();
        webView.destroy();
    }

    public class myWebChromeClient extends WebChromeClient {
        // 播放网络视频时全屏会被调用的方法
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            webView.setVisibility(View.INVISIBLE);
            // 如果一个视图已经存在，那么立刻终止并新建一个
            if (xCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            video_fullView.addView(view);
            xCustomView = view;
            xCustomViewCallback = callback;
            video_fullView.setVisibility(View.VISIBLE);
        }

        // 视频播放退出全屏会被调用的
        @Override
        public void onHideCustomView() {
            if (xCustomView == null)// 不是全屏播放状态
                return;

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            xCustomView.setVisibility(View.GONE);
            video_fullView.removeView(xCustomView);
            xCustomView = null;
            video_fullView.setVisibility(View.GONE);
            xCustomViewCallback.onCustomViewHidden();
            webView.setVisibility(View.VISIBLE);
        }

        // 视频加载时进程loading
        @Override
        public View getVideoLoadingProgressView() {
            return null;
        }
    }

    public boolean inCustomView() {
        return (xCustomView != null);
    }

    // 全屏时按返回键执行退出全屏的方法
    public void hideCustomView() {
        myWebChromeClient.onHideCustomView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inCustomView()) {
                hideCustomView();//退出全屏
                return true;
            }
            if (webView.canGoBack())
                webView.goBack();// goBack()表示返回WebView的上一页面
            else {
                webView.loadUrl("about:blank");
                VideoPlay.this.finish();
            }
        }
        return false;
    }
}

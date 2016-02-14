package com.example.acer.myrecycleviewtext.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.acer.myrecycleviewtext.AccessTokenKeeper;
import com.example.acer.myrecycleviewtext.Constants;
import com.example.acer.myrecycleviewtext.R;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;

/**
 * 发布新微博的页面
 * Created by acer on 2016/2/12.
 */
public class WriteWeiboActivity extends AppCompatActivity {

    //当前 Token 信息
    private Oauth2AccessToken mAccessToken;
    //用于获取微博信息流等操作的API
    private StatusesAPI mStatusesAPI;
    private String text;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_weibo);

        editText = (EditText) findViewById(R.id.id_new_weibo_edittext);

        findViewById(R.id.id_new_weibo_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText() != null) {
                    text = editText.getText().toString();
                    // 获取当前已保存过的 Token
                    mAccessToken = AccessTokenKeeper.readAccessToken(WriteWeiboActivity.this);
                    // 对statusAPI实例化
                    mStatusesAPI = new StatusesAPI(WriteWeiboActivity.this, Constants.APP_KEY, mAccessToken);
                    if (mAccessToken != null && mAccessToken.isSessionValid()) {
                        mStatusesAPI.update(text, null, null, mListener);
                    }
                }else {
                    Toast.makeText(WriteWeiboActivity.this, "发布内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.id_new_weibo_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            if (!TextUtils.isEmpty(s)) {
                if (s.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(s);
                    Toast.makeText(WriteWeiboActivity.this,
                            "发布微博成功, id = " + status.id,
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(WriteWeiboActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
}

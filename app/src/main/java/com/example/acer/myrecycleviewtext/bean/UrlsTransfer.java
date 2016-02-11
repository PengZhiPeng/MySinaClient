package com.example.acer.myrecycleviewtext.bean;

import org.json.JSONObject;

/**
 * 短链接转长链接
 * Created by acer on 2016/2/9.
 */
public class UrlsTransfer {
    //短链接
    private String url_short;
    //长链接
    private String url_long;
    //链接的类型，0：普通网页、1：视频、2：音乐、3：活动、5、投票
    private int type;
    //短链的可用状态，true：可用、false：不可用
    private boolean result;


    public static UrlsTransfer parse(JSONObject jsonObject){
        if (null == jsonObject) {
            return null;
        }
        UrlsTransfer urlsTransfer = new UrlsTransfer();
        urlsTransfer.setUrl_long(jsonObject.optString("url_long"));
        urlsTransfer.setUrl_short(jsonObject.optString("url_short"));
        urlsTransfer.setType(jsonObject.optInt("type"));
        urlsTransfer.setResult(jsonObject.optBoolean("result"));
        return urlsTransfer;
    }


    public String getUrl_short() {
        return url_short;
    }

    public void setUrl_short(String url_short) {
        this.url_short = url_short;
    }

    public String getUrl_long() {
        return url_long;
    }

    public void setUrl_long(String url_long) {
        this.url_long = url_long;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}

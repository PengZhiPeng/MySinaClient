package com.example.acer.myrecycleviewtext.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.example.acer.myrecycleviewtext.ui.VideoPlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理微博内容中的 @某人、#话题#、网址 变成高亮蓝色。
 * 使用SpannableString实现。
 * Created by acer on 2016/1/28.
 */
public class TextColorUtils {

    private static final int TYPE_TOPIC = 0, TYPE_NAMEH = 1, TYPE_URLH = 2;
    private static final String START = "start", END = "end", RESULT = "result";

    public static SpannableString atBlue(Context mContext, String weiboText) {
        //话题        # 任意字符 加号0次或1次 #
        final String TOPIC = "#.+?#";
        //@某人          @( [中文 大小写英文 数字 下划线_ 横杠-] 0个或多个 )
        final String NAMEH = "@([\u4e00-\u9fa5A-Za-z0-9_\\-]*)";
        //网址             [大小写英文] :// [非中文或空格或标点] 0个或多个
        final String URLH = "[a-zA-Z]+://[^\u4e00-\u9fa5\\s，。？：；‘’！“”—……、]*";
        SpannableString spannableString = new SpannableString(weiboText);

        heightLight(mContext, weiboText, TOPIC, spannableString, TYPE_TOPIC);
        heightLight(mContext, weiboText, NAMEH, spannableString, TYPE_NAMEH);
        heightLight(mContext, weiboText, URLH, spannableString, TYPE_URLH);
        return spannableString;
    }

    public static SpannableString heightLight(final Context mContext, String weiboText, String pattern,
                                              SpannableString spannableString, int type) {

        ArrayList<Map<String, String>> lists = getStartEndResult(weiboText, Pattern.compile(pattern));

        for (Map<String, String> str : lists) {
            ForegroundColorSpan span = new ForegroundColorSpan(Color.BLUE);
            spannableString.setSpan(span, Integer.parseInt(str.get(START)),
                    Integer.parseInt(str.get(END)), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        switch (type) {
            case TYPE_URLH:
                for (final Map<String, String> url : lists) {
                    ClickableSpan sp = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            Intent intent = new Intent(mContext, VideoPlay.class);
                            intent.putExtra("url", url.get(RESULT));
                            mContext.startActivity(intent);
                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(Color.BLUE);
                            ds.setUnderlineText(false);
                            ds.clearShadowLayer();
                        }
                    };
                    spannableString.setSpan(sp, Integer.parseInt(url.get(START)),
                            Integer.parseInt(url.get(END)), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                }
                break;
            case TYPE_TOPIC:

                break;
            case TYPE_NAMEH:

                break;
        }

        return spannableString;
    }

    private static ArrayList<Map<String, String>> getStartEndResult(String weiboText, Pattern pattern) {

        ArrayList<Map<String, String>> lists = new ArrayList<Map<String, String>>(0);
        //正则对象.匹配（数据源）
        Matcher matcher = pattern.matcher(weiboText);
        while (matcher.find()) {
            Map<String, String> map = new HashMap<String, String>(0);
            map.put(START, matcher.start() + "");
            map.put(END, matcher.end() + "");
            map.put(RESULT, matcher.group());
            lists.add(map);
        }
        return lists;
    }
}
package com.example.acer.myrecycleviewtext.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

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

    public static SpannableString atBlue(String weiboText) {
        //正则式
        final String TOPIC = "#.+?#";//话题
        final String NAMEH = "@([\u4e00-\u9fa5A-Za-z0-9_]*)";//@某人
        final String URLH = "[a-zA-z]+://[^\\s]*";//网址

        SpannableString spannableString = new SpannableString(weiboText);

        heightLight(weiboText,TOPIC, spannableString);
        heightLight(weiboText,NAMEH, spannableString);
        heightLight(weiboText,URLH, spannableString);
        return spannableString;
    }

    public static SpannableString heightLight(String weiboText,String pattern, SpannableString spannableString) {
        final String START = "start";
        final String END = "end";
        ArrayList<Map<String, String>> lists = getStartAndEnd(weiboText,Pattern.compile(pattern));

        for (Map<String, String> str : lists) {
            ForegroundColorSpan span = new ForegroundColorSpan(Color.BLUE);
            spannableString.setSpan(span, Integer.parseInt(str.get(START)),
                    Integer.parseInt(str.get(END)), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return spannableString;
    }

    private static ArrayList<Map<String, String>> getStartAndEnd(String weiboText,Pattern pattern) {
        final String START = "start";
        final String END = "end";

        ArrayList<Map<String, String>> lists = new ArrayList<Map<String, String>>(0);
        //正则对象.匹配（数据源）
        Matcher matcher = pattern.matcher(weiboText);
        while (matcher.find()) {
            Map<String, String> map = new HashMap<String, String>(0);
            map.put(START, matcher.start() + "");
            map.put(END, matcher.end() + "");
            lists.add(map);
        }
        return lists;
    }
}
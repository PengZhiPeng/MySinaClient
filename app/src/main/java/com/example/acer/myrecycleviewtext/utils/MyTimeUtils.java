package com.example.acer.myrecycleviewtext.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类：用于将解析到的时间数据转换成类似 “X分钟前”等格式。
 * Created by acer on 2016/1/10.
 */
public class MyTimeUtils {

    // 将微博的日期字符串转换为Date对象
    public static Date strToDate(String str) {
        // sample：Tue May 31 17:46:55 +0800 2011
        // E：周 MMM：字符串形式的月，如果只有两个M，表示数值形式的月 Z表示时区（＋0800）
        SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy",
                Locale.US);
        Date result = null;
        try {
            result = sdf.parse(str);
        } catch (Exception e) {

        }
        return result;
    }

    public static String getTimeStr(Date oldTime, Date currentDate) {
        long time1 = currentDate.getTime();

        long time2 = oldTime.getTime();

        long time = (time1 - time2) / 1000;

        if (time >= 0 && time < 60) {
            return "just now";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + " min ago";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + " hour ago";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.format(oldTime);
        }
    }


}

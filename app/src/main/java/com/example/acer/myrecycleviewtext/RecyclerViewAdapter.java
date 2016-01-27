package com.example.acer.myrecycleviewtext;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acer.myrecycleviewtext.utils.MyTimeUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by acer on 2016/1/6.
 */
public class RecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Status> mStatusList;
    private GridViewAdapter gridViewAdapter;

    public RecyclerViewAdapter(Context mContext, List<Status> mStatusList) {
        this.mContext = mContext;
        this.mStatusList = mStatusList;
    }

    public List<Status> getmDatas() {
        return mStatusList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weibo_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.holder_weibo_text.setText(mStatusList.get(position).getText());//微博内容
        //将标准时间转换成“几分钟前”格式
        holder.holder_created_time.setText(MyTimeUtils.getTimeStr(
                MyTimeUtils.strToDate(mStatusList.get(position).getCreated_at()), new Date()));
        holder.holder_name.setText(mStatusList.get(position).getUser().getScreen_name());//微博主名称
        //显示头像图片的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(mStatusList.get(position).getUser().getProfile_image_url(),
                holder.holder_profile_image, options);
        //微博来源
        String source = "From " + mStatusList.get(position).getTextSource();
        holder.holder_source.setText(source);
        if (mStatusList.get(position).getRetweeted_status() != null
                && mStatusList.get(position).getUser() != null) {
            holder.holder_retweeted_text.setVisibility(View.VISIBLE);
            //被转发的微博名称和内容
            String nameAndTest = "@" + mStatusList.get(position).getRetweeted_status().getUser().getName()
                    + " : " + mStatusList.get(position).getRetweeted_status().getText();
            holder.holder_retweeted_text.setText(nameAndTest);
        } else {
            holder.holder_retweeted_text.setVisibility(View.GONE);
        }
        if (mStatusList.get(position).getRetweeted_status() != null) {//有转发，则添加转发的配图
            gridViewAdapter = new GridViewAdapter(mContext,
                    mStatusList.get(position).getRetweeted_status().getPic_urls());
        } else {
            gridViewAdapter = new GridViewAdapter(mContext,
                    mStatusList.get(position).getPic_urls());//没转发，则添加原创微博的配图
        }
        holder.holder_gridview.setAdapter(gridViewAdapter);
        //用正则表达式提取微博内容Text中的网址:[a-zA-z]+://[^\s]*
        /*String weiboText = mStatusList.get(position).getText();
        String regEx = "[a-zA-z]+://[^\\s]*"; //网址
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(weiboText);
//        MediaController controller = new MediaController(mContext);
        if (mat.find()) {
            holder.holder_video.setVisibility(View.VISIBLE);
            Log.i("uri", mat.group());
            Uri uri = Uri.parse(mat.group());
            holder.holder_video.setVideoURI(uri);
//            holder.holder_video.setMediaController(controller);
//            controller.setMediaPlayer(holder.holder_video);
//            holder.holder_video.start();
            holder.holder_video.requestFocus();
            *//*controller.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });*//*
        } else {
            holder.holder_video.setVisibility(View.GONE);
        }*/
        String weiboText = mStatusList.get(position).getText();
        String regEx = "[a-zA-z]+://[^\\s]*"; //网址
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(weiboText);
        if (mat.find()) {
            holder.holder_webview.setVisibility(View.VISIBLE);
            Log.i("url", mat.group());
            //WebView加载web资源
            holder.holder_webview.loadUrl(mat.group());
            //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
            holder.holder_webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    view.loadUrl(url);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mStatusList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView holder_weibo_text, holder_created_time, holder_name, holder_source,
                holder_retweeted_text;
        ImageView holder_profile_image;
        MyGridView holder_gridview;
        //        VideoView holder_video;
        WebView holder_webview;

        public ViewHolder(View itemView) {
            super(itemView);
            holder_weibo_text = (TextView) itemView.findViewById(R.id.id_tv_status);
            holder_created_time = (TextView) itemView.findViewById(R.id.id_tv_timedate);
            holder_name = (TextView) itemView.findViewById(R.id.id_tv_userID);
            holder_profile_image = (ImageView) itemView.findViewById(R.id.id_iv_userIcon);
            holder_source = (TextView) itemView.findViewById(R.id.id_tv_from_source);
            holder_retweeted_text = (TextView) itemView.findViewById(R.id.id_tv_retweeted_info);
            holder_gridview = (MyGridView) itemView.findViewById(R.id.id_gridview);
//            holder_video = (VideoView) itemView.findViewById(R.id.id_videoview);
            holder_webview = (WebView) itemView.findViewById(R.id.id_webview);
        }
    }
}

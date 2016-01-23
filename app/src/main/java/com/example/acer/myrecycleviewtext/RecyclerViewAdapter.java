package com.example.acer.myrecycleviewtext;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acer.myrecycleviewtext.utils.MyTimeUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by acer on 2016/1/6.
 */
public class RecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mSinaTextDatas, mName, mprofile_image_url, mSource, mRetweetedText;
    private List<ArrayList<String>> mPic_urls;
    private List<Date> mTime;

    public RecyclerViewAdapter(Context mContext, List<String> mSinaTextDatas,
                               List<Date> mTime, List<String> mName,
                               List<String> mprofile_image_url, List<String> mSource,
                               List<String> mRetweetedText, List<ArrayList<String>> mPic_urls) {
        this.mContext = mContext;
        this.mSinaTextDatas = mSinaTextDatas;
        this.mTime = mTime;
        this.mName = mName;
        this.mprofile_image_url = mprofile_image_url;
        this.mSource = mSource;
        this.mRetweetedText = mRetweetedText;
        this.mPic_urls = mPic_urls;
    }

    public List<String> getmDatas() {
        return mSinaTextDatas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weibo_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.holder_weibo_text.setText(mSinaTextDatas.get(position));//微博内容
        //将标准时间转换成“几分钟前”格式
        holder.holder_created_time.setText(MyTimeUtils.getTimeStr(mTime.get(position), new Date()));
        holder.holder_name.setText(mName.get(position));//微博主名称
        //显示头像图片的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(mprofile_image_url.get(position),
                holder.holder_profile_image, options);
        holder.holder_source.setText("From " + mSource.get(position));//微博来源
        if (!mRetweetedText.get(position).equals("null")) {//如果是转发，显示转发名称+内容
            holder.holder_retweeted_text.setVisibility(View.VISIBLE);
            holder.holder_retweeted_text.setText(mRetweetedText.get(position));
        } else {//不是转发则隐藏view
            holder.holder_retweeted_text.setVisibility(View.GONE);
        }
        if (!mPic_urls.get(position).equals("MyNull")) {//有配图则用gridview显示
            GridViewAdapter gridViewAdapter = new GridViewAdapter(mContext,mPic_urls.get(position));
//            Log.i("pic","RecycleView--mPic_urls.get(p): "+mPic_urls.get(position));
            holder.holder_gridview.setAdapter(gridViewAdapter);
            //第三个参数true为快速滚动时停止加载图片。第二个为滚动时停止加载图片功能（false--不停止加载）。
//            holder.holder_gridview.setOnScrollListener(
//                    new PauseOnScrollListener(ImageLoader.getInstance(), false, false));
        }
    }

    @Override
    public int getItemCount() {
        return mSinaTextDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView holder_weibo_text, holder_created_time, holder_name, holder_source,
                holder_retweeted_text;
        ImageView holder_profile_image;
        MyGridView holder_gridview;

        public ViewHolder(View itemView) {
            super(itemView);
            holder_weibo_text = (TextView) itemView.findViewById(R.id.id_tv_status);
            holder_created_time = (TextView) itemView.findViewById(R.id.id_tv_timedate);
            holder_name = (TextView) itemView.findViewById(R.id.id_tv_userID);
            holder_profile_image = (ImageView) itemView.findViewById(R.id.id_iv_userIcon);
            holder_source = (TextView) itemView.findViewById(R.id.id_tv_from_source);
            holder_retweeted_text = (TextView) itemView.findViewById(R.id.id_tv_retweeted_info);
            holder_gridview = (MyGridView) itemView.findViewById(R.id.id_gridview);
        }
    }

}

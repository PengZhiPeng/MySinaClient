package com.example.acer.myrecycleviewtext.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acer.myrecycleviewtext.R;
import com.example.acer.myrecycleviewtext.utils.MyGridView;
import com.example.acer.myrecycleviewtext.utils.MyItemClickListener;
import com.example.acer.myrecycleviewtext.utils.MyTimeUtils;
import com.example.acer.myrecycleviewtext.utils.TextColorUtils;
import com.example.acer.myrecycleviewtext.utils.TextViewFixTouchConsume;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.Date;
import java.util.List;

/**
 * Created by acer on 2016/1/6.
 */
public class RecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Status> mStatusList;
    private GridViewAdapter gridViewAdapter;
    private MyItemClickListener mItemClickListener;

    public RecyclerViewAdapter(Context mContext, List<Status> mStatusList) {
        this.mContext = mContext;
        this.mStatusList = mStatusList;
    }

    public List<Status> getmDatas() {
        return mStatusList;
    }

    // 获取已经显示的微博的最小ID,即最后一条的微博（id越大，发的时间越晚，在越前面）
    public long getMinId() {
        if (mStatusList.size() > 0)
            return mStatusList.get(mStatusList.size() - 1).id;
        else
            return Long.MAX_VALUE;
    }

    @Override
    public int getItemCount() {
        return mStatusList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weibo_item, parent, false));
        return holder;
    }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//      微博正文内容（处理蓝色高亮部分）
        holder.holder_weibo_text.setText(TextColorUtils.atBlue(mContext, mStatusList.get(position).getText()));
        //为内容中的链接添加点击事件，点击事件具体在TextColorUtils类中。
        holder.holder_weibo_text.setMovementMethod(TextViewFixTouchConsume.localLinkMovementMethod.getsInstance());
        //设置点击后的背景颜色为灰色// FIXME: 2016/2/10 无效！！！
        holder.holder_weibo_text.setHighlightColor(Color.GRAY);
        //将标准时间转换成“几分钟前”格式
        holder.holder_created_time.setText(MyTimeUtils.getTimeStr(
                MyTimeUtils.strToDate(mStatusList.get(position).getCreated_at()), new Date()));
        holder.holder_name.setText(mStatusList.get(position).getUser().getScreen_name());//微博主名称
        //显示头像图片的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(100))
                .build();
        ImageLoader.getInstance().displayImage(mStatusList.get(position).getUser().getProfile_image_url(),
                holder.holder_profile_image, options);
        //微博来源
        String source = "From " + mStatusList.get(position).getTextSource();
        holder.holder_source.setText(source);
        if (mStatusList.get(position).getRetweeted_status() != null
                && mStatusList.get(position).getUser() != null) {
            holder.holder_retweeted_text.setVisibility(View.VISIBLE);
            //被转发的微博名称和内容（蓝色高亮处理）
            holder.holder_retweeted_text.setText(TextColorUtils.atBlue(mContext, "@" + mStatusList.get(position).getRetweeted_status().getUser().getName()
                    + " : " + mStatusList.get(position).getRetweeted_status().getText()));
            //为内容中的链接添加点击事件，点击事件具体在TextColorUtils类中。
            holder.holder_retweeted_text.setMovementMethod(TextViewFixTouchConsume.localLinkMovementMethod.getsInstance());
        } else {
            holder.holder_retweeted_text.setVisibility(View.GONE);
        }
        if (mStatusList.get(position).getRetweeted_status() != null) {//有转发，则添加转发的配图
            gridViewAdapter = new GridViewAdapter(mContext,
                    mStatusList.get(position).getRetweeted_status().getHighPicUrls());
        } else {
            gridViewAdapter = new GridViewAdapter(mContext,
                    mStatusList.get(position).getHighPicUrls());//没转发，则添加原创微博的配图
        }
        holder.holder_gridview.setAdapter(gridViewAdapter);

        // 如果设置了回调，则设置点击事件
        if (mItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    mItemClickListener.onItemClick(holder.itemView, pos);
                }
            });
        }

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

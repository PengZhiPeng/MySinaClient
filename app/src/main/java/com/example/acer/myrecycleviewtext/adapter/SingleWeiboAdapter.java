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
import com.example.acer.myrecycleviewtext.utils.MyTimeUtils;
import com.example.acer.myrecycleviewtext.utils.TextColorUtils;
import com.example.acer.myrecycleviewtext.utils.TextViewFixTouchConsume;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.Date;
import java.util.List;

/**
 * 显示单条微博的recycleview Adapter
 * 将recycleview分为两部分：1，header显示微博；2，item显示评论。
 * Created by acer on 2016/2/3.
 */
// FIXME: 2016/2/6 没有评论时，整个页面无显示内容，headerview加载不出来！

public class SingleWeiboAdapter extends RecyclerView.Adapter<SingleWeiboAdapter.SingleWeiboViewHolder> {
    private static final int TYPE_HEADER = 0, TYPE_ITEM = 1;

    private View mHeaderView;
    private Context mContext;
    private Status mStatus;
    private List<Comment> mCommentsList;
    private GridViewAdapter gridViewAdapter;
    private int headerSize = 1;

    public SingleWeiboAdapter(Context mContext, Status mStatus,
                              List<Comment> mCommentsList) {
        this.mContext = mContext;
        this.mStatus = mStatus;
        this.mCommentsList = mCommentsList;
    }

    @Override
    public int getItemCount() {
        return headerSize + mCommentsList.size();
    }

    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public List<Comment> getCommentDatas() {
        return mCommentsList;
    }

    // 获取已经显示的评论的最小ID,即最后一条的评论（id越大，发的时间越晚，在越前面）
    public long getMinId() {
        if (mCommentsList.size() > 0)
            return mCommentsList.get(mCommentsList.size() - 1).id;
        else
            return Long.MAX_VALUE;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_ITEM;
        if (position == 0) return TYPE_HEADER;
        return TYPE_ITEM;
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public SingleWeiboAdapter.SingleWeiboViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != TYPE_HEADER) { //position不是头布局（微博）就显示item布局（评论）
            return new SingleWeiboViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_show_single_weibo_comments, parent, false));
        }
        return new SingleWeiboViewHolder(mHeaderView);
    }

    @Override
    public void onBindViewHolder(SingleWeiboViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            //显示头像图片的配置
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            ImageLoader.getInstance().displayImage(mStatus.getUser().getProfile_image_url(),
                    holder.profileImage, options);
            holder.userName.setText(mStatus.getUser().getScreen_name());
            holder.creatAt.setText(MyTimeUtils.getTimeStr(
                    MyTimeUtils.strToDate(mStatus.getCreated_at()), new Date()));
            holder.source.setText("From " + mStatus.getTextSource());
            holder.statusText.setText(TextColorUtils.atBlue(mContext, mStatus.getText()));
            //为内容中的链接添加点击事件，点击事件具体在TextColorUtils类中。
            holder.statusText.setMovementMethod(TextViewFixTouchConsume
                    .localLinkMovementMethod.getsInstance());
            //设置点击后的背景颜色为灰色// FIXME: 2016/2/10 无效！！！
            holder.statusText.setHighlightColor(Color.GRAY);

            if (mStatus.getRetweeted_status() != null && mStatus.getUser() != null) {
                holder.retweetText.setVisibility(View.VISIBLE);
                holder.retweetText.setText(TextColorUtils.atBlue(mContext, "@" + mStatus.getRetweeted_status().getUser().getName()
                        + " : " + mStatus.getRetweeted_status().getText()));
                holder.retweetText.setMovementMethod(TextViewFixTouchConsume
                        .localLinkMovementMethod.getsInstance());
            } else {
                holder.retweetText.setVisibility(View.GONE);
            }
            if (mStatus.getRetweeted_status() != null) {//有转发，则添加转发的配图
                gridViewAdapter = new GridViewAdapter(mContext,
                        mStatus.getRetweeted_status().getHighPicUrls());
            } else {
                gridViewAdapter = new GridViewAdapter(mContext,
                        mStatus.getHighPicUrls());//没转发，则添加原创微博的配图
            }
            holder.pics.setAdapter(gridViewAdapter);
            holder.retweetNum.setText("转发 " + mStatus.reposts_count);
            holder.commentNum.setText("评论 " + mStatus.comments_count);
            holder.likeNum.setText("赞 " + mStatus.attitudes_count);

        } else {//评论\转发\赞 部分
            if (mCommentsList == null) {
                holder.comment_noComment.setVisibility(View.VISIBLE);
            } else {
                int pos = getRealPosition(holder);//有headerview，故position实际上要减1

                holder.comment_noComment.setVisibility(View.GONE);

                holder.comment_creatAt.setText(MyTimeUtils.getTimeStr(MyTimeUtils
                        .strToDate(mCommentsList.get(pos).getCreated_at()), new Date()));
                holder.comment_userName.setText(mCommentsList.get(pos).getUser().getScreen_name());
                holder.comment_text.setText(TextColorUtils.atBlue(mContext,mCommentsList.get(pos).getText()));
                //显示评论头像图片的配置
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
                ImageLoader.getInstance().displayImage(mCommentsList.get(pos).getUser().getProfile_image_url(),
                        holder.comment_profileImage, options);
            }
        }
    }

    public class SingleWeiboViewHolder extends RecyclerView.ViewHolder {

        TextView statusText, creatAt, userName, source, retweetText;
        TextView retweetNum, commentNum, likeNum;
        ImageView profileImage;
        MyGridView pics;

        TextView comment_creatAt, comment_userName, comment_text, comment_noComment;
        ImageView comment_profileImage;

        public SingleWeiboViewHolder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) {
                profileImage = (ImageView) itemView.findViewById(R.id.id_singleweibo_iv_userIcon);
                userName = (TextView) itemView.findViewById(R.id.id_singleweibo_tv_userID);
                creatAt = (TextView) itemView.findViewById(R.id.id_singleweibo_tv_timedate);
                source = (TextView) itemView.findViewById(R.id.id_singleweibo_tv_from_source);
                statusText = (TextView) itemView.findViewById(R.id.id_singleweibo_tv_status);
                retweetText = (TextView) itemView.findViewById(R.id.id_singleweibo_tv_retweeted_info);
                pics = (MyGridView) itemView.findViewById(R.id.id_singleweibo_gridview);
                retweetNum = (TextView) itemView.findViewById(R.id.id_singleweibo_tv_retweeted_numbers);
                commentNum = (TextView) itemView.findViewById(R.id.id_singleweibo_tv_comment_numbers);
                likeNum = (TextView) itemView.findViewById(R.id.id_singleweibo_tv_like_numbers);
            } else {
                comment_creatAt = (TextView) itemView.findViewById(R.id.id_comments_tv_timedate);
                comment_userName = (TextView) itemView.findViewById(R.id.id_comments_tv_userID);
                comment_text = (TextView) itemView.findViewById(R.id.id_comments_tv_text);
                comment_profileImage = (ImageView) itemView.findViewById(R.id.id_comments_iv_userIcon);
                comment_noComment = (TextView) itemView.findViewById(R.id.id_comments_tv_nocomment);
            }
        }
    }
}

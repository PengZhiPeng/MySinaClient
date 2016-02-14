package com.example.acer.myrecycleviewtext.ui;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.acer.myrecycleviewtext.AccessTokenKeeper;
import com.example.acer.myrecycleviewtext.Constants;
import com.example.acer.myrecycleviewtext.R;
import com.example.acer.myrecycleviewtext.adapter.SingleWeiboAdapter;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.utils.LogUtil;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

/**
 * 显示单条微博的Activity，接收微博浏览时的item微博点击事件传来的某条具体微博信息对象Status，
 * 及异步加载评论列表集合，传入SingleWeiboAdapter中加载显示。
 * Created by acer on 2016/2/3.
 */
public class ShowSingleWeibo extends AppCompatActivity
        implements PullLoadMoreRecyclerView.PullLoadMoreListener {

    private static final String TAG = ShowSingleWeibo.class.getName();
    private CoordinatorLayout container;
    //接收微博页面传来的status对象，必须声明为static，使用intent传递status就不是原来的status了
    public static Status status;

    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private SingleWeiboAdapter mSingleWeiboAdapter = null;

    //用于获取评论信息流等操作的API
    private CommentsAPI mCommentsAPI;
    //当前 Token 信息
    private Oauth2AccessToken mAccessToken;
    private View headerView;
    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_single_weibo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        container = (CoordinatorLayout) findViewById(R.id.id_single_container);
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) findViewById(R.id.id_show_single_weibo_recycleview);
        //设置线性布局
        mPullLoadMoreRecyclerView.setLinearLayout();
        mPullLoadMoreRecyclerView.setRefreshing(true);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);

        //整个recycleview布局为：headerView显示微博，itemview显示评论
        headerView = LayoutInflater.from(this).
                inflate(R.layout.activity_show_single_weibo_header,
                        mPullLoadMoreRecyclerView, false);
        loadNewComments();
    }

    private void loadNewComments() {
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        mCommentsAPI = new CommentsAPI(this, Constants.APP_KEY, mAccessToken);
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mCommentsAPI.show(status.id, 0, 0, 10, mPage, 0, mListener);
        }
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
            if (!TextUtils.isEmpty(response)) {
                Log.i("json", response);
                if (response.startsWith("{\"comments\"")) {
                    // 调用 CommentList#parse 解析字符串成评论列表对象
                    final CommentList comment = CommentList.parse(response);
                    if (comment != null && comment.total_number > 0) {
                        if (mSingleWeiboAdapter == null) {
                            mSingleWeiboAdapter = new SingleWeiboAdapter(ShowSingleWeibo.this
                                    , status, comment.commentList);
                            mSingleWeiboAdapter.setHeaderView(headerView);
                            mPullLoadMoreRecyclerView.setAdapter(mSingleWeiboAdapter);
                        } else {
                            if (mPage == 1) {//刷新回到顶部
                                mSingleWeiboAdapter.getCommentDatas().clear();
                            }
                            if (comment.commentList != null) {//加载更多
                                mSingleWeiboAdapter.getCommentDatas().addAll(comment.commentList);
                                mSingleWeiboAdapter.notifyDataSetChanged();
                            }else {//无更多显示snackbar提示
                                Snackbar.make(container, "已无更多评论", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(ShowSingleWeibo.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onRefresh() {
        mPage = 1;
        loadNewComments();
    }

    @Override
    public void onLoadMore() {
        mPage += 1;
        long maxId = mSingleWeiboAdapter.getMinId();
        mCommentsAPI.show(status.id, 0, maxId, 10, mPage, 0, mListener);
    }
}

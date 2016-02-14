package com.example.acer.myrecycleviewtext.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.acer.myrecycleviewtext.AccessTokenKeeper;
import com.example.acer.myrecycleviewtext.Constants;
import com.example.acer.myrecycleviewtext.R;
import com.example.acer.myrecycleviewtext.adapter.RecyclerViewAdapter;
import com.example.acer.myrecycleviewtext.utils.MyItemClickListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

/**
 * 浏览微博页面（recycleview）
 * Created by acer on 2016/1/5.
 */
public class WeiboMainFragemnt extends Fragment implements View.OnClickListener,
        PullLoadMoreRecyclerView.PullLoadMoreListener {
    private static final String TAG = WeiboMainFragemnt.class.getName();
    private CoordinatorLayout container;
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter = null;
    //当前 Token 信息
    private Oauth2AccessToken mAccessToken;
    //用于获取微博信息流等操作的API
    private StatusesAPI mStatusesAPI;
    private int mPage = 1;
    private View rootView;//复用rootview，使viewpager滑到第三页时此页面不销毁。

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (null == rootView) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
            initView(rootView);
            mPullLoadMoreRecyclerView.setRefreshing(true);
            mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);//滑动监听（刷新/加载更多）
            loadNewWeibo();//第一次打开则自动加载最新微博// FIXME: 2016/1/31 有可能没登陆
        }
        return rootView;
    }

    private void loadNewWeibo() {
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(getActivity(), Constants.APP_KEY, mAccessToken);
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mStatusesAPI.friendsTimeline(0L, 0L, 10, mPage, false, 0, false, mListener);//加载最新微博
        }
    }

    private void initView(View rootView) {
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) rootView.findViewById(R.id.pullLoadMoreRecyclerView);
        //设置线性布局
        mPullLoadMoreRecyclerView.setLinearLayout();
        rootView.findViewById(R.id.bottombar_tv_main).setOnClickListener(this);
        rootView.findViewById(R.id.bottombar_tv_push).setOnClickListener(this);
        rootView.findViewById(R.id.bottombar_tv_comment).setOnClickListener(this);
        rootView.findViewById(R.id.bottombar_tv_user).setOnClickListener(this);
        rootView.findViewById(R.id.bottombar_tv_discover).setOnClickListener(this);
        container = (CoordinatorLayout) rootView.findViewById(R.id.id_container);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottombar_tv_main:
                mPullLoadMoreRecyclerView.scrollToTop();
                mPage = 1;
                loadNewWeibo();
                break;
            case R.id.bottombar_tv_push:
                Intent writeWeibo = new Intent(getActivity(), WriteWeiboActivity.class);
                startActivity(writeWeibo);
                break;
        }
    }

    // 微博 OpenAPI 回调接口。
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();//关闭加载进度圈
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    final StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        if (mRecyclerViewAdapter == null) {
                            //绑定适配器
                            mRecyclerViewAdapter = new RecyclerViewAdapter(getActivity(),
                                    statuses.statusList);
                            mPullLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);
                        } else {
                            if (mPage == 1) {
                                mRecyclerViewAdapter.getmDatas().clear();
                            }
                            if (statuses.statusList != null) {
                                mRecyclerViewAdapter.getmDatas().addAll(statuses.statusList);
                                mRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                                Snackbar.make(container, "已无更多微博", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                        mRecyclerViewAdapter.setOnItemClickListener(new MyItemClickListener() {
                            @Override
                            public void onItemClick(View view, int postion) {
                                Status status = mRecyclerViewAdapter.getmDatas().get(postion);
                                if (status != null) {
                                    Intent intent = new Intent(getActivity(), ShowSingleWeibo.class);
                                    ShowSingleWeibo.status = status;
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(getActivity(), info.toString(), Toast.LENGTH_LONG).show();
        }
    };

    //下拉刷新
    @Override
    public void onRefresh() {
        mPage = 1;
        loadNewWeibo();
    }

    //滑到底部加载更多
    @Override
    public void onLoadMore() {
        mPage += 1;
        long maxId = mRecyclerViewAdapter.getMinId();
        mStatusesAPI.friendsTimeline(0L, maxId, 10, mPage, false, 0, false, mListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != rootView)
            ((ViewGroup) rootView.getParent()).removeView(rootView);
    }
}

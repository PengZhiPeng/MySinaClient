package com.example.acer.myrecycleviewtext;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
 * Created by acer on 2016/1/5.
 */
public class Img1Fragment extends Fragment implements View.OnClickListener,
        PullLoadMoreRecyclerView.PullLoadMoreListener {
    private static final String TAG = Img1Fragment.class.getName();

    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter = null;
    //当前 Token 信息
    private Oauth2AccessToken mAccessToken;
    //用于获取微博信息流等操作的API
    private StatusesAPI mStatusesAPI;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initView(rootView);
        mPullLoadMoreRecyclerView.setRefreshing(true);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);//滑动监听（刷新/加载更多）
        loadNewWeibo();//第一次打开则自动加载最新微博
        return rootView;
    }

    private void loadNewWeibo() {
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(getActivity(), Constants.APP_KEY, mAccessToken);
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);//加载最新微博
        }
    }

    private void initView(View rootView) {
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) rootView.findViewById(R.id.pullLoadMoreRecyclerView);
        //设置线性布局
        mPullLoadMoreRecyclerView.setLinearLayout();
        rootView.findViewById(R.id.bottombar_tv_main).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottombar_tv_main:
                loadNewWeibo();
                break;
        }
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            //关闭加载进度圈
            mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Log.i("json", response);
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    final StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        //绑定适配器
                        mRecyclerViewAdapter = new RecyclerViewAdapter(getActivity(), statuses.statusList);
                        mPullLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);
                        /*Object obj = view.getTag();
                        if (obj!=  null){
                            String weiboId = obj.toString();
                            Intent intent = new Intent(getActivity(),VideoPlay.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("weiboId",weiboId);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }*/
                    }
                } else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
                    Toast.makeText(getActivity(),
                            "发送一送微博成功, id = " + status.id,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
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
        loadNewWeibo();
    }

    //滑到底部加载更多
    @Override
    public void onLoadMore() {

    }
}

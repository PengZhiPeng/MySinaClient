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

import com.example.acer.myrecycleviewtext.utils.MyTimeUtils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * Created by acer on 2016/1/5.
 */
public class Img1Fragment extends Fragment implements View.OnClickListener {
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
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(getActivity(), Constants.APP_KEY, mAccessToken);
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);//加载最新微博
        }
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(
                new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                    @Override
                    public void onRefresh() {
                        mStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);
                    }

                    @Override
                    public void onLoadMore() {

                    }
                });
        return rootView;
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
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {

                        List<String> textlist = new ArrayList<>();
                        List<Date> timelist = new ArrayList<>();
                        List<String> namelist = new ArrayList<>();
                        List<String> profileImageUrlList = new ArrayList<>();
                        List<String> sourceList = new ArrayList<>();
                        List<String> retweetedTextList = new ArrayList<>();
                        List<ArrayList<String>> picUrlsList = new ArrayList<>();
                        Iterator it = statuses.statusList.iterator();
                        while (it.hasNext()) {
                            Status list = (Status) it.next();
                            textlist.add(list.text);//微博正文内容
                            timelist.add(MyTimeUtils.strToDate(list.created_at));//发布时间,泛型不同所以不跟微博来源合并
                            namelist.add(list.user.screen_name);//名字
                            profileImageUrlList.add(list.user.profile_image_url);//头像
                            sourceList.add(list.getTextSource());//微博来源
                            if (list.retweeted_status != null //被转发的微博名称和内容
                                    && list.retweeted_status.user != null) {
                                retweetedTextList.add("@" + list.retweeted_status.user.name
                                        + " : " + list.retweeted_status.text);
                            } else {
                                retweetedTextList.add("null");
                            }
                            if (list.retweeted_status != null) {//有转发，则添加转发的配图
                                picUrlsList.add(list.retweeted_status.pic_urls);
                            } else {
                                picUrlsList.add(list.pic_urls);//没转发，则添加原创微博的配图
                            }
                        }
                        //绑定适配器
                        mRecyclerViewAdapter = new RecyclerViewAdapter(getActivity(), textlist, timelist,
                                namelist, profileImageUrlList, sourceList, retweetedTextList,
                                picUrlsList);
                        mPullLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);

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
}

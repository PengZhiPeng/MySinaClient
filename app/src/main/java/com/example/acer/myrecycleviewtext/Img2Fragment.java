package com.example.acer.myrecycleviewtext;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by acer on 2016/1/5.
 */
public class Img2Fragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView tv = new TextView(getActivity());
        tv.setText("第二页，开发中...");
        return tv;
    }

    /*private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private List<String> mDatas;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initData();
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) rootView.findViewById(R.id.pullLoadMoreRecyclerView);
        //设置瀑布布局
        mPullLoadMoreRecyclerView.setStaggeredGridLayout(2);//参数为列数

        //绑定适配器
        mRecyclerViewAdapter = new RecyclerViewAdapter(mDatas);
        mPullLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);
        return rootView;
    }

    private void initData() {

        String[] array = {"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa","bbbbbbbb",
                "ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc","dddddddddddddddddd",
        "eeeeeeeeeeeeeeeeeeeeeeee","f","ggggggggggggggggggggggggggggggggggggggggggggggggggg",
        "hh","i","jjjj"};

        mDatas = new ArrayList<String>();
        for (int i = 0; i < array.length; i++) {
            mDatas.add(array[i]);
        }
    }*/
}

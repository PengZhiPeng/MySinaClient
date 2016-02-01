package com.example.acer.myrecycleviewtext.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.acer.myrecycleviewtext.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 浏览微博配图的fragment，一个fragment显示一张图。
 * （点击九宫格图片后的大图浏览页面）
 * Created by acer on 2016/1/31.
 */
public class ImageBrowerFragment extends Fragment {

    private static final String IMAGE_URL = "image";
    private PhotoView photoView;
    private PhotoViewAttacher mAttacher;
    private String imageUrl;

    public ImageBrowerFragment() {
    }

    public static ImageBrowerFragment newInstance(String imageUrl) {
        ImageBrowerFragment fragment = new ImageBrowerFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    //fragment的生命周期：
    // onAttach-->onCreat-->onCreatView-->onActivityCreated-->onStart-->onResume
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_brower, container, false);
        photoView = (PhotoView) view.findViewById(R.id.id_photoview);
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);
        //图片配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        //加载图片进 photoview
        ImageLoader.getInstance().displayImage(imageUrl, photoView, options);
        pb.setVisibility(View.GONE);
        return view;
    }

    //在onActivityCreated此周期中写点击事件
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //单击退出图片浏览页面，返回微博页面。
        photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                getActivity().finish();
            }
        });
    }
}
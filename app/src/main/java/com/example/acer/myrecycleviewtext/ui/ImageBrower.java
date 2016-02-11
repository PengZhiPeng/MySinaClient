package com.example.acer.myrecycleviewtext.ui;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.acer.myrecycleviewtext.R;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class ImageBrower extends AppCompatActivity {

    /**
     * 微博配图点击后进入此Activity，
     * 引用开源库 PhotoView（缩放） + viewPage +  fragment拖动浏览图片。
     * Created by acer on 2016/1/31.
     */
    private ImagePagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private List<String> mpicsList;
    //activity动画
    protected int activityCloseEnterAnimation;
    protected int activityCloseExitAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_brower);

        mpicsList = new ArrayList<>();

        Bundle bundle = this.getIntent().getExtras();
        int clickPosition = bundle.getInt("position");
        mpicsList = bundle.getStringArrayList("mPicsList");

        mSectionsPagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), mpicsList);
        mViewPager = (ViewPager) findViewById(R.id.id_imagebrower_viewpage);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(clickPosition);
        //绑定viewpager底部线形指示器
        UnderlinePageIndicator titleIndicator = (UnderlinePageIndicator)findViewById(R.id.id_indicator);
        titleIndicator.setViewPager(mViewPager);
        //活动开关动画
        activityCloseAnim();
    }

    private void activityCloseAnim() {
        TypedArray activityStyle = getTheme()
                .obtainStyledAttributes(new int[]{android.R.attr.windowAnimationStyle});
        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
        activityStyle.recycle();

        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId,
                new int[]{android.R.attr.activityCloseEnterAnimation,
                        android.R.attr.activityCloseExitAnimation});
        activityCloseEnterAnimation = activityStyle.getResourceId(0, 0);
        activityCloseExitAnimation = activityStyle.getResourceId(1, 0);
        activityStyle.recycle();
    }

    public class ImagePagerAdapter extends FragmentPagerAdapter {

        private List<String> mDatas;

        public ImagePagerAdapter(FragmentManager fm, List<String> mDatas) {
            super(fm);
            this.mDatas = mDatas;
        }

        @Override
        public Fragment getItem(int position) {
            String url = mDatas.get(position);
            Fragment fragment = ImageBrowerFragment.newInstance(url);
            return fragment;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(activityCloseEnterAnimation, activityCloseExitAnimation);
    }
}

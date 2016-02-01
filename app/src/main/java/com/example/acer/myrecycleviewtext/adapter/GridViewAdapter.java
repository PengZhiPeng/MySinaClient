package com.example.acer.myrecycleviewtext.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.acer.myrecycleviewtext.R;
import com.example.acer.myrecycleviewtext.ui.ImageBrower;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * listview 里嵌套的 gridview 的适配器，gridview用于显示微博配图。
 * Created by acer on 2016/1/11.
 */
public class GridViewAdapter extends BaseAdapter {

    private ArrayList<String> mPicsList;
    private Context mContext;

    public GridViewAdapter(Context mContext, ArrayList<String> mPicsList) {
        this.mPicsList = mPicsList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mPicsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPicsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pics_gridview_item, null, false);
            holder.holder_pics = (ImageView) convertView.findViewById(R.id.id_picture);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (holder.holder_pics != null) {
            //微博配图的配置
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            //加载微博配图
            ImageLoader.getInstance().displayImage(mPicsList.get(position),
                    holder.holder_pics, options);
            holder.holder_pics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ImageBrower.class);
                    Bundle b = new Bundle();
                    b.putInt("position", position);
                    b.putStringArrayList("mPicsList", mPicsList);
                    intent.putExtras(b);
                    mContext.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView holder_pics;
    }
}

package com.oktested.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.home.model.CommunityPostDataResponse;
import com.oktested.utils.Helper;
import com.oktested.utils.ScaleImageView;

import java.util.ArrayList;

public class CarouselPostAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<CommunityPostDataResponse.Album> albumAL;

    public CarouselPostAdapter(Context context, ArrayList<CommunityPostDataResponse.Album> albumAL) {
        this.context = context;
        this.albumAL = albumAL;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_post_item, parent, false);

        ScaleImageView postIV = view.findViewById(R.id.postIV);

        if (Helper.isContainValue(albumAL.get(position).url)) {
            Glide.with(context)
                    .load(albumAL.get(position).url)
                    .placeholder(R.drawable.placeholder)
                    .into(postIV);
        } else {
            postIV.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder));
        }

        parent.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return albumAL.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
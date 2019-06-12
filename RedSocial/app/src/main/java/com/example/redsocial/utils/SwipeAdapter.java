package com.example.redsocial.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.redsocial.ImageDetail;
import com.example.redsocial.R;

import java.util.ArrayList;

public class SwipeAdapter extends PagerAdapter {

    private ArrayList<String> images;
    private Context current;
    private LayoutInflater layoutInflater;

    public SwipeAdapter(Context context, ArrayList<String> images) {
        this.current = context;
        this.images = images;
        this.layoutInflater = (LayoutInflater) current.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject( View view, Object o) {
        return view == (ConstraintLayout)o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.image_for_galery, container, false);
        ImageView imagen = itemView.findViewById(R.id.imgForGalery);
        Glide.with(current).load(images.get(position)).into(imagen);

        imagen.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(current.getApplicationContext(), ImageDetail.class);
                intent.putExtra("userID", new UserPreferences(current).getEmail());
                intent.putExtra("imageUrl", images.get(position));
                current.startActivity(intent);
            }
        });

        container.addView(itemView);
        return itemView;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);

    }



}

package com.example.redsocial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.redsocial.R;

public class FeedFragment extends Fragment {

    View current;
    TextView txtPublicacion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        current = layoutInflater.inflate(R.layout.feed_layout, container, false);

        txtPublicacion = current.findViewById(R.id.txtPublicacion);
        txtPublicacion.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(current.getContext(), "Hello world", Toast.LENGTH_SHORT).show();
            }
        });


        return current;
    }



}

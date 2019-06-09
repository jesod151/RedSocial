package com.example.redsocial;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import com.example.redsocial.utils.UserPreferences;
import com.squareup.picasso.Picasso;


public class UserFragment extends Fragment {
    private View rootView;
    ImageView usrPhoto;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        rootView = layoutInflater.inflate(R.layout.user_layout, container, false);
        usrPhoto = rootView.findViewById(R.id.userPhoto);
        UserPreferences prefs = new UserPreferences(rootView.getContext());
        Picasso.get().load(prefs.getPhoto()).into(usrPhoto);
        return  rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }



}

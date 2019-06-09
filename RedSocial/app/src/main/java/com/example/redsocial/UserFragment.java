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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.redsocial.utils.UserPreferences;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executor;


public class UserFragment extends Fragment {
    private View rootView;
    ImageView usrPhoto;
    Button btnLogout;

    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth firebaseAuth;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        rootView = layoutInflater.inflate(R.layout.user_layout, container, false);
        usrPhoto = rootView.findViewById(R.id.userPhoto);
        UserPreferences prefs = new UserPreferences(rootView.getContext());
        Picasso.get().load(prefs.getPhoto()).fit().into(usrPhoto);
        TextView userNameTv = rootView.findViewById(R.id.txtName);
        userNameTv.setText(prefs.getNombre());
        TextView infoTv = rootView.findViewById(R.id.txtInfo);
        String info = "";
        info+="\nCorreo: ";
        info+=prefs.getEmail();
        infoTv.setText(info);

        btnLogout = rootView.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        return  rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        closeApp();
    }

    private void closeApp() {

        getActivity().finish();
    }





}

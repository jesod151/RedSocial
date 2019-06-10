package com.example.redsocial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.redsocial.utils.UserPreferences;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;


public class UserFragment extends Fragment {
    private View rootView;
    ImageView usrPhoto;
    Button btnLogout;
    Button btnGallery;
    Button btnFriendList;
    Button btnInfo;

    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth firebaseAuth;
    UserPreferences prefs;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        rootView = layoutInflater.inflate(R.layout.user_layout, container, false);
        usrPhoto = rootView.findViewById(R.id.userPhoto);
        prefs = new UserPreferences(rootView.getContext());
        String photoUrl = prefs.getPhoto();
        if(photoUrl.contains("profilePhotos")){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            //Log.e("Tuts+", "uri: " + photoUrl[0]);
            StorageReference storageRef = storage.getReferenceFromUrl("gs://redsocial-dam.appspot.com").child(photoUrl);
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri photoUri) {
                    Log.e("Tuts+", "uri: " + photoUri.toString());
                    //photoUrl[0] = photoUri.toString();
                    Picasso.get().load(photoUri.toString()).fit().into(usrPhoto);
                    //Handle whatever you're going to do with the URL here
                }
            });
        } else {Picasso.get().load(photoUrl).fit().into(usrPhoto);}




        TextView userNameTv = rootView.findViewById(R.id.txtName);
        userNameTv.setText(prefs.getNombre());
        String info = "";
        info+="\nCorreo: ";
        info+=prefs.getEmail();

        TextView AboutUser = rootView.findViewById(R.id.TextViewAbout);
        AboutUser.setText("Sobre "+ prefs.getNombre().substring(0, prefs.getNombre().indexOf(' ')));

        FillPersonalData();

        btnLogout = rootView.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        btnGallery = rootView.findViewById(R.id.btnGallery);
        btnGallery.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoGallery();
            }
        });

        btnFriendList = rootView.findViewById(R.id.btnFriends);
        btnFriendList.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoFriendList();
            }
        });

        btnInfo= rootView.findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoInfo();
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
        prefs.clear();
        getActivity().finish();
    }

    private void FillPersonalData(){
        final ListView lv = rootView.findViewById(R.id.UserInfo);

        List<String> your_array_list = new ArrayList();
        your_array_list.add("De: San Jose");
        your_array_list.add("Trabaja en: La Calle");
        your_array_list.add("Estudios: Fidelitas");
        your_array_list.add("Habla: Patua");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(), R.layout.simple_list_item_1, your_array_list);

        lv.setAdapter(arrayAdapter);


    }

    private void GoGallery(){
        //Display Gallery fragment or activity
        Toast.makeText(getActivity(),"Ir a galeria", Toast.LENGTH_LONG).show();
    }

    private void GoFriendList(){
        FriendListFragment friendlist = new FriendListFragment();
        friendlist.getActivity();

        Toast.makeText(getActivity(),"Ir a lista de amigos", Toast.LENGTH_LONG).show();
    }

    private void GoInfo(){
        Intent intent = new Intent(getActivity(), ManageInfoActivity.class);
        startActivity(intent);

        Toast.makeText(getActivity(),"Agregar Info", Toast.LENGTH_LONG).show();

    }





}

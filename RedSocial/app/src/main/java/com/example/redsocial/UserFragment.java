package com.example.redsocial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.bumptech.glide.Glide;
import com.example.redsocial.DataBaseObjects.Post;
import com.example.redsocial.DataBaseObjects.User;
import com.example.redsocial.utils.RecyclerViewAdapterPost;
import com.example.redsocial.utils.UserPreferences;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import static android.app.Activity.RESULT_OK;


public class UserFragment extends Fragment {
    private View rootView;
    ImageView usrPhoto;
    Button btnLogout;
    Button btnGallery;
    Button btnFriendList;
    Button btnInfo;
    Button btnDeleteUser;
    RecyclerView recyclerView;

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

        setPhoto();

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

        btnInfo = rootView.findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoInfo();
            }
        });

        btnDeleteUser = rootView.findViewById(R.id.btnDeleteUser);
        btnDeleteUser.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference collectionReference;
                Query query;

                collectionReference = db.collection("Comments");
                query = collectionReference.whereEqualTo("userID", prefs.getEmail());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            ArrayList<String> comments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference comentarios = db.collection("Comments").document(document.getId());
                                comentarios.delete();
                            }
                        }
                    }
                });

                collectionReference = db.collection("Users");
                query = collectionReference.whereEqualTo("email", prefs.getEmail());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            ArrayList<String> comments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference comentarios = db.collection("Users").document(document.getId());
                                comentarios.delete();
                            }
                        }
                    }
                });

                collectionReference = db.collection("Posts");
                query = collectionReference.whereEqualTo("userID", prefs.getEmail());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            ArrayList<String> comments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference comentarios = db.collection("Posts").document(document.getId());
                                comentarios.delete();
                            }
                        }
                    }
                });

                collectionReference = db.collection("Friends");
                query = collectionReference.whereEqualTo("user", prefs.getEmail());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            ArrayList<String> comments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference comentarios = db.collection("Friends").document(document.getId());
                                comentarios.delete();
                            }
                        }
                    }
                });

                collectionReference = db.collection("LikeDislike");
                query = collectionReference.whereEqualTo("userID", prefs.getEmail());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            ArrayList<String> comments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference comentarios = db.collection("LikeDislike").document(document.getId());
                                comentarios.delete();
                            }
                        }
                    }
                });

                collectionReference = db.collection("LikeDislikeImage");
                query = collectionReference.whereEqualTo("userID", prefs.getEmail());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            ArrayList<String> comments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference comentarios = db.collection("LikeDislikeImage").document(document.getId());
                                comentarios.delete();
                            }
                        }
                    }
                });

                collectionReference = db.collection("Galery");
                query = collectionReference.whereEqualTo("userID", prefs.getEmail());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            ArrayList<String> comments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference comentarios = db.collection("Galery").document(document.getId());
                                comentarios.delete();
                            }
                        }
                    }
                });

                collectionReference = db.collection("PersonalData");
                query = collectionReference.whereEqualTo("userID", prefs.getEmail());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            ArrayList<String> comments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference comentarios = db.collection("PersonalData").document(document.getId());
                                comentarios.delete();
                            }
                        }
                    }
                });

                Intent intent = new Intent(getActivity(), LogInActivity.class);
                startActivity(intent);
            }
        });

        searchForPosts();
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

        ListView lv = rootView.findViewById(R.id.UserInfo);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("PersonalData");

        Query query = collectionReference.whereEqualTo("userID", prefs.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<String> data = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        data.add(document.getString("data"));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_list_item_1, data);
                    lv.setAdapter(arrayAdapter);
                }
            }
        });
    }

    private void GoGallery(){
        Intent intent = new Intent(getActivity(), GaleryActivity.class);
        startActivity(intent);
    }

    private void GoFriendList(){
        Intent intent = new Intent(getActivity(), FriendList.class);
        startActivity(intent);
    }

    private void GoInfo(){
        Intent intent = new Intent(getActivity(), ManageInfoActivity.class);
        startActivityForResult(intent, 2);
        Toast.makeText(getActivity(),"Agregar Info", Toast.LENGTH_LONG).show();
    }


    public void searchForPosts(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Posts");

        Query query = collectionReference.whereEqualTo("userID", prefs.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    ArrayList<Post> posts = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        posts.add(0, document.toObject(Post.class));
                    }
                    setPosts(posts);
                }
            }
        });
    }

    private void setPosts(ArrayList<Post> posts){
        recyclerView = rootView.findViewById(R.id.recyclerProfilePosts);
        RecyclerViewAdapterPost recyclerViewAdapterPost = new RecyclerViewAdapterPost(rootView.getContext(), posts, this);
        recyclerView.setAdapter(recyclerViewAdapterPost);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){//posts
            searchForPosts();
        }
        else if(requestCode == 2){
            FillPersonalData();
        }
    }

    public void setPhoto(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Users");

        Query query = collectionReference.whereEqualTo("email", prefs.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(!document.getString("photo").equals("")){
                            Glide.with(getContext()).load(document.getString("photo")).into(usrPhoto);
                            return;
                        }
                    }
                }
            }
        });
    }

    public void getAmigosComun(String emailA, String emailB){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Friends");

        Query query = collectionReference.whereEqualTo("user", emailA);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<String> amigosA = new ArrayList();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(!document.getString("friend").equals(emailA)){
                            amigosA.add(document.getString("friend"));
                        }
                    }

                    Query queryB = collectionReference.whereEqualTo("user", emailB);
                    queryB.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                ArrayList<String> amigosB = new ArrayList();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(!document.getString("friend").equals(emailB)){
                                        amigosB.add(document.getString("friend"));
                                    }

                                }
                                getAmigosEnComun(amigosA, amigosB);
                            }
                        }
                    });
                }
            }
        });
    }

    public void getAmigosEnComun(ArrayList<String> amigosA, ArrayList<String> amigosB){
        ArrayList<String> result = new ArrayList<>();
        for(int i = 0; i < amigosA.size(); i++){
            for(int j = 0; j < amigosB.size(); j++){
                if(amigosA.get(i).equals(amigosB.get(j))){
                    result.add(amigosA.get(i));
                }
            }
        }
        getAmigosData(result);
    }

    public void getAmigosData(ArrayList<String> list){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Users");
        ArrayList<User> result = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){

            Query query = collectionReference.whereEqualTo("email", list.get(i));
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            result.add(new User(document.getString("name"),
                                    "",
                                    document.getString("email"),
                                    document.getString("photo")));

                            if(result.size() == list.size()){
                                amigosEnComunResult(result);
                            }
                        }
                    }
                }
            });
        }
    }

    public void amigosEnComunResult(ArrayList<User> list){
        for(int i = 0; i < list.size(); i++){
            Log.d("taga", "amigo: " + list.get(i).toString());
        }
    }

    public void getMisAmigos(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Friends");
        Query query = collectionReference.whereEqualTo("user", prefs.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<String> amigos = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(!document.getString("friend").equals(prefs.getEmail())){
                            amigos.add(document.getString("friend"));
                        }
                    }
                    Log.d("taga", "mis amigos son> ");
                    getAmigosData(amigos);
                }
            }
        });
    }
}


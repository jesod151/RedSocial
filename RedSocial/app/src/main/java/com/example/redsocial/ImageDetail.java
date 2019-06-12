package com.example.redsocial;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.redsocial.DataBaseObjects.Post;
import com.example.redsocial.utils.UserPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class ImageDetail extends AppCompatActivity {

    private TextView likes, dislikes, txtComentario, setPerfil;
    private ImageView btnLike, btnDislike, btnDelete, imagen, btnComment;
    private ListView listViewComments;
    private String currentUrl, currentUser;
    private int currentLikes, currentDislikes;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detai);
        userPreferences = new UserPreferences(this);

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();

        currentUrl = bundle.getString("imageUrl");
        currentUser = bundle.getString("userID");

        txtComentario = findViewById(R.id.txtComentarioDetailImage);
        likes = findViewById(R.id.txtLikesDetailImage);
        dislikes = findViewById(R.id.txtDislikeDetailImage);
        btnLike = findViewById(R.id.imgLikeDetailImage);
        btnDislike = findViewById(R.id.imgDislikeDetailImage);
        btnDelete = findViewById(R.id.imgDeletePostDetailImage);
        imagen = findViewById(R.id.imgPostDetailImage);
        btnComment = findViewById(R.id.imgComentarDetailImage);
        listViewComments = findViewById(R.id.listViewCommentsImage);
        setPerfil = findViewById(R.id.txtSetPerfil);

        Glide.with(this).load(currentUrl).into(imagen);
        setLikes();
        setComments();

        if(userPreferences.getEmail().equals(currentUser)){
            setPerfil.setVisibility(View.VISIBLE);
            setPerfil.setOnClickListener(new TextView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFotoPerfil();
                }
            });
        }

        btnLike.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLikes++;
                checkIfLikeable();
            }
        });

        btnDislike.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDislikes++;
                checkIfLikeable();
            }
        });

        btnComment.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });

    }

    public void updateCurrentImageLikes(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Galery");

        Query query = collectionReference.whereEqualTo("imageUrl", currentUrl);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getString("userID").equals(userPreferences.getEmail())){
                            DocumentReference image = db.collection("Galery").document(document.getId());
                            image.update("likes", Integer.toString(currentLikes), "dislikes", Integer.toString(currentDislikes));
                            likes.setText(Integer.toString(currentLikes));
                            dislikes.setText(Integer.toString(currentDislikes));
                            return;
                        }
                    }
                    updateCurrentImageLikes();
                    addSetLikeDislike();
                }
            }
        });

    }

    public void checkIfLikeable(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("LikeDislikeImage");

        Query query = collectionReference.whereEqualTo("imageUrl", currentUrl);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getString("userID").equals(userPreferences.getEmail())){
                            Toast.makeText(getApplicationContext(), "Usted ya había calificado la publicacion", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    updateCurrentImageLikes();
                    addSetLikeDislike();
                }
            }
        });
    }

    public void setLikes(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Galery");

        Query query = collectionReference.whereEqualTo("imageUrl", currentUrl);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getString("userID").equals(userPreferences.getEmail())){
                            currentLikes = Integer.valueOf(document.getString("likes"));
                            currentDislikes = Integer.valueOf(document.getString("dislikes"));
                            likes.setText(Integer.toString(currentLikes));
                            dislikes.setText(Integer.toString(currentDislikes));
                            return;
                        }
                    }
                    updateCurrentImageLikes();
                    addSetLikeDislike();
                }
            }
        });
    }

    public void addSetLikeDislike(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference postDocument = db.collection("LikeDislikeImage").document();
        HashMap<String, String> hash = new HashMap<>();
        hash.put("userID", userPreferences.getEmail());
        hash.put("imageUrl", currentUrl);
        postDocument.set(hash);
    }

    public void addComment() {
        if(!this.txtComentario.getText().toString().equals("")){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference postDocument = db.collection("CommentsImage").document();
            HashMap<String, String> hash = new HashMap<>();
            hash.put("username", userPreferences.getNombre());
            hash.put("imageUrl", currentUrl);
            hash.put("texto", this.txtComentario.getText().toString());
            postDocument.set(hash);
            setComments();
        }
    }

    public void setComments(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("CommentsImage");

        Query query = collectionReference.whereEqualTo("imageUrl", currentUrl);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<String> comments = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        comments.add(document.getString("username") + ": " + document.getString("texto"));
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.simple_list_item_1, comments);
                    listViewComments.setAdapter(arrayAdapter);
                }
            }
        });
    }

    public void setFotoPerfil(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Users");

        Query query = collectionReference.whereEqualTo("email", userPreferences.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("taga", "email document:" + document.getString("email"));
                        if(document.getString("email").equals(userPreferences.getEmail())){
                            DocumentReference user = db.collection("Users").document(document.getId());
                            user.update("photo", currentUrl);
                            return;
                        }
                    }
                    updateCurrentImageLikes();
                    addSetLikeDislike();
                }
            }
        });
    }
}

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
import com.example.redsocial.DataBaseObjects.User;
import com.example.redsocial.utils.UserPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Post_detail_layout extends AppCompatActivity {

    private TextView texto, likes, dislikes, txtComentario;
    private ImageView btnLike, btnDislike, btnDelete, imagen, btnComment, imgUser;
    private ListView listViewComments;
    private Post current;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = new UserPreferences(this);
        setContentView(R.layout.activity_post_detail_layout);

        txtComentario = findViewById(R.id.txtComentarioDetail);
        texto = findViewById(R.id.txtPostDetail);
        likes = findViewById(R.id.txtLikesDetail);
        dislikes = findViewById(R.id.txtDislikeDetail);
        btnLike = findViewById(R.id.imgLikeDetail);
        btnDislike = findViewById(R.id.imgDislikeDetail);
        btnDelete = findViewById(R.id.imgDeletePostDetail);
        imagen = findViewById(R.id.imgPostDetail);
        btnComment = findViewById(R.id.imgComentarDetail);
        imgUser = findViewById(R.id.imgPostDetailUser);
        listViewComments = findViewById(R.id.listViewComments);

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        current = new Post(bundle.getString("id"),
                           bundle.getString("userID"),
                           bundle.getString("texto"),
                           bundle.getString("imagen"),
                           bundle.getInt("likes"),
                           bundle.getInt("dislikes"),
                           new Date());
        current.setUserName(bundle.getString("userName"));

        Glide.with(this).load(bundle.getString("imgUser")).into(imgUser);
        texto.setText(current.getUserName() + "\n" + bundle.getString("fecha") + "\n" + current.getTexto());
        likes.setText(Integer.toString(current.getLikes()));
        dislikes.setText(Integer.toString(current.getDislikes()));
        if(!current.getImagenUrl().equals("")){
            Glide.with(this).load(current.getImagenUrl()).into(imagen);
        }
        else{
            imagen.setVisibility(View.GONE);
        }
        setComments();


        btnLike.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
               current.like();
               checkIfLikeable();
            }
        });

        btnDislike.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                current.dislike();
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

    public void updateCurrentPostLikes(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference document = db.collection("Posts").document(current.getPostID());
        document.update("likes", current.getLikes(), "dislikes", current.getDislikes());
        likes.setText(Integer.toString(current.getLikes()));
        dislikes.setText(Integer.toString(current.getDislikes()));
    }

    public void checkIfLikeable(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("LikeDislike");

        Query query = collectionReference.whereEqualTo("postID", current.getPostID());
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
                    updateCurrentPostLikes();
                    addSetLikeDislike();
                }
            }
        });
    }

    public void addSetLikeDislike(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference postDocument = db.collection("LikeDislike").document();
        HashMap<String, String> hash = new HashMap<>();
        hash.put("userID", userPreferences.getEmail());
        hash.put("postID", current.getPostID());
        postDocument.set(hash);
    }

    public void addComment() {
        if(!this.txtComentario.getText().toString().equals("")){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference postDocument = db.collection("Comments").document();
            HashMap<String, String> hash = new HashMap<>();
            hash.put("username", userPreferences.getNombre());
            hash.put("postID", current.getPostID());
            hash.put("texto", this.txtComentario.getText().toString());
            hash.put("üserID", userPreferences.getEmail());
            postDocument.set(hash);
            setComments();
        }
    }

    public void setComments(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Comments");

        Query query = collectionReference.whereEqualTo("postID", current.getPostID());
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

}

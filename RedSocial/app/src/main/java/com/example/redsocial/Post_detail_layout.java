package com.example.redsocial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Post_detail_layout extends AppCompatActivity {

    private TextView texto, likes, dislikes, txtComentario;
    private ImageView btnLike, btnDislike, btnDelete, imagen, btnComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail_layout);


        texto = findViewById(R.id.txtPostDetail);
        likes = findViewById(R.id.txtLikesDetail);
        dislikes = findViewById(R.id.txtDislikeDetail);
        btnLike = findViewById(R.id.imgLikeDetail);
        btnDislike = findViewById(R.id.imgDislikeDetail);
        btnDelete = findViewById(R.id.imgDeletePostDetail);
        imagen = findViewById(R.id.imgPostDetail);
        btnComment = findViewById(R.id.imgPostDetail);

    }
}

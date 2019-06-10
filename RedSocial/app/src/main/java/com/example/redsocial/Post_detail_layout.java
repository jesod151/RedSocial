package com.example.redsocial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.redsocial.DataBaseObjects.Post;

import java.util.Date;


public class Post_detail_layout extends AppCompatActivity {

    private TextView texto, likes, dislikes, txtComentario;
    private ImageView btnLike, btnDislike, btnDelete, imagen, btnComment;
    private Post current;

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

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        current = new Post(bundle.getString("userID"),
                           bundle.getString("texto"),
                           bundle.getString("imagen"),
                           bundle.getInt("likes"),
                           bundle.getInt("dislikes"),
                           new Date());

        texto.setText(current.getUserID() + "\n" + bundle.getString("fecha") + "\n" + current.getTexto());
        likes.setText(Integer.toString(current.getLikes()));
        dislikes.setText(Integer.toString(current.getDislikes()));
        if(!current.getImagenUrl().equals("")){
            Glide.with(this).load(current.getImagenUrl()).into(imagen);
        }
    }


}

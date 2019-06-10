package com.example.redsocial.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.redsocial.DataBaseObjects.Post;
import com.example.redsocial.Post_detail_layout;
import com.example.redsocial.R;

import java.util.ArrayList;
import java.util.Date;

public class RecyclerViewAdapterPost extends RecyclerView.Adapter<RecyclerViewAdapterPost.ViewHolder>{

    private Context context;
    private ArrayList<Post> posts;

    public RecyclerViewAdapterPost(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feedpost_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.likes.setText(Integer.toString(posts.get(i).getLikes()));
        viewHolder.dislikes.setText(Integer.toString(posts.get(i).getDislikes()));
        viewHolder.texto.setText(posts.get(i).getUserID() + "\n" + getDistanciaFecha(posts.get(i).getFechaCreacion()) + "\n" + posts.get(i).getTexto());
        if(!posts.get(i).getImagenUrl().equals("")){
            Glide.with(context).load(posts.get(i).getImagenUrl()).into(viewHolder.imagen);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goMainActivity(posts.get(i));
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView texto, likes, dislikes;
        private ImageView btnLike, btnDislike, btnDelete, imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            texto = itemView.findViewById(R.id.txtPost);
            likes = itemView.findViewById(R.id.txtLikes);
            dislikes = itemView.findViewById(R.id.txtDislikes);
            btnLike = itemView.findViewById(R.id.imgLike);
            btnDislike = itemView.findViewById(R.id.imgDislike);
            btnDelete = itemView.findViewById(R.id.imgDeletePost);
            imagen = itemView.findViewById(R.id.imgPost);

        }
    }

    private void goMainActivity(Post post){
        Intent intent = new Intent(context, Post_detail_layout.class);
        intent.putExtra("userID", post.getUserID());
        intent.putExtra("texto", post.getTexto());
        intent.putExtra("likes", post.getLikes());
        intent.putExtra("imagen", post.getImagenUrl());
        intent.putExtra("dislikes", post.getDislikes());
        intent.putExtra("fecha", getDistanciaFecha(post.getFechaCreacion()));
        context.startActivity(intent);
    }

    private String getDistanciaFecha(Date date){

        Date actual = new Date();
        long diff = actual.getTime() - date.getTime();
        int diffMinutes = (int) Math.floor(diff / (60 * 1000) % 60);
        int diffHours = (int) Math.floor(diff / (60 * 60 * 1000));

        Log.d("fecja", Integer.toString(diffHours));
        Log.d("fecja", Integer.toString(diffMinutes));

        if(diffHours >= 24){
            return "hace " + Double.toString((int) Math.floor(diffHours / 24)) + " dias";
        }
        if(diffHours >= 1){
            return "hace " + Integer.toString(diffHours) + " horas";
        }
        if(diffMinutes >= 60){
            return "hace " + Double.toString((int) Math.floor(diffMinutes / 60)) + " horas";
        }
        if(diffMinutes >= 1){
            return "hace " + Integer.toString(diffMinutes) + " minutos";
        }

        return "hace menos de un minuto";
    }
}

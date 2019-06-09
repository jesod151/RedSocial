package com.example.redsocial.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.redsocial.DataBaseObjects.Post;
import com.example.redsocial.R;

import java.util.ArrayList;

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
        viewHolder.texto.setText(posts.get(i).getUserID() + "\n" + posts.get(i).getTexto());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, posts.get(i).getUserID(), Toast.LENGTH_SHORT).show();
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
}

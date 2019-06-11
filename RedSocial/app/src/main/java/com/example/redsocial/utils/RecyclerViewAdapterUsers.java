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

import com.bumptech.glide.Glide;
import com.example.redsocial.DataBaseObjects.Post;
import com.example.redsocial.DataBaseObjects.User;
import com.example.redsocial.FeedFragment;
import com.example.redsocial.Post_detail_layout;
import com.example.redsocial.R;
import com.example.redsocial.SearchFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class RecyclerViewAdapterUsers extends RecyclerView.Adapter<RecyclerViewAdapterUsers.ViewHolder>{

    private Context context;
    private ArrayList<User> users;
    private FeedFragment feedFragment;
    private SearchFragment searchFragment;
    private UserPreferences userPreferences;

    public RecyclerViewAdapterUsers(Context context, ArrayList<User> users, SearchFragment searchFragment) {
        this.context = context;
        this.userPreferences = new UserPreferences(context);
        this.users = users;
        this.searchFragment = searchFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_search_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.texto.setText(users.get(i).getNombre());
        if(!users.get(i).getImagenUrl().equals("")){
            Glide.with(context).load(users.get(i).getImagenUrl()).into(viewHolder.imagen);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView texto;
        private ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            texto = itemView.findViewById(R.id.txtUserSearch);
            imagen = itemView.findViewById(R.id.imgUserSearch);

        }
    }

    /*private void goPostDetail(Post post){
        Intent intent = new Intent(context, Post_detail_layout.class);
        intent.putExtra("userID", post.getUserID());
        intent.putExtra("texto", post.getTexto());
        intent.putExtra("likes", post.getLikes());
        intent.putExtra("imagen", post.getImagenUrl());
        intent.putExtra("imgUser", post.getImageUrlUser());
        intent.putExtra("dislikes", post.getDislikes());
        intent.putExtra("fecha", getDistanciaFecha(post.getFechaCreacion()));
        intent.putExtra("id", post.getPostID());
        
        if(feedFragment != null){
            feedFragment.startActivityForResult(intent, 2);
        }
        else if(searchFragment != null){
            searchFragment.startActivityForResult(intent, 1);
        }
    }*/

    public void setUserImageUrl(Post post, ImageView img){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Users");

        Query query = collectionReference.whereEqualTo("email", post.getUserID());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        post.setImageUrlUser(document.getString("photo"));
                        if(!post.getImageUrlUser().equals("")){
                            Glide.with(context).load(post.getImageUrlUser()).into(img);
                        }
                    }
                }
            }
        });
    }

}

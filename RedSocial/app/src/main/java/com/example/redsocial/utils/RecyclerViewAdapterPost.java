package com.example.redsocial.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class RecyclerViewAdapterPost extends RecyclerView.Adapter<RecyclerViewAdapterPost.ViewHolder>{

    private Context context;
    private ArrayList<Post> posts;
    private FeedFragment feedFragment;
    private SearchFragment searchFragment;
    private UserPreferences userPreferences;

    public RecyclerViewAdapterPost(Context context, ArrayList<Post> posts, FeedFragment feedFragment) {
        this.context = context;
        this.userPreferences = new UserPreferences(context);
        this.posts = posts;
        this.feedFragment = feedFragment;
    }

    public RecyclerViewAdapterPost(Context context, ArrayList<Post> posts, SearchFragment searchFragment) {
        this.context = context;
        this.userPreferences = new UserPreferences(context);
        this.posts = posts;
        this.searchFragment = searchFragment;
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
        viewHolder.texto.setText(posts.get(i).getUserName() + "\n" + getDistanciaFecha(posts.get(i).getFechaCreacion()) + "\n" + posts.get(i).getTexto());
        if(!posts.get(i).getImagenUrl().equals("")){
            Glide.with(context).load(posts.get(i).getImagenUrl()).into(viewHolder.imagen);
        }
        if(userPreferences.getEmail().equals(posts.get(i).getUserID())){
            viewHolder.btnDelete.setVisibility(View.VISIBLE);
            viewHolder.btnDelete.setClickable(true);
            viewHolder.btnDelete.setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference documentReference = db.collection("Posts").document(posts.get(i).getPostID());
                    documentReference.delete();
                    feedFragment.getPosts();
                }
            });
        }
        setUserImageUrl(posts.get(i), viewHolder.imgUser);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goPostDetail(posts.get(i));
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView texto, likes, dislikes;
        private ImageView btnLike, btnDislike, btnDelete, imagen, imgUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            texto = itemView.findViewById(R.id.txtPost);
            likes = itemView.findViewById(R.id.txtLikes);
            dislikes = itemView.findViewById(R.id.txtDislikes);
            btnLike = itemView.findViewById(R.id.imgLike);
            btnDislike = itemView.findViewById(R.id.imgDislike);
            btnDelete = itemView.findViewById(R.id.imgDeletePost);
            imagen = itemView.findViewById(R.id.imgPost);
            imgUser = itemView.findViewById(R.id.imgPostUserImage);
            btnDelete = itemView.findViewById(R.id.imgDeletePost);

        }
    }

    private void goPostDetail(Post post){
        Intent intent = new Intent(context, Post_detail_layout.class);
        intent.putExtra("userID", post.getUserID());
        intent.putExtra("userName", post.getUserName());
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


    }

    private String getDistanciaFecha(Date date){

        Date actual = new Date();
        long diff = actual.getTime() - date.getTime();
        int diffMinutes = (int) Math.floor(diff / (60 * 1000) % 60);
        int diffHours = (int) Math.floor(diff / (60 * 60 * 1000));

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

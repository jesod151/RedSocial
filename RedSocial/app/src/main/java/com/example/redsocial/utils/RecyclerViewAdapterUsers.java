package com.example.redsocial.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.redsocial.DataBaseObjects.Notificacion;
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
import java.util.HashMap;
import java.util.UUID;

public class RecyclerViewAdapterUsers extends RecyclerView.Adapter<RecyclerViewAdapterUsers.ViewHolder>{

    private Context context;
    private ArrayList<User> users;
    private FeedFragment feedFragment;
    private SearchFragment searchFragment;
    private UserPreferences userPreferences;
    private ArrayList<String> amigos;

    public RecyclerViewAdapterUsers(Context context, ArrayList<User> users, SearchFragment searchFragment) {
        this.context = context;
        this.userPreferences = new UserPreferences(context);
        this.users = users;
        this.searchFragment = searchFragment;
        amigos = new ArrayList<>();
        getMisAmigos(userPreferences.getEmail());
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
                Toast.makeText(context, "Hola soy " + users.get(i).getNombre(), Toast.LENGTH_LONG).show();
            }
        });
        if(searchFragment != null && !isAmigo(users.get(i).getCorreo())) {
            viewHolder.btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    //Instancio la bd

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference notifDocument = db.collection("Notifications").document();

                    //Creo la notificacion
                    Notificacion notif = new Notificacion(notifDocument.getId(), "SolicitudAmistad", userPreferences.getEmail(),
                            users.get(i).getCorreo(), "" + userPreferences.getNombre() + " quiere ser tu amigo.", new Date(),
                            false, userPreferences.getPhoto());
                    notifDocument.set(notif).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(context, "Invitacion de amistad enviada a " + users.get(i).getNombre(), Toast.LENGTH_LONG).show();
                        }
                    });
                    viewHolder.btnAddFriend.setEnabled(false);
                    viewHolder.btnAddFriend.setBackgroundResource(R.drawable.add_friend_yet);
                }

            });
        }
        else {
            viewHolder.btnAddFriend.setVisibility(View.GONE);
        }

    }

    private boolean isAmigo(String mail){
        Log.d("taga", "mis amigos son> "+ amigos.toString());
        Log.d("taga", "??? "+ mail);
        return amigos.contains(mail);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView texto;
        private ImageView imagen;
        private Button btnAddFriend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            texto = itemView.findViewById(R.id.txtUserSearch);
            imagen = itemView.findViewById(R.id.imgUserSearch);
            btnAddFriend = itemView.findViewById(R.id.btnAddFriend);

        }
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
    public void getMisAmigos(String email){
        amigos = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Friends");
        Query query = collectionReference.whereEqualTo("user", email);

        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    if(!document.getString("friend").equals(userPreferences.getEmail())){
                        amigos.add(document.getString("friend"));
                    }
                }
            }
        });
    }



}



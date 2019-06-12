package com.example.redsocial;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.redsocial.DataBaseObjects.Notificacion;
import com.example.redsocial.DataBaseObjects.Post;
import com.example.redsocial.DataBaseObjects.User;
import com.example.redsocial.utils.RecyclerViewAdapterNotification;
import com.example.redsocial.utils.RecyclerViewAdapterPost;
import com.example.redsocial.utils.UserPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {
    private View current;
    private ArrayList<Notificacion> notifs = new ArrayList<>();
    private RecyclerView recyclerView;

    private UserPreferences prefs;
    private User currentUser;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        current = layoutInflater.inflate(R.layout.notification_layout, container, false);
        prefs = new UserPreferences(current.getContext());

        db = FirebaseFirestore.getInstance();

        getNotifications();

        return current;
    }

    public void getNotifications(){

        notifs = new ArrayList<>();
        CollectionReference collectionReference = db.collection("Notifications");

        Query query = collectionReference.whereEqualTo("receiver", prefs.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        notifs.add(0, document.toObject(Notificacion.class));
                        Log.i("For ", "Encontrado");
                    }
                    setNotifs();
                }
            }
        });

    }

    private void setNotifs(){
        recyclerView = current.findViewById(R.id.notifRecycler);
        RecyclerViewAdapterNotification recyclerViewAdapterNotification = new RecyclerViewAdapterNotification(current.getContext(), notifs, this);
        recyclerView.setAdapter(recyclerViewAdapterNotification);
        recyclerView.setLayoutManager(new LinearLayoutManager(current.getContext()));
    }



}

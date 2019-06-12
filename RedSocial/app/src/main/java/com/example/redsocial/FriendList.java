package com.example.redsocial;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.redsocial.DataBaseObjects.User;
import com.example.redsocial.utils.RecyclerViewAdapterUsers;
import com.example.redsocial.utils.UserPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FriendList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        prefs = new UserPreferences(this);

        recyclerView = findViewById(R.id.friendRecycler);
        getMisAmigos();
    }

    public void getAmigosData(ArrayList<String> list){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Users");
        ArrayList<User> result = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){

            Query query = collectionReference.whereEqualTo("email", list.get(i));
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            result.add(new User(document.getString("name"),
                                    "",
                                    document.getString("email"),
                                    document.getString("photo")));

                            if(result.size() == list.size()){
                                setUsers(result);
                            }
                        }
                    }
                }
            });
        }
    }

    private void setUsers(ArrayList<User> users){
        RecyclerViewAdapterUsers recyclerViewAdapterUsers = new RecyclerViewAdapterUsers(getApplicationContext(), users, null);
        recyclerView.setAdapter(recyclerViewAdapterUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void getMisAmigos(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Friends");
        Query query = collectionReference.whereEqualTo("user", prefs.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<String> amigos = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(!document.getString("friend").equals(prefs.getEmail())){
                            amigos.add(document.getString("friend"));
                        }
                    }
                    Log.d("taga", "mis amigos son> ");
                    getAmigosData(amigos);
                }
            }
        });
    }
}

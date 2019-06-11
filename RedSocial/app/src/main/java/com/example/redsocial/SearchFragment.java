package com.example.redsocial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.redsocial.DataBaseObjects.Post;
import com.example.redsocial.DataBaseObjects.User;
import com.example.redsocial.utils.RecyclerViewAdapterPost;
import com.example.redsocial.utils.RecyclerViewAdapterUsers;
import com.example.redsocial.utils.UserPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment {

    private Spinner spinner;
    private View current;
    private TextView filter;
    private ImageView search;
    private RecyclerView recyclerView;
    private UserPreferences userPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        current = layoutInflater.inflate(R.layout.search_layout, container, false);
        userPreferences = new UserPreferences(current.getContext());

        filter = current.findViewById(R.id.txtSearchFilter);
        search = current.findViewById(R.id.imgSearch);
        recyclerView = current.findViewById(R.id.searchRecyclerView);
        spinner = current.findViewById(R.id.spinnerSearch);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(current.getContext(), R.array.searchs, R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        search.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spinner.getSelectedItem().toString().equals("")){
                    if(spinner.getSelectedItem().toString().equals("Usuarios")){
                        searchForUsers();
                    }
                    else{
                        searchForPosts();
                    }
                }
            }
        });
        return current;
    }

    public void searchForUsers(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Users");

        Query query = collectionReference;
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    ArrayList<User> users = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getString("name").toUpperCase().contains(filter.getText().toString().toUpperCase())
                                || document.getString("email").toUpperCase().contains(filter.getText().toString().toUpperCase())){
                            users.add(new User(document.getString("name"),
                                            "",
                                            document.getString("email"),
                                            document.getString("photo")));
                        }
                    }
                    setUsers(users);
                }
            }
        });

    }

    public void searchForPosts(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Posts");

        Query query = collectionReference;
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    ArrayList<Post> posts = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getString("texto").toUpperCase().contains(filter.getText().toString().toUpperCase())) {
                            posts.add(0, document.toObject(Post.class));
                        }
                    }
                    setPosts(posts);
                }
            }
        });
    }

    private void setPosts(ArrayList<Post> posts){
        recyclerView = current.findViewById(R.id.searchRecyclerView);
        RecyclerViewAdapterPost recyclerViewAdapterPost = new RecyclerViewAdapterPost(current.getContext(), posts, this);
        recyclerView.setAdapter(recyclerViewAdapterPost);
        recyclerView.setLayoutManager(new LinearLayoutManager(current.getContext()));
    }

    private void setUsers(ArrayList<User> users){
        recyclerView = current.findViewById(R.id.searchRecyclerView);
        RecyclerViewAdapterUsers recyclerViewAdapterUsers = new RecyclerViewAdapterUsers(current.getContext(), users, this);
        recyclerView.setAdapter(recyclerViewAdapterUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(current.getContext()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){//posts
            searchForPosts();
        }
        else{//users
            searchForUsers();
        }
    }


}

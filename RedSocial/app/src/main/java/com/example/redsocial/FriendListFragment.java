package com.example.redsocial;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.redsocial.DataBaseObjects.Post;
import com.example.redsocial.R;
import com.example.redsocial.utils.RecyclerViewAdapterPost;

import java.util.ArrayList;
import java.util.Date;


public class FriendListFragment extends Fragment {
    View current;
    TextView txtPublicacion;
    ArrayList<Post> posts = new ArrayList<>();
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        current = layoutInflater.inflate(R.layout.feed_layout, container, false);

        txtPublicacion = current.findViewById(R.id.txtPublicacion);
        txtPublicacion.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(current.getContext(), "Hello world", Toast.LENGTH_SHORT).show();
            }
        });

        posts.add(new Post("usuarioID1", "texto1", "", 0, 0, new Date()));
        posts.add(new Post("usuarioID2", "texto2", "", 0, 0, new Date()));
        posts.add(new Post("usuarioID3", "texto3", "", 0, 0, new Date()));
        posts.add(new Post("usuarioID4", "texto4", "", 0, 0, new Date()));
        posts.add(new Post("usuarioID5", "texto5", "", 0, 0, new Date()));
        posts.add(new Post("usuarioID6", "texto6", "", 0, 0, new Date()));
        posts.add(new Post("usuarioID7", "texto7", "", 0, 0, new Date()));
        posts.add(new Post("usuarioID8", "texto8", "", 0, 0, new Date()));

        recyclerView = current.findViewById(R.id.feedRecycler);
        RecyclerViewAdapterPost recyclerViewAdapterPost = new RecyclerViewAdapterPost(current.getContext(), posts, (FeedFragment) null);
        recyclerView.setAdapter(recyclerViewAdapterPost);
        recyclerView.setLayoutManager(new LinearLayoutManager(current.getContext()));

        return current;
    }
}

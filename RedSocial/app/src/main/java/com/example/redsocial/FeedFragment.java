package com.example.redsocial;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.redsocial.DataBaseObjects.Post;
import com.example.redsocial.DataBaseObjects.User;
import com.example.redsocial.R;
import com.example.redsocial.utils.RecyclerViewAdapterPost;
import com.example.redsocial.utils.UserPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class FeedFragment extends Fragment {

    private View current;
    private TextView txtPublicacion;
    private ImageView imgPost;
    private Button btnPost;
    private ArrayList<Post> posts = new ArrayList<>();
    private RecyclerView recyclerView;

    private Uri mImageUri = null;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;

    private UserPreferences prefs;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        current = layoutInflater.inflate(R.layout.feed_layout, container, false);
        prefs = new UserPreferences(current.getContext());

        imgPost = current.findViewById(R.id.imgAdd);
        btnPost = current.findViewById(R.id.btnPublicar);
        txtPublicacion = current.findViewById(R.id.txtPublicacion);


        imgPost.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnPost.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPost();
                setUserInstance();
            }
        });

        setUserInstance();

        return current;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Glide.with(this).load(mImageUri).into(imgPost);
        }
        else if(requestCode == 2){
            setUserInstance();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadPost() {
        mStorageRef = FirebaseStorage.getInstance().getReference("Posts");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference postDocument = db.collection("Posts").document();

        Post toUpload = new Post(postDocument.getId(),
                                 prefs.getEmail(),
                                 this.txtPublicacion.getText().toString(),
                                 0, 0,
                                 new Date());

        toUpload.setUserName(prefs.getNombre());

        if (mImageUri != null) {
            StorageTask mUploadTask;
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    toUpload.setImagenUrl(uri.toString());
                                    postDocument.set(toUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(current.getContext(), "Se realizó la publiación", Toast.LENGTH_SHORT).show();
                                            txtPublicacion.setText("");
                                            imgPost.setBackgroundResource(R.drawable.ic_add_box_black_24dp);
                                            getPosts();
                                        }
                                    });
                                }
                            });
                        }
                    });
        }
        else{
            postDocument.set(toUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(current.getContext(), "Se realizó la publiación", Toast.LENGTH_SHORT).show();
                    txtPublicacion.setText("");
                    imgPost.setBackgroundResource(R.drawable.ic_add_box_black_24dp);
                }
            });
        }
    }

    private void setUserInstance(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Users");

        Query query = collectionReference.whereEqualTo("email", prefs.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        currentUser = new User(document.getString("name"),
                                               "",
                                               document.getString("email"),
                                               document.getString("photo"));
                        getFriends();
                        return;
                    }
                }
            }
        });
    }

    private void getFriends(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Friends");

        Query query = collectionReference.whereEqualTo("user", currentUser.getCorreo());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        currentUser.addAmigo(document.getString("friend"));
                    }
                    getPosts();
                }
            }
        });
    }

    public void getPosts(){

        posts = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Posts");

        for(int i = 0; i < currentUser.getAmigos().size(); i++) {

            Query query = collectionReference.whereEqualTo("userID", currentUser.getAmigos().get(i));
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            posts.add(0, document.toObject(Post.class));
                        }
                        setPosts();
                    }
                }
            });
        }
    }

    private void setPosts(){
        recyclerView = current.findViewById(R.id.feedRecycler);
        RecyclerViewAdapterPost recyclerViewAdapterPost = new RecyclerViewAdapterPost(current.getContext(), posts, this);
        recyclerView.setAdapter(recyclerViewAdapterPost);
        recyclerView.setLayoutManager(new LinearLayoutManager(current.getContext()));
    }



}

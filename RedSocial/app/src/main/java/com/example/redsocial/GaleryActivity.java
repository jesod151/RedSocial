package com.example.redsocial;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.redsocial.DataBaseObjects.Post;
import com.example.redsocial.utils.SwipeAdapter;
import com.example.redsocial.utils.UserPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class GaleryActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private SwipeAdapter swipeAdapter;
    private UserPreferences userPreferences;
    private Uri mImageUri = null;
    private ImageView image, addImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galery_layout);
        userPreferences = new UserPreferences(this);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        image = findViewById(R.id.imgGalery);
        addImage = findViewById(R.id.imgAddToGalery);

        image.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        addImage.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        getImages();
    }

    public void getImages() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Galery");
        Query query = collectionReference.whereEqualTo("userID", userPreferences.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<String> images = new ArrayList<>();
                    for(QueryDocumentSnapshot document: task.getResult()){
                        images.add(document.getString("imageUrl"));
                    }
                    if(!images.isEmpty()){
                        setImages(images);
                    }
                }
            }
        });
    }

    public void setImages(ArrayList<String> images){
        swipeAdapter = new SwipeAdapter(this, images);
        viewPager.setAdapter(swipeAdapter);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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

            Glide.with(this).load(mImageUri).into(image);
        }
    }

    public void uploadImage(){
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("Galery");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference postDocument = db.collection("Galery").document();

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
                                    HashMap<String, String> toUpload = new HashMap<>();
                                    toUpload.put("userID", userPreferences.getEmail());
                                    toUpload.put("imageUrl", uri.toString());
                                    toUpload.put("likes", "0");
                                    toUpload.put("dislikes", "0");
                                    postDocument.set(toUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            getImages();
                                        }
                                    });
                                }
                            });
                        }
                    });
        }
    }
}

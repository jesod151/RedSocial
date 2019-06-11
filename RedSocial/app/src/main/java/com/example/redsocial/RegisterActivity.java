package com.example.redsocial;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    static final int PHOTO_CHOOSER = 123;

    EditText etNombre, etApellido, etCorreo, etContrasena;
    ProgressBar pb;
    Button btnRegister;
    ImageView btnFoto;
    String nombre, apellido, correo, contrasena;
    private Uri selectedImage;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private String uploadImage(Uri photo){
        /*ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Registrando...");
        pd.show();*/

        String uuid = "profilePhotos/"+ UUID.randomUUID().toString() ;

        StorageReference ref = storageReference.child(uuid);
        ref.putFile(photo)
                .addOnCompleteListener(task -> {
                    //pd.dismiss();
                    Toast.makeText(getApplicationContext(), "La foto se ha subido con exito",
                            Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    //pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Error al subir la imagen",
                            Toast.LENGTH_SHORT).show();
                }).addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    //pd.setMessage("Subiendo imagen... "+ (int)progress + "% completado");
                });

        return "gs://redsocial-dam.appspot.com/"+uuid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.txtRegisterNombre);
        etApellido = findViewById(R.id.txtRegisterApelldo);
        etCorreo = findViewById(R.id.txtRegisterCorreo);
        etContrasena = findViewById(R.id.txtRegisterContrasenna);
        pb = findViewById(R.id.progressBarRegister);
        btnRegister = findViewById(R.id.btnRegistrarse);
        btnFoto = findViewById(R.id.btnFoto);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                nombre = etNombre.getText().toString().trim();
                apellido = etApellido.getText().toString().trim();
                correo = etCorreo.getText().toString().trim();
                contrasena = etContrasena.getText().toString().trim();
                boolean error = false;
                if(nombre.equals("")){
                    etNombre.setError("Debe indicar su nombre");
                    error = true;
                }
                if(apellido.equals("")){
                    etApellido.setError("Debe indicar minimo un apellido");
                    error = true;
                }
                if(correo.equals("")){
                    etCorreo.setError("Debe indicar su correo");
                    error = true;
                }
                if(contrasena.equals("")){
                    etContrasena.setError("Debe indicar una contrasena");
                    error = true;
                }
                if(contrasena.length() < 6){
                    etContrasena.setError("La contrasena debe ser mayor a 6 digitos");
                    error = true;
                }
                if(selectedImage == null){
                    Toast.makeText(RegisterActivity.this, "DEBE SELECCIONAR UNA FOTO DE PERFIL.",
                            Toast.LENGTH_SHORT).show();
                    btnFoto.setBackgroundColor(getResources().getColor(R.color.colorError));
                    error = true;
                }
                if (!error){
                    nombre += " " + apellido;
                    registrarUsuario(nombre, correo, contrasena, selectedImage);
                }

            }
        });

        btnFoto.setOnClickListener(v -> {
            Intent getImageIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(getImageIntent, PHOTO_CHOOSER);
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(requestCode == PHOTO_CHOOSER && resultCode == RESULT_OK && imageReturnedIntent != null && imageReturnedIntent.getData() != null)
        {
            selectedImage = imageReturnedIntent.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            btnFoto.setImageURI(selectedImage);
        }
    }

    private void registrarUsuario(String nombre, String email, String password, Uri photo) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("RegistrarUsuaio: ", "El usuario se ha creado con exito");
                            FirebaseUser user = mAuth.getCurrentUser();

                            String uid = user.getUid();
                            //Registro en BD
                            final HashMap<Object, String> hashMap = new HashMap<>();
                            //Agregamos los datos del usuario nuevo
                            hashMap.put("email", user.getEmail());
                            hashMap.put("uid", uid);
                            hashMap.put("name", nombre);

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("Users");
                            reference.child(uid).setValue(hashMap);

                            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("profilePhotos");
                            StorageTask mUploadTask;
                            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(photo));
                            mUploadTask = fileReference.putFile(photo)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    hashMap.put("photo", uri.toString());
                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                    DocumentReference userDocument = db.collection("Users").document();
                                                    userDocument.set(hashMap);
                                                }
                                            });
                                        }
                                    });

                            pb.setVisibility(View.INVISIBLE);
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userDocument = db.collection("Friends").document();
                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("friend", user.getEmail());
                            hashMap1.put("user", user.getEmail());
                            userDocument.set(hashMap1);

                            pb.setVisibility(View.INVISIBLE);
                            goMain();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("RegistrarUsuaio: ", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "La creacion del usuario nuevo fallo.\nEl correo ya se encuentra registrado",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pb.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "El correo ya se encuentra registrado en el sistema.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void goMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

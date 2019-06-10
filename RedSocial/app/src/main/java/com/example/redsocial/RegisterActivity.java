package com.example.redsocial;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText etNombre, etApellido, etCorreo, etContrasena;
    ProgressBar pb;
    Button btnRegister;
    String nombre, apellido, correo, contrasena;

    private FirebaseAuth mAuth;

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
                if (!error){
                    nombre += " " + apellido;
                    registrarUsuario(nombre, correo, contrasena);
                }

            }
        });




    }

    private void registrarUsuario(String nombre, String email, String password) {
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
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //Agregamos los datos del usuario nuevo
                            hashMap.put("email", user.getEmail());
                            hashMap.put("uid", uid);
                            hashMap.put("name", nombre);
                            hashMap.put("photo", "");
                            //Iniciamos la BD
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //Path para guardar los usuarios
                            DatabaseReference reference = database.getReference("Users");
                            reference.child(uid).setValue(hashMap);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userDocument = db.collection("Users").document();
                            userDocument.set(hashMap);



                            pb.setVisibility(View.INVISIBLE);
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
}

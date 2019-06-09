package com.example.redsocial;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.example.redsocial.utils.UserPreferences;

import java.util.HashMap;

public class LogInActivity extends AppCompatActivity {
    static final int GOOGLE_SIGN_IN = 123;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    Button btnLogIn, btnGoRegister, btnLoginGoogle;
    ProgressBar progressBar;
    UserPreferences prefs;
    Activity context;
    EditText etCorreo, etContrasena;
    String correo, contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_layout);
        context = this;

        progressBar = findViewById(R.id.progressCircular);

        etContrasena = findViewById(R.id.txtContrasenna);
        etCorreo = findViewById(R.id.txtCorreo);
        
        btnLogIn = findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithEmail();
            }
        });

        btnLoginGoogle = findViewById(R.id.loginGoogle);
        btnLoginGoogle.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLoginGoogle();
            }
        });

        btnGoRegister = findViewById(R.id.btnGoRegistro);
        btnGoRegister.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRegister();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        progressBar.setVisibility(View.INVISIBLE);
        prefs = new UserPreferences(context);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);

                        Log.d("TAG", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        if(task.getResult().getAdditionalUserInfo().isNewUser() ){
                            //Registro en BD
                            String uid = user.getUid();
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //Agregamos los datos del usuario nuevo
                            hashMap.put("email", user.getEmail());
                            hashMap.put("uid", uid);
                            hashMap.put("name", user.getDisplayName());
                            hashMap.put("photo", String.valueOf(user.getPhotoUrl()));
                            //Iniciamos la BD
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //Path para guardar los usuarios
                            DatabaseReference reference = database.getReference("Users");
                            reference.child(uid).setValue(hashMap);
                        }



                        updateUI(user);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);

                        Log.w("TAG", "signInWithCredential:failure", task.getException());

                        Toast.makeText(LogInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String id = user.getUid();
            String name = user.getDisplayName();
            String msgBienvenida = "Bienvenido de vuelta "+name;
            Toast.makeText(LogInActivity.this, msgBienvenida,
                    Toast.LENGTH_SHORT).show();

            String email = user.getEmail();
            String photo = String.valueOf(user.getPhotoUrl());

            prefs.saveUser(id, name, email, photo);
            prefs.saveLastLogin();
            progressBar.setVisibility(View.INVISIBLE);
            goMain();
            
        } else {
            if (!prefs.isEmpty())
                prefs.clear();
            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    private void goMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void goLoginGoogle(){
        if (mGoogleSignInClient == null)
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Logout();
        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);


    }

    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> updateUI(null));
    }

    private void loginWithEmail(){
        contrasena = etContrasena.getText().toString().trim();
        correo = etCorreo.getText().toString().trim();
        boolean error = false;
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
            mAuth.signInWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("SignIn", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(getApplicationContext(), "Exito al intentar ingresar.",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference reference = firebaseDatabase.getReference("Users");
                                Query query = reference.orderByChild("email").equalTo(user.getEmail());
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren() ){
                                            String id = "" + ds.child("uid").getValue();
                                            String email = "" + ds.child("email").getValue();
                                            String name = "" + ds.child("name").getValue();
                                            String photo = "" + ds.child("photo").getValue();
                                            prefs.saveUser(id, name, email, photo);
                                            prefs.saveLastLogin();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                goMain();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("SignIn", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Hubo un error al intentar ingresar con los datos propoorcionados.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // ...
                        }
                    });
        }
    }
}

package com.example.redsocial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LogInActivity extends AppCompatActivity {

    Button btnLogIn, btnGoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_layout);
        
        btnLogIn = findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMain();
            }
        });

        btnGoRegister = findViewById(R.id.btnGoRegistro);
        btnGoRegister.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRegister();
            }
        });
    }

    private void goMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}

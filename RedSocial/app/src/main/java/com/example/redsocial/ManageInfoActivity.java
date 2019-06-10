package com.example.redsocial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ManageInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_info);
    }

    public void AddInfo(View view) {
        TextView textView =  findViewById(R.id.editTextinfo);
        String UserBasicInfo = textView.getText().toString();

        //guardar UserBasicInfo en la table de datos personales, en conjunto con el id del usuario


    }
}

package com.example.redsocial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.redsocial.utils.UserPreferences;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;

public class ManageInfoActivity extends AppCompatActivity {

    private TextView txtInfo;
    private Button add;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_info);
        userPreferences = new UserPreferences(this);

        txtInfo = findViewById(R.id.editTextinfo);
        add = findViewById(R.id.btnAddInfo);
        add.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtInfo.getText().toString().equals("")){
                    HashMap<String, String> info = new HashMap<>();
                    info.put("userID", userPreferences.getEmail());
                    info.put("data", txtInfo.getText().toString());

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference postDocument = db.collection("PersonalData").document();
                    postDocument.set(info);
                    closePage();
                }
            }
        });
    }

    private void closePage() {
        this.finish();
    }


}

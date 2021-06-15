package com.example.norvid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class adminActivity extends AppCompatActivity {

    Button logout;
    TextView ccaa, prov, mun;
    EditText email, pass, bbddccaa, bbddprov, bbddmun;
    CheckBox ccaaCheckBox, provCheckBox, munCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        logout = findViewById(R.id.logout);

        email = findViewById(R.id.emailup);
        pass = findViewById(R.id.passup);

        ccaa = findViewById(R.id.ccaaAdmin);
        prov = findViewById(R.id.provAdmin);
        mun = findViewById(R.id.munAdmin);

        bbddccaa = findViewById(R.id.bbddccaaAdmin);
        bbddprov = findViewById(R.id.bbddPAdmin);
        bbddmun = findViewById(R.id.bbddMAdmin);

        ccaaCheckBox = findViewById(R.id.ccaaCheckBox);
        provCheckBox = findViewById(R.id.provCheckBox);
        munCheckBox = findViewById(R.id.munCheckBox);

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent myIntent = new Intent(adminActivity.this, MainActivity.class);
            adminActivity.this.startActivity(myIntent);
        });
    }

}
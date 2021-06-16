package com.example.norvid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class adminActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button logout, crear;
    TextView ccaa, prov, mun;
    EditText email, pass, bbddccaa, bbddprov, bbddmun;
    CheckBox ccaaCheckBox, provCheckBox, munCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        logout = findViewById(R.id.logout);
        crear = findViewById(R.id.crear);

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

        setup();

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent myIntent = new Intent(adminActivity.this, MainActivity.class);
            adminActivity.this.startActivity(myIntent);
        });

        crear.setOnClickListener(v -> {
            if(email.getText()!=null && pass.getText()!=null){
                 FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                     .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if (task.isSuccessful()) {
                                 DocumentReference dbuser = db.collection("users").document(email.getText().toString());
                                 Map<String, Object> data = new HashMap<>();

                                 if(!bbddccaa.getText().toString().equals(""))
                                     data.put("CCAA", bbddccaa.getText().toString());
                                 else
                                     data.put("CCAA", "null");

                                 if(!bbddprov.getText().toString().equals(""))
                                     data.put("Prov", bbddprov.getText().toString());
                                 else
                                     data.put("Prov", "null");

                                 if(!bbddmun.getText().toString().equals(""))
                                     data.put("Mun", bbddmun.getText().toString());
                                 else
                                     data.put("Mun", "null");

                                 dbuser.set(data);
                             }
                             else{
                                 AlertDialog.Builder error = new AlertDialog.Builder(adminActivity.this);
                                 error.setTitle("Error");
                                 error.setMessage("Algo salio mal.");
                                 error.setPositiveButton("Aceptar", null);

                                 AlertDialog dialog = error.create();
                                 dialog.show();
                             }
                         }
                     });
            }
        });

        setOnCheckListeners();
    }

    private void setup(){
        ccaa.setTextColor(Color.parseColor("#222222"));
        bbddccaa.setTextColor(Color.parseColor("#222222"));
        bbddccaa.setEnabled(false);
        prov.setTextColor(Color.parseColor("#222222"));
        bbddprov.setTextColor(Color.parseColor("#222222"));
        bbddprov.setEnabled(false);
        bbddprov.setText("");
        mun.setTextColor(Color.parseColor("#222222"));
        bbddmun.setTextColor(Color.parseColor("#222222"));
        bbddmun.setEnabled(false);
        bbddmun.setText("");
    }

    private void setOnCheckListeners() {

        ccaaCheckBox.setOnClickListener(v -> {
            if (ccaaCheckBox.isChecked()) {
                ccaa.setTextColor(Color.parseColor("#BBBBBB"));
                bbddccaa.setTextColor(Color.parseColor("#BBBBBB"));
                bbddccaa.setEnabled(true);
            }
            else {
                ccaa.setTextColor(Color.parseColor("#222222"));
                bbddccaa.setTextColor(Color.parseColor("#222222"));
                bbddccaa.setEnabled(false);
                bbddccaa.setText("");
            }
        });

        provCheckBox.setOnClickListener(v -> {
            if (provCheckBox.isChecked()) {
                prov.setTextColor(Color.parseColor("#BBBBBB"));
                bbddprov.setTextColor(Color.parseColor("#BBBBBB"));
                bbddprov.setEnabled(true);
            }
            else {
                prov.setTextColor(Color.parseColor("#222222"));
                bbddprov.setTextColor(Color.parseColor("#222222"));
                bbddprov.setEnabled(false);
                bbddprov.setText("");
            }
        });

        munCheckBox.setOnClickListener(v -> {
            if (munCheckBox.isChecked()) {
                mun.setTextColor(Color.parseColor("#BBBBBB"));
                bbddmun.setTextColor(Color.parseColor("#BBBBBB"));
                bbddmun.setEnabled(true);
            } else {
                mun.setTextColor(Color.parseColor("#222222"));
                bbddmun.setTextColor(Color.parseColor("#222222"));
                bbddmun.setEnabled(false);
                bbddmun.setText("");
            }
        });
    }

}
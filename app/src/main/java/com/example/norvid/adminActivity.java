package com.example.norvid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class adminActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    int lid = 4000;
    ArrayList<Integer> ccaas = new ArrayList<>();
    ArrayList<Integer> provs = new ArrayList<>();
    ArrayList<Integer> muns = new ArrayList<>();

    ConstraintLayout clccaa, clprov, clmun;
    Button logout, crear, buttonDCCAA, buttonDprov, buttonDmun;
    TextView ccaa, prov, mun;
    EditText email, pass;
    AutoCompleteTextView autoccaa, autoprov, automun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        clccaa = findViewById(R.id.ccaacl);
        clprov = findViewById(R.id.provcl);
        clmun = findViewById(R.id.muncl);

        buttonDCCAA = findViewById(R.id.buttonDCCAA);
        buttonDprov = findViewById(R.id.buttonDprov);
        buttonDmun = findViewById(R.id.buttonDmun);

        logout = findViewById(R.id.logout);
        crear = findViewById(R.id.crear);

        email = findViewById(R.id.emailup);
        pass = findViewById(R.id.passup);

        ccaa = findViewById(R.id.ccaaAdmin);
        prov = findViewById(R.id.provAdmin);
        mun = findViewById(R.id.munAdmin);

        autoccaa = clccaa.findViewById(R.id.autoccaa);
        autoprov = clprov.findViewById(R.id.autoprov);
        automun = clmun.findViewById(R.id.automun);

        db.collection("Listas").document("CCAA").get()
            .addOnSuccessListener(documentSnapshot -> {
                ArrayList<String> ccaaList = (ArrayList<String>) documentSnapshot.getData().get("lista");
                String[] arrayAdapter = new String[ccaaList.size()];
                for(int i = 0; i < ccaaList.size(); i++)
                    arrayAdapter[i] = ccaaList.get(i);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayAdapter);
                autoccaa.setAdapter(adapter);

                autoccaa.setOnItemClickListener((parent, view, position, id) -> {
                    ConstraintSet set = new ConstraintSet();
                    TextView v = new TextView(adminActivity.this);
                    v.setId(lid);
                    ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    v.setText(parent.getItemAtPosition(position).toString());
                    v.setTextSize(17);
                    v.setTextColor(Color.parseColor("#BBBBBB"));
                    clccaa.addView(v, lp);
                    set.clone(clccaa);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) autoccaa.getLayoutParams();
                    set.connect(v.getId(), ConstraintSet.TOP, params.topToBottom, ConstraintSet.BOTTOM,10);
                    set.connect(v.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 30);
                    set.connect(autoccaa.getId(), ConstraintSet.TOP, v.getId(), ConstraintSet.BOTTOM, 10);
                    set.applyTo(clccaa);
                    ccaas.add(v.getId());
                    lid++;
                    autoccaa.setText("");
                });
            });

        db.collection("Listas").document("Provincias").get()
                .addOnSuccessListener(documentSnapshot -> {
                    ArrayList<String> provList = (ArrayList<String>) documentSnapshot.getData().get("lista");
                    String[] arrayAdapter = new String[provList.size()];
                    for(int i = 0; i < provList.size(); i++)
                        arrayAdapter[i] = provList.get(i);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayAdapter);
                    autoprov.setAdapter(adapter);

                    autoprov.setOnItemClickListener((parent, view, position, id) -> {
                        ConstraintSet set = new ConstraintSet();
                        TextView v = new TextView(adminActivity.this);
                        v.setId(lid);
                        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                        v.setText(parent.getItemAtPosition(position).toString());
                        v.setTextSize(17);
                        v.setTextColor(Color.parseColor("#BBBBBB"));
                        clprov.addView(v, lp);
                        set.clone(clprov);
                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) autoprov.getLayoutParams();
                        set.connect(v.getId(), ConstraintSet.TOP, params.topToBottom, ConstraintSet.BOTTOM,10);
                        set.connect(v.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 30);
                        set.connect(autoprov.getId(), ConstraintSet.TOP, v.getId(), ConstraintSet.BOTTOM, 10);
                        set.applyTo(clprov);
                        provs.add(v.getId());
                        lid++;
                        autoprov.setText("");
                    });
                });

        db.collection("Listas").document("Municipios").get()
                .addOnSuccessListener(documentSnapshot -> {
                    ArrayList<String> munList = (ArrayList<String>) documentSnapshot.getData().get("lista");
                    String[] arrayAdapter = new String[munList.size()];
                    for(int i = 0; i < munList.size(); i++)
                        arrayAdapter[i] = munList.get(i);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayAdapter);
                    automun.setAdapter(adapter);

                    automun.setOnItemClickListener((parent, view, position, id) -> {
                        ConstraintSet set = new ConstraintSet();
                        TextView v = new TextView(adminActivity.this);
                        v.setId(lid);
                        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                        v.setText(parent.getItemAtPosition(position).toString());
                        v.setTextSize(17);
                        v.setTextColor(Color.parseColor("#BBBBBB"));
                        clmun.addView(v, lp);
                        set.clone(clmun);
                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) automun.getLayoutParams();
                        set.connect(v.getId(), ConstraintSet.TOP, params.topToBottom, ConstraintSet.BOTTOM,10);
                        set.connect(v.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 30);
                        set.connect(automun.getId(), ConstraintSet.TOP, v.getId(), ConstraintSet.BOTTOM, 10);
                        set.applyTo(clmun);
                        muns.add(v.getId());
                        lid++;
                        automun.setText("");
                    });
                });

        buttonDCCAA.setOnClickListener(v -> {
            for(int i = 0; i < ccaas.size(); i++)
                clccaa.removeView(clccaa.findViewById(ccaas.get(i)));
            ccaas.clear();
            ConstraintSet set = new ConstraintSet();
            set.clone(clccaa);
            set.connect(autoccaa.getId(),ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 10);
            set.applyTo(clccaa);
        });

        buttonDprov.setOnClickListener(v -> {
            for(int i = 0; i < provs.size(); i++)
                clprov.removeView(clprov.findViewById(provs.get(i)));
            provs.clear();
            ConstraintSet set = new ConstraintSet();
            set.clone(clprov);
            set.connect(autoprov.getId(),ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 10);
            set.applyTo(clprov);
        });

        buttonDmun.setOnClickListener(v -> {
            for(int i = 0; i < muns.size(); i++)
                clmun.removeView(clmun.findViewById(muns.get(i)));
            muns.clear();
            ConstraintSet set = new ConstraintSet();
            set.clone(clmun);
            set.connect(automun.getId(),ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 10);
            set.applyTo(clmun);
        });

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent myIntent = new Intent(adminActivity.this, MainActivity.class);
            adminActivity.this.startActivity(myIntent);
        });

        crear.setOnClickListener(v -> {
            if(email.getText()!=null && !email.getText().toString().equals("") && pass.getText()!=null && !pass.getText().toString().equals("")){
                 FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                     .addOnCompleteListener(adminActivity.this, task -> {
                         if (task.isSuccessful()) {
                             Map<String, Object> data = new HashMap<>();
                             ArrayList<String> nccaa = new ArrayList<>();
                             ArrayList<String> nprov= new ArrayList<>();
                             ArrayList<String> nmun = new ArrayList<>();
                             for(int i = 0; i < ccaas.size(); i++) {
                                 TextView v1 = clccaa.findViewById(ccaas.get(i));
                                 nccaa.add(v1.getText().toString());
                             }
                             data.put("CCAA", nccaa);
                             for(int i = 0; i < provs.size(); i++) {
                                 TextView v1 = clprov.findViewById(provs.get(i));
                                 nprov.add(v1.getText().toString());
                             }
                             data.put("Prov", nprov);
                             for(int i = 0; i < muns.size(); i++) {
                                 TextView v1 = clmun.findViewById(muns.get(i));
                                 nmun.add(v1.getText().toString());
                             }
                             data.put("Mun", nmun);

                             db.collection("users").document(email.getText().toString()).set(data);

                             AlertDialog.Builder error = new AlertDialog.Builder(adminActivity.this);
                             error.setTitle("Completo");
                             error.setMessage("Cuenta creada con éxito");
                             error.setPositiveButton("Aceptar", null);

                             AlertDialog dialog = error.create();
                             dialog.show();
                         }
                         else{
                             AlertDialog.Builder error = new AlertDialog.Builder(adminActivity.this);
                             error.setTitle("Error");
                             error.setMessage("Usuario ya existente");
                             error.setPositiveButton("Aceptar", null);

                             AlertDialog dialog = error.create();
                             dialog.show();
                         }
                     });
            }
            else{
                AlertDialog.Builder error = new AlertDialog.Builder(adminActivity.this);
                error.setTitle("Error");
                error.setMessage("Rellenar los campos de email y contraseña");
                error.setPositiveButton("Aceptar", null);

                AlertDialog dialog = error.create();
                dialog.show();
            }
        });
    }
}
package com.example.norvid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertRestrictions extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "DocSnippets";
    public static final int FAST_UPDATE_INTERVAL = 5;

    TextView cp, slash, tdq, r, hmh, i, e, hmon;
    EditText bbddtdq, bbddtdq2, bbddr, bbddhmh, bbddi, bbdde, bbddhmon;
    TextView ccaatext;
    CheckBox bbddcp, tdqCheckBox, rCheckBox, hmhCheckBox, iCheckBox, eCheckBox, hmonCheckBox;
    String mun, provUser, ccaaUser;
    Button button, logout;

    FusedLocationProviderClient fusedLocationProviderClient;

    LocationRequest locationRequest;

    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_restrictions);

        String emailUse = getIntent().getStringExtra("email");

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(FAST_UPDATE_INTERVAL * 1000);
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        setup(location);
                    }
                }
            }
        };
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(InsertRestrictions.this);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }

        provUser = "Ninguno asignado";
        ccaaUser = "Ninguno asignado";

        tdqCheckBox = findViewById(R.id.tdqCheckBox);
        rCheckBox = findViewById(R.id.rCheckBox);
        hmhCheckBox = findViewById(R.id.hmhCheckBox);
        iCheckBox = findViewById(R.id.iCheckBox);
        eCheckBox = findViewById(R.id.eCheckBox);
        hmonCheckBox = findViewById(R.id.hmonCheckBox);

        cp = findViewById(R.id.cp);
        tdq = findViewById(R.id.tdq);
        slash = findViewById(R.id.slash);
        r = findViewById(R.id.r);
        hmh = findViewById(R.id.hmh);
        i = findViewById(R.id.i);
        e = findViewById(R.id.e);
        hmon = findViewById(R.id.hmon);

        bbddtdq = findViewById(R.id.bbddtdq);
        bbddtdq2 = findViewById(R.id.bbddtdq2);
        bbddcp = findViewById(R.id.bbddcp);
        bbddr = findViewById(R.id.ddbbr);
        bbddhmh = findViewById(R.id.bbddthmh);
        bbddi = findViewById(R.id.ddbbi);
        bbdde = findViewById(R.id.ddbbe);
        bbddhmon = findViewById(R.id.bbddhmon);
        ccaatext = findViewById(R.id.ccaatext);

        button = findViewById(R.id.savebutton);
        logout = findViewById(R.id.logOutButton);

        DocumentReference dbuser = db.collection("users").document(emailUse);
        dbuser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ccaaUser = document.getData().get("CCAA").toString();
                    ccaatext.setText(ccaaUser);
                    DocumentReference ccaaField = db.collection("CCAA").document(ccaaUser);
                    ccaaField.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot ccaaE = task1.getResult();
                            if (ccaaE.exists()) {
                                String toqueDeQueda = ccaaE.getData().get("tdq").toString();
                                if(!toqueDeQueda.equals("-")) {
                                    int barra = toqueDeQueda.indexOf('-');
                                    bbddtdq.setText(toqueDeQueda.substring(0, barra));
                                    bbddtdq2.setText(toqueDeQueda.substring(barra + 1, toqueDeQueda.length()));
                                    tdqCheckBox.setChecked(true);
                                }
                                else {
                                    tdq.setTextColor(Color.parseColor("#222222"));
                                    slash.setTextColor(Color.parseColor("#222222"));
                                    bbddtdq.setTextColor(Color.parseColor("#222222"));
                                    bbddtdq.setEnabled(false);
                                    bbddtdq2.setTextColor(Color.parseColor("#222222"));
                                    bbddtdq2.setEnabled(false);
                                    tdqCheckBox.setChecked(false);
                                }

                                bbddcp.setChecked((Boolean) ccaaE.getData().get("cp"));
                                if(!bbddcp.isChecked())
                                    cp.setTextColor(Color.parseColor("#222222"));

                                String rddbb = ccaaE.getData().get("r").toString();
                                if(!rddbb.equals("")) {
                                    bbddr.setText(rddbb);
                                    rCheckBox.setChecked(true);
                                }
                                else{
                                    r.setTextColor(Color.parseColor("#222222"));
                                    bbddr.setTextColor(Color.parseColor("#222222"));
                                    bbddr.setEnabled(false);
                                    rCheckBox.setChecked(false);
                                }

                                String hmhddbb = ccaaE.getData().get("hmh").toString();
                                if(!hmhddbb.equals("")){
                                    bbddhmh.setText(hmhddbb);
                                    hmhCheckBox.setChecked(true);
                                }
                                else{
                                    hmh.setTextColor(Color.parseColor("#222222"));
                                    bbddhmh.setTextColor(Color.parseColor("#222222"));
                                    bbddhmh.setEnabled(false);
                                    hmhCheckBox.setChecked(false);
                                }

                                String iddbb = ccaaE.getData().get("i").toString();
                                if(!iddbb.equals("")){
                                    bbddi.setText(iddbb);
                                    iCheckBox.setChecked(true);
                                }
                                else{
                                    i.setTextColor(Color.parseColor("#222222"));
                                    bbddi.setTextColor(Color.parseColor("#222222"));
                                    bbddi.setEnabled(false);
                                    iCheckBox.setChecked(false);
                                }

                                String eddbb = ccaaE.getData().get("e").toString();
                                if(!eddbb.equals("")){
                                    bbdde.setText(eddbb);
                                    eCheckBox.setChecked(true);
                                }
                                else{
                                    e.setTextColor(Color.parseColor("#222222"));
                                    bbdde.setTextColor(Color.parseColor("#222222"));
                                    bbdde.setEnabled(false);
                                    eCheckBox.setChecked(false);
                                }

                                String hmonddbb = ccaaE.getData().get("hmon").toString();
                                if(!hmonddbb.equals("")){
                                    bbddhmon.setText(hmonddbb);
                                    hmonCheckBox.setChecked(true);
                                }
                                else{
                                    hmon.setTextColor(Color.parseColor("#222222"));
                                    bbddhmon.setTextColor(Color.parseColor("#222222"));
                                    bbddhmon.setEnabled(false);
                                    hmonCheckBox.setChecked(false);
                                }
                            }
                        }
                    });
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                DocumentReference dbgetter = db.collection("users").document(emailUse);
                dbgetter.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ccaaUser = document.getData().get("CCAA").toString();
                                Map<String, Object> data = new HashMap<>();

                                data.put("tdq", bbddtdq.getText().toString() + "-" + bbddtdq2.getText().toString());
                                data.put("cp", bbddcp.isChecked());
                                data.put("r", bbddr.getText().toString());
                                data.put("hmh", bbddhmh.getText().toString());
                                data.put("i", bbddi.getText().toString());
                                data.put("e", bbdde.getText().toString());
                                data.put("hmon", bbddhmon.getText().toString());

                                DocumentReference dbField = db.collection("CCAA").document(ccaaUser);
                                dbField.set(data);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent myIntent = new Intent(InsertRestrictions.this, MainActivity.class);
                InsertRestrictions.this.startActivity(myIntent);
            }
        });

        setOnCheckListeners();
    }

    private void setOnCheckListeners(){

        bbddcp.setOnClickListener(v -> {
            if(bbddcp.isChecked())
                cp.setTextColor(Color.parseColor("#BBBBBB"));
            else
                cp.setTextColor(Color.parseColor("#222222"));
        });

        tdqCheckBox.setOnClickListener(v -> {
            if(tdqCheckBox.isChecked()) {
                tdq.setTextColor(Color.parseColor("#BBBBBB"));
                slash.setTextColor(Color.parseColor("#BBBBBB"));
                bbddtdq.setTextColor(Color.parseColor("#BBBBBB"));
                bbddtdq.setEnabled(true);
                bbddtdq2.setTextColor(Color.parseColor("#BBBBBB"));
                bbddtdq2.setEnabled(true);
            }
            else{
                tdq.setTextColor(Color.parseColor("#222222"));
                slash.setTextColor(Color.parseColor("#222222"));
                bbddtdq.setTextColor(Color.parseColor("#222222"));
                bbddtdq.setEnabled(false);
                bbddtdq.setText("");
                bbddtdq2.setTextColor(Color.parseColor("#222222"));
                bbddtdq2.setEnabled(false);
                bbddtdq2.setText("");
            }
        });

        rCheckBox.setOnClickListener(v -> {
            if(rCheckBox.isChecked()){
                r.setTextColor(Color.parseColor("#BBBBBB"));
                bbddr.setTextColor(Color.parseColor("#BBBBBB"));
                bbddr.setEnabled(true);
            }
            else{
                r.setTextColor(Color.parseColor("#222222"));
                bbddr.setTextColor(Color.parseColor("#222222"));
                bbddr.setEnabled(false);
                rCheckBox.setChecked(false);
                bbddr.setText("");
            }
        });

        hmhCheckBox.setOnClickListener(v -> {
            if(hmhCheckBox.isChecked()){
                hmh.setTextColor(Color.parseColor("#BBBBBB"));
                bbddhmh.setTextColor(Color.parseColor("#BBBBBB"));
                bbddhmh.setEnabled(true);
            }
            else{
                hmh.setTextColor(Color.parseColor("#222222"));
                bbddhmh.setTextColor(Color.parseColor("#222222"));
                bbddhmh.setEnabled(false);
                hmhCheckBox.setChecked(false);
                bbddhmh.setText("");
            }
        });

        iCheckBox.setOnClickListener(v -> {
            if(iCheckBox.isChecked()){
                i.setTextColor(Color.parseColor("#BBBBBB"));
                bbddi.setTextColor(Color.parseColor("#BBBBBB"));
                bbddi.setEnabled(true);
            }
            else{
                i.setTextColor(Color.parseColor("#222222"));
                bbddi.setTextColor(Color.parseColor("#222222"));
                bbddi.setEnabled(false);
                iCheckBox.setChecked(false);
                bbddi.setText("");
            }
        });

        eCheckBox.setOnClickListener(v -> {
            if(eCheckBox.isChecked()){
                e.setTextColor(Color.parseColor("#BBBBBB"));
                bbdde.setTextColor(Color.parseColor("#BBBBBB"));
                bbdde.setEnabled(true);
            }
            else{
                e.setTextColor(Color.parseColor("#222222"));
                bbdde.setTextColor(Color.parseColor("#222222"));
                bbdde.setEnabled(false);
                eCheckBox.setChecked(false);
                bbdde.setText("");
            }
        });

        hmonCheckBox.setOnClickListener(v -> {
            if(hmonCheckBox.isChecked()){
                hmon.setTextColor(Color.parseColor("#BBBBBB"));
                bbddhmon.setTextColor(Color.parseColor("#BBBBBB"));
                bbddhmon.setEnabled(true);
            }
            else{
                hmon.setTextColor(Color.parseColor("#222222"));
                bbddhmon.setTextColor(Color.parseColor("#222222"));
                bbddhmon.setEnabled(false);
                hmonCheckBox.setChecked(false);
                bbddhmon.setText("");
            }
        });

    }

    private void setup(Location location){
        try {
            Geocoder geocoder = new Geocoder(InsertRestrictions.this);
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            mun = addresses.get(0).getLocality();
        }catch(Exception e){
            Log.d(TAG,"Error: " + e);
        }
    }
}
package com.example.norvid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertRestrictions extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "DocSnippets";
    public static final int FAST_UPDATE_INTERVAL = 5;
    int lid = 2000;

    TextView cp, slash, tdq, r, hmh, i, e, hmon;
    EditText bbddtdq, bbddtdq2, bbddr, bbddhmh, bbddi, bbdde, bbddhmon;

    TextView pcp, pslash, ptdq, pr, phmh, pi, pe, phmon;
    EditText pbbddtdq, pbbddtdq2, pbbddr, pbbddhmh, pbbddi, pbbdde, pbbddhmon;

    TextView mcp, mslash, mtdq, mr, mhmh, mi, me, mhmon;
    EditText mbbddtdq, mbbddtdq2, mbbddr, mbbddhmh, mbbddi, mbbdde, mbbddhmon;

    TextView ccaatext, provtext, muntext;

    CheckBox bbddcp, tdqCheckBox, rCheckBox, hmhCheckBox, iCheckBox, eCheckBox, hmonCheckBox;

    CheckBox pbbddcp, ptdqCheckBox, prCheckBox, phmhCheckBox, piCheckBox, peCheckBox, phmonCheckBox;

    CheckBox mbbddcp, mtdqCheckBox, mrCheckBox, mhmhCheckBox, miCheckBox, meCheckBox, mhmonCheckBox;

    String mun, munUser, provUser, ccaaUser;
    Button button, logout;

    FusedLocationProviderClient fusedLocationProviderClient;

    LocationRequest locationRequest;

    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_restrictions);

        String emailUse = getIntent().getStringExtra("email");

        provUser = "Ninguno asignado";
        ccaaUser = "Ninguno asignado";

        button = findViewById(R.id.savebutton);
        logout = findViewById(R.id.logOutButton);

        ConstraintLayout layoutGeneral = (ConstraintLayout) logout.getParent();

        DocumentReference dbuser = db.collection("users").document(emailUse);
        dbuser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    int idCabecera = -1;
                    if (document.getData().get("CCAA") != null) {
                        List<String> CCAAs = (List<String>) document.getData().get("CCAA");
                        Log.d(TAG, CCAAs.size() + "AAAAAAAAAA");
                        boolean first = true;
                        for (int i = 0; i < CCAAs.size(); i++) {
                            ConstraintSet set = new ConstraintSet();
                            TextView restriction = new TextView(InsertRestrictions.this);
                            Log.d(TAG,Integer.toString(lid));
                            restriction.setId(lid);
                            lid++;;
                            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            restriction.setText("Comunidad autónoma: " + CCAAs.get(i));
                            restriction.setTextSize(17);
                            restriction.setTextColor(Color.parseColor("#BBBBBB"));
                            layoutGeneral.addView(restriction, lp);
                            set.clone(layoutGeneral);
                            if(idCabecera!=-1)
                                set.connect(restriction.getId(),ConstraintSet.TOP,idCabecera,ConstraintSet.BOTTOM,650);
                            else
                                set.connect(restriction.getId(),ConstraintSet.TOP,logout.getId(),ConstraintSet.BOTTOM,20);

                            idCabecera=restriction.getId();
                            set.applyTo(layoutGeneral);
                            ConstraintSet scrollset = new ConstraintSet();
                            ScrollView scroll = new ScrollView(InsertRestrictions.this);
                            Log.d(TAG,Integer.toString(lid));
                            scroll.setId(lid);
                            lid++;
                            ConstraintLayout.LayoutParams lpscroll = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,500);
                            layoutGeneral.addView(scroll, lpscroll);
                            scrollset.clone(layoutGeneral);
                            scrollset.connect(scroll.getId(),ConstraintSet.TOP,restriction.getId(),ConstraintSet.BOTTOM, 10);
                            scrollset.applyTo(layoutGeneral);

                            String comunidad = CCAAs.get(i);

                            CollectionReference dbCCAA = db.collection("CCAA").document(comunidad).collection("Restricciones");
                            dbCCAA.get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    List<DocumentSnapshot> documents = task1.getResult().getDocuments();
                                    Log.d(TAG,documents.get(0).getData().get("0") + "AAAAAA");
                                    //try {
                                        DocumentSnapshot totry = documents.get(0);
                                        ConstraintLayout layout = (ConstraintLayout) findViewById(lid - 1);
                                        for (int j = 0; j < documents.size(); j++) {
                                            ConstraintSet setRes = new ConstraintSet();
                                            TextView restrictions = new TextView(InsertRestrictions.this);
                                            restrictions.setId(lid);
                                            lid++;
                                            ConstraintLayout.LayoutParams lpRes = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                                            restrictions.setText("Algo");
                                            restrictions.setTextSize(17);
                                            restrictions.setTextColor(Color.parseColor("#BBBBBB"));
                                            layout.addView(restrictions, lpRes);
                                            setRes.clone(layout);
                                            setRes.connect(restrictions.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                                            setRes.connect(restrictions.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                            setRes.applyTo(layout);
                                        }
                                    /*} catch (Exception err) {
                                        Log.d(TAG,"LOL");
                                    }*/
                                }
                            });
                        }
                    }
                    if (document.getData().get("Prov") != null) {
                        List<String> PROVs = (List<String>) document.getData().get("Prov");
                        boolean first = true;
                        for (int i = 0; i < PROVs.size(); i++) {
                            ConstraintSet set = new ConstraintSet();
                            TextView restriction = new TextView(InsertRestrictions.this);
                            Log.d(TAG,Integer.toString(lid));
                            restriction.setId(lid);
                            lid++;;
                            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            restriction.setText("Provincia: " + PROVs.get(i));
                            restriction.setTextSize(17);
                            restriction.setTextColor(Color.parseColor("#BBBBBB"));
                            layoutGeneral.addView(restriction, lp);
                            set.clone(layoutGeneral);
                            if(idCabecera!=-1)
                                set.connect(restriction.getId(),ConstraintSet.TOP,idCabecera,ConstraintSet.BOTTOM,650);
                            else
                                set.connect(restriction.getId(),ConstraintSet.TOP,logout.getId(),ConstraintSet.BOTTOM,20);

                            idCabecera=restriction.getId();
                            set.applyTo(layoutGeneral);
                            ConstraintSet scrollset = new ConstraintSet();
                            ScrollView scroll = new ScrollView(InsertRestrictions.this);
                            Log.d(TAG,Integer.toString(lid));
                            scroll.setId(lid);
                            lid++;
                            ConstraintLayout.LayoutParams lpscroll = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,500);
                            layoutGeneral.addView(scroll, lpscroll);
                            scrollset.clone(layoutGeneral);
                            scrollset.connect(scroll.getId(),ConstraintSet.TOP,restriction.getId(),ConstraintSet.BOTTOM, 10);
                            scrollset.applyTo(layoutGeneral);
                        }
                    }
                    if (document.getData().get("Mun") != null) {
                        List<String> MUNs = (List<String>) document.getData().get("Mun");
                        boolean first = true;
                        for (int i = 0; i < MUNs.size(); i++) {
                            ConstraintSet set = new ConstraintSet();
                            TextView restriction = new TextView(InsertRestrictions.this);
                            Log.d(TAG,Integer.toString(lid));
                            restriction.setId(lid);
                            lid++;;
                            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            restriction.setText("Municipio: " + MUNs.get(i));
                            restriction.setTextSize(17);
                            restriction.setTextColor(Color.parseColor("#BBBBBB"));
                            layoutGeneral.addView(restriction, lp);
                            set.clone(layoutGeneral);
                            if(idCabecera!=-1)
                                set.connect(restriction.getId(),ConstraintSet.TOP,idCabecera,ConstraintSet.BOTTOM,650);
                            else
                                set.connect(restriction.getId(),ConstraintSet.TOP,logout.getId(),ConstraintSet.BOTTOM,20);

                            idCabecera=restriction.getId();
                            set.applyTo(layoutGeneral);
                            ConstraintSet scrollset = new ConstraintSet();
                            ScrollView scroll = new ScrollView(InsertRestrictions.this);
                            Log.d(TAG,Integer.toString(lid));
                            scroll.setId(lid);
                            lid++;
                            ConstraintLayout.LayoutParams lpscroll = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,500);
                            layoutGeneral.addView(scroll, lpscroll);
                            scrollset.clone(layoutGeneral);
                            scrollset.connect(scroll.getId(),ConstraintSet.TOP,restriction.getId(),ConstraintSet.BOTTOM, 10);
                            scrollset.applyTo(layoutGeneral);
                        }
                    }
                }
            }
        });
/*
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
                                try{
                                    String toqueDeQueda = ccaaE.getData().get("tdq").toString();
                                    int barra = toqueDeQueda.indexOf('-');
                                    bbddtdq.setText(toqueDeQueda.substring(0, barra));
                                    bbddtdq2.setText(toqueDeQueda.substring(barra + 1, toqueDeQueda.length()));
                                    tdqCheckBox.setChecked(true);
                                }catch(Exception err) {
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

                    provUser = document.getData().get("Prov").toString();
                    provtext.setText(provUser);
                    DocumentReference provField = db.collection("Provincias").document(provUser);
                    provField.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot provE = task1.getResult();
                            if (provE.exists()) {
                                String toqueDeQueda = provE.getData().get("tdq").toString();
                                if(!toqueDeQueda.equals("-")) {
                                    int barra = toqueDeQueda.indexOf('-');
                                    pbbddtdq.setText(toqueDeQueda.substring(0, barra));
                                    pbbddtdq2.setText(toqueDeQueda.substring(barra + 1, toqueDeQueda.length()));
                                    ptdqCheckBox.setChecked(true);
                                }
                                else {
                                    ptdq.setTextColor(Color.parseColor("#222222"));
                                    pslash.setTextColor(Color.parseColor("#222222"));
                                    pbbddtdq.setTextColor(Color.parseColor("#222222"));
                                    pbbddtdq.setEnabled(false);
                                    pbbddtdq2.setTextColor(Color.parseColor("#222222"));
                                    pbbddtdq2.setEnabled(false);
                                    ptdqCheckBox.setChecked(false);
                                }

                                pbbddcp.setChecked((Boolean) provE.getData().get("cp"));
                                if(!pbbddcp.isChecked())
                                    pcp.setTextColor(Color.parseColor("#222222"));

                                String prddbb = provE.getData().get("r").toString();
                                if(!prddbb.equals("")) {
                                    pbbddr.setText(prddbb);
                                    prCheckBox.setChecked(true);
                                }
                                else{
                                    pr.setTextColor(Color.parseColor("#222222"));
                                    pbbddr.setTextColor(Color.parseColor("#222222"));
                                    pbbddr.setEnabled(false);
                                    prCheckBox.setChecked(false);
                                }

                                String phmhddbb = provE.getData().get("hmh").toString();
                                if(!phmhddbb.equals("")){
                                    pbbddhmh.setText(phmhddbb);
                                    phmhCheckBox.setChecked(true);
                                }
                                else{
                                    phmh.setTextColor(Color.parseColor("#222222"));
                                    pbbddhmh.setTextColor(Color.parseColor("#222222"));
                                    pbbddhmh.setEnabled(false);
                                    phmhCheckBox.setChecked(false);
                                }

                                String piddbb = provE.getData().get("i").toString();
                                if(!piddbb.equals("")){
                                    pbbddi.setText(piddbb);
                                    piCheckBox.setChecked(true);
                                }
                                else{
                                    pi.setTextColor(Color.parseColor("#222222"));
                                    pbbddi.setTextColor(Color.parseColor("#222222"));
                                    pbbddi.setEnabled(false);
                                    piCheckBox.setChecked(false);
                                }

                                String peddbb = provE.getData().get("e").toString();
                                if(!peddbb.equals("")){
                                    pbbdde.setText(peddbb);
                                    peCheckBox.setChecked(true);
                                }
                                else{
                                    pe.setTextColor(Color.parseColor("#222222"));
                                    pbbdde.setTextColor(Color.parseColor("#222222"));
                                    pbbdde.setEnabled(false);
                                    peCheckBox.setChecked(false);
                                }

                                String phmonddbb = provE.getData().get("hmon").toString();
                                if(!phmonddbb.equals("")){
                                    pbbddhmon.setText(phmonddbb);
                                    phmonCheckBox.setChecked(true);
                                }
                                else{
                                    phmon.setTextColor(Color.parseColor("#222222"));
                                    pbbddhmon.setTextColor(Color.parseColor("#222222"));
                                    pbbddhmon.setEnabled(false);
                                    phmonCheckBox.setChecked(false);
                                }
                            }
                        }
                    });

                    munUser = document.getData().get("Mun").toString();
                    muntext.setText(munUser);
                    DocumentReference munField = db.collection("Municipios").document(munUser);
                    munField.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot munE = task1.getResult();
                            if (munE.exists()) {
                                String toqueDeQueda = munE.getData().get("tdq").toString();
                                if(!toqueDeQueda.equals("-")) {
                                    int barra = toqueDeQueda.indexOf('-');
                                    mbbddtdq.setText(toqueDeQueda.substring(0, barra));
                                    mbbddtdq2.setText(toqueDeQueda.substring(barra + 1, toqueDeQueda.length()));
                                    mtdqCheckBox.setChecked(true);
                                }
                                else {
                                    mtdq.setTextColor(Color.parseColor("#222222"));
                                    mslash.setTextColor(Color.parseColor("#222222"));
                                    mbbddtdq.setTextColor(Color.parseColor("#222222"));
                                    mbbddtdq.setEnabled(false);
                                    mbbddtdq2.setTextColor(Color.parseColor("#222222"));
                                    mbbddtdq2.setEnabled(false);
                                    mtdqCheckBox.setChecked(false);
                                }

                                mbbddcp.setChecked((Boolean) munE.getData().get("cp"));
                                if(!mbbddcp.isChecked())
                                    mcp.setTextColor(Color.parseColor("#222222"));

                                String mrddbb = munE.getData().get("r").toString();
                                if(!mrddbb.equals("")) {
                                    mbbddr.setText(mrddbb);
                                    mrCheckBox.setChecked(true);
                                }
                                else{
                                    mr.setTextColor(Color.parseColor("#222222"));
                                    mbbddr.setTextColor(Color.parseColor("#222222"));
                                    mbbddr.setEnabled(false);
                                    mrCheckBox.setChecked(false);
                                }

                                String mhmhddbb = munE.getData().get("hmh").toString();
                                if(!mhmhddbb.equals("")){
                                    mbbddhmh.setText(mhmhddbb);
                                    mhmhCheckBox.setChecked(true);
                                }
                                else{
                                    mhmh.setTextColor(Color.parseColor("#222222"));
                                    mbbddhmh.setTextColor(Color.parseColor("#222222"));
                                    mbbddhmh.setEnabled(false);
                                    mhmhCheckBox.setChecked(false);
                                }

                                String middbb = munE.getData().get("i").toString();
                                if(!middbb.equals("")){
                                    mbbddi.setText(middbb);
                                    miCheckBox.setChecked(true);
                                }
                                else{
                                    mi.setTextColor(Color.parseColor("#222222"));
                                    mbbddi.setTextColor(Color.parseColor("#222222"));
                                    mbbddi.setEnabled(false);
                                    miCheckBox.setChecked(false);
                                }

                                String meddbb = munE.getData().get("e").toString();
                                if(!meddbb.equals("")){
                                    mbbdde.setText(meddbb);
                                    meCheckBox.setChecked(true);
                                }
                                else{
                                    me.setTextColor(Color.parseColor("#222222"));
                                    mbbdde.setTextColor(Color.parseColor("#222222"));
                                    mbbdde.setEnabled(false);
                                    meCheckBox.setChecked(false);
                                }

                                String mhmonddbb = munE.getData().get("hmon").toString();
                                if(!mhmonddbb.equals("")){
                                    mbbddhmon.setText(mhmonddbb);
                                    mhmonCheckBox.setChecked(true);
                                }
                                else{
                                    mhmon.setTextColor(Color.parseColor("#222222"));
                                    mbbddhmon.setTextColor(Color.parseColor("#222222"));
                                    mbbddhmon.setEnabled(false);
                                    mhmonCheckBox.setChecked(false);
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

                                provUser = document.getData().get("Prov").toString();
                                Map<String, Object> pdata = new HashMap<>();

                                pdata.put("tdq", pbbddtdq.getText().toString() + "-" + pbbddtdq2.getText().toString());
                                pdata.put("cp", pbbddcp.isChecked());
                                pdata.put("r", pbbddr.getText().toString());
                                pdata.put("hmh", pbbddhmh.getText().toString());
                                pdata.put("i", pbbddi.getText().toString());
                                pdata.put("e", pbbdde.getText().toString());
                                pdata.put("hmon", pbbddhmon.getText().toString());

                                DocumentReference pdbField = db.collection("Provincias").document(provUser);
                                pdbField.set(pdata);

                                munUser = document.getData().get("Mun").toString();
                                Map<String, Object> mdata = new HashMap<>();

                                mdata.put("tdq", mbbddtdq.getText().toString() + "-" + mbbddtdq2.getText().toString());
                                mdata.put("cp", mbbddcp.isChecked());
                                mdata.put("r", mbbddr.getText().toString());
                                mdata.put("hmh", mbbddhmh.getText().toString());
                                mdata.put("i", mbbddi.getText().toString());
                                mdata.put("e", mbbdde.getText().toString());
                                mdata.put("hmon", mbbddhmon.getText().toString());

                                DocumentReference mdbField = db.collection("Municipios").document(munUser);
                                mdbField.set(mdata);

                                Toast.makeText(InsertRestrictions.this, "Datos guardados", Toast.LENGTH_SHORT).show();

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        });*/

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
/*
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

        pbbddcp.setOnClickListener(v -> {
            if(pbbddcp.isChecked())
                pcp.setTextColor(Color.parseColor("#BBBBBB"));
            else
                pcp.setTextColor(Color.parseColor("#222222"));
        });

        ptdqCheckBox.setOnClickListener(v -> {
            if(ptdqCheckBox.isChecked()) {
                ptdq.setTextColor(Color.parseColor("#BBBBBB"));
                pslash.setTextColor(Color.parseColor("#BBBBBB"));
                pbbddtdq.setTextColor(Color.parseColor("#BBBBBB"));
                pbbddtdq.setEnabled(true);
                pbbddtdq2.setTextColor(Color.parseColor("#BBBBBB"));
                pbbddtdq2.setEnabled(true);
            }
            else{
                ptdq.setTextColor(Color.parseColor("#222222"));
                pslash.setTextColor(Color.parseColor("#222222"));
                pbbddtdq.setTextColor(Color.parseColor("#222222"));
                pbbddtdq.setEnabled(false);
                pbbddtdq.setText("");
                pbbddtdq2.setTextColor(Color.parseColor("#222222"));
                pbbddtdq2.setEnabled(false);
                pbbddtdq2.setText("");
            }
        });

        prCheckBox.setOnClickListener(v -> {
            if(prCheckBox.isChecked()){
                pr.setTextColor(Color.parseColor("#BBBBBB"));
                pbbddr.setTextColor(Color.parseColor("#BBBBBB"));
                pbbddr.setEnabled(true);
            }
            else{
                pr.setTextColor(Color.parseColor("#222222"));
                pbbddr.setTextColor(Color.parseColor("#222222"));
                pbbddr.setEnabled(false);
                prCheckBox.setChecked(false);
                pbbddr.setText("");
            }
        });

        phmhCheckBox.setOnClickListener(v -> {
            if(phmhCheckBox.isChecked()){
                phmh.setTextColor(Color.parseColor("#BBBBBB"));
                pbbddhmh.setTextColor(Color.parseColor("#BBBBBB"));
                pbbddhmh.setEnabled(true);
            }
            else{
                phmh.setTextColor(Color.parseColor("#222222"));
                pbbddhmh.setTextColor(Color.parseColor("#222222"));
                pbbddhmh.setEnabled(false);
                phmhCheckBox.setChecked(false);
                pbbddhmh.setText("");
            }
        });

        piCheckBox.setOnClickListener(v -> {
            if(piCheckBox.isChecked()){
                pi.setTextColor(Color.parseColor("#BBBBBB"));
                pbbddi.setTextColor(Color.parseColor("#BBBBBB"));
                pbbddi.setEnabled(true);
            }
            else{
                pi.setTextColor(Color.parseColor("#222222"));
                pbbddi.setTextColor(Color.parseColor("#222222"));
                pbbddi.setEnabled(false);
                piCheckBox.setChecked(false);
                pbbddi.setText("");
            }
        });

        peCheckBox.setOnClickListener(v -> {
            if(peCheckBox.isChecked()){
                pe.setTextColor(Color.parseColor("#BBBBBB"));
                pbbdde.setTextColor(Color.parseColor("#BBBBBB"));
                pbbdde.setEnabled(true);
            }
            else{
                pe.setTextColor(Color.parseColor("#222222"));
                pbbdde.setTextColor(Color.parseColor("#222222"));
                pbbdde.setEnabled(false);
                peCheckBox.setChecked(false);
                pbbdde.setText("");
            }
        });

        phmonCheckBox.setOnClickListener(v -> {
            if(phmonCheckBox.isChecked()){
                phmon.setTextColor(Color.parseColor("#BBBBBB"));
                pbbddhmon.setTextColor(Color.parseColor("#BBBBBB"));
                pbbddhmon.setEnabled(true);
            }
            else{
                phmon.setTextColor(Color.parseColor("#222222"));
                pbbddhmon.setTextColor(Color.parseColor("#222222"));
                pbbddhmon.setEnabled(false);
                phmonCheckBox.setChecked(false);
                pbbddhmon.setText("");
            }
        });

        mbbddcp.setOnClickListener(v -> {
            if(mbbddcp.isChecked())
                mcp.setTextColor(Color.parseColor("#BBBBBB"));
            else
                mcp.setTextColor(Color.parseColor("#222222"));
        });

        mtdqCheckBox.setOnClickListener(v -> {
            if(mtdqCheckBox.isChecked()) {
                mtdq.setTextColor(Color.parseColor("#BBBBBB"));
                mslash.setTextColor(Color.parseColor("#BBBBBB"));
                mbbddtdq.setTextColor(Color.parseColor("#BBBBBB"));
                mbbddtdq.setEnabled(true);
                mbbddtdq2.setTextColor(Color.parseColor("#BBBBBB"));
                mbbddtdq2.setEnabled(true);
            }
            else{
                mtdq.setTextColor(Color.parseColor("#222222"));
                mslash.setTextColor(Color.parseColor("#222222"));
                mbbddtdq.setTextColor(Color.parseColor("#222222"));
                mbbddtdq.setEnabled(false);
                mbbddtdq.setText("");
                mbbddtdq2.setTextColor(Color.parseColor("#222222"));
                mbbddtdq2.setEnabled(false);
                mbbddtdq2.setText("");
            }
        });

        mrCheckBox.setOnClickListener(v -> {
            if(mrCheckBox.isChecked()){
                mr.setTextColor(Color.parseColor("#BBBBBB"));
                mbbddr.setTextColor(Color.parseColor("#BBBBBB"));
                mbbddr.setEnabled(true);
            }
            else{
                mr.setTextColor(Color.parseColor("#222222"));
                mbbddr.setTextColor(Color.parseColor("#222222"));
                mbbddr.setEnabled(false);
                mrCheckBox.setChecked(false);
                mbbddr.setText("");
            }
        });

        mhmhCheckBox.setOnClickListener(v -> {
            if(mhmhCheckBox.isChecked()){
                mhmh.setTextColor(Color.parseColor("#BBBBBB"));
                mbbddhmh.setTextColor(Color.parseColor("#BBBBBB"));
                mbbddhmh.setEnabled(true);
            }
            else{
                mhmh.setTextColor(Color.parseColor("#222222"));
                mbbddhmh.setTextColor(Color.parseColor("#222222"));
                mbbddhmh.setEnabled(false);
                mhmhCheckBox.setChecked(false);
                mbbddhmh.setText("");
            }
        });

        miCheckBox.setOnClickListener(v -> {
            if(miCheckBox.isChecked()){
                mi.setTextColor(Color.parseColor("#BBBBBB"));
                mbbddi.setTextColor(Color.parseColor("#BBBBBB"));
                mbbddi.setEnabled(true);
            }
            else{
                mi.setTextColor(Color.parseColor("#222222"));
                mbbddi.setTextColor(Color.parseColor("#222222"));
                mbbddi.setEnabled(false);
                miCheckBox.setChecked(false);
                mbbddi.setText("");
            }
        });

        meCheckBox.setOnClickListener(v -> {
            if(meCheckBox.isChecked()){
                me.setTextColor(Color.parseColor("#BBBBBB"));
                mbbdde.setTextColor(Color.parseColor("#BBBBBB"));
                mbbdde.setEnabled(true);
            }
            else{
                me.setTextColor(Color.parseColor("#222222"));
                mbbdde.setTextColor(Color.parseColor("#222222"));
                mbbdde.setEnabled(false);
                meCheckBox.setChecked(false);
                mbbdde.setText("");
            }
        });

        mhmonCheckBox.setOnClickListener(v -> {
            if(mhmonCheckBox.isChecked()){
                mhmon.setTextColor(Color.parseColor("#BBBBBB"));
                mbbddhmon.setTextColor(Color.parseColor("#BBBBBB"));
                mbbddhmon.setEnabled(true);
            }
            else{
                mhmon.setTextColor(Color.parseColor("#222222"));
                mbbddhmon.setTextColor(Color.parseColor("#222222"));
                mbbddhmon.setEnabled(false);
                mhmonCheckBox.setChecked(false);
                mbbddhmon.setText("");
            }
        });
*/
    }
}
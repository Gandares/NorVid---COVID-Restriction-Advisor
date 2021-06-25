package com.example.norvid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firestore.v1.WriteResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertRestrictions extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "DocSnippets";
    public static final int FAST_UPDATE_INTERVAL = 5;
    int lid = 2000;
    int contador = 0;
    int contadorAsincrono = 0;
    boolean reestructurar = true;

    ArrayList<ConstraintLayout> cls = new ArrayList<>();
    ArrayList<ArrayList<ArrayList<Integer>>> Guardar = new ArrayList<>();
    ArrayList<ArrayList<String>> Lugares = new ArrayList<>();

    Button saveButton, logout;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_restrictions);

        String emailUse = getIntent().getStringExtra("email");

        saveButton = findViewById(R.id.savebutton);
        logout = findViewById(R.id.logOutButton);

        saveButton.setEnabled(false);
        logout.setEnabled(false);

        ConstraintLayout layoutGeneral = (ConstraintLayout) logout.getParent();

        DocumentReference dbuser = db.collection("users").document(emailUse);
        dbuser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    if (document.getData().get("CCAA") != null) {
                        List<String> CCAAs = (List<String>) document.getData().get("CCAA");
                        ArrayList<String> Lugar = new ArrayList<>();
                        for (int i = 0; i < CCAAs.size(); i++) {
                            Lugar.add(CCAAs.get(i));
                            ConstraintSet set = new ConstraintSet();
                            TextView restriction = new TextView(InsertRestrictions.this);
                            restriction.setId(lid);
                            lid++;
                            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                            restriction.setText("Comunidad autónoma: " + CCAAs.get(i));
                            restriction.setTextSize(17);
                            restriction.setTextColor(Color.parseColor("#BBBBBB"));
                            layoutGeneral.addView(restriction, lp);
                            set.clone(layoutGeneral);
                            if (lid != 2001)
                                set.connect(restriction.getId(), ConstraintSet.TOP, lid - 2, ConstraintSet.BOTTOM, 20);
                            else
                                set.connect(restriction.getId(), ConstraintSet.TOP, logout.getId(), ConstraintSet.BOTTOM, 20);

                            set.applyTo(layoutGeneral);

                            ScrollView scrollView = new ScrollView(InsertRestrictions.this);
                            scrollView.setId(lid);
                            lid++;
                            ConstraintLayout.LayoutParams sclp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 500);
                            layoutGeneral.addView(scrollView, sclp);
                            set.clone(layoutGeneral);
                            set.connect(scrollView.getId(), ConstraintSet.TOP, restriction.getId(), ConstraintSet.BOTTOM, 20);
                            set.applyTo(layoutGeneral);

                            ConstraintLayout cl = new ConstraintLayout(InsertRestrictions.this);
                            cl.setId(lid);
                            lid++;
                            ConstraintLayout.LayoutParams cllp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 500);
                            layoutGeneral.addView(cl, cllp);
                            set.clone(layoutGeneral);
                            set.connect(cl.getId(), ConstraintSet.TOP, scrollView.getId(), ConstraintSet.TOP);
                            set.connect(cl.getId(), ConstraintSet.BOTTOM, scrollView.getId(), ConstraintSet.BOTTOM);
                            set.connect(cl.getId(), ConstraintSet.END, scrollView.getId(), ConstraintSet.END);
                            set.connect(cl.getId(), ConstraintSet.START, scrollView.getId(), ConstraintSet.START);
                            set.applyTo(layoutGeneral);

                            cls.add(cl);

                            ImageButton deleteButton = new ImageButton(InsertRestrictions.this);
                            ConstraintLayout.LayoutParams lpButton = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            deleteButton.setId(lid);
                            lid++;
                            deleteButton.setImageResource(R.drawable.delete);
                            layoutGeneral.addView(deleteButton, lpButton);
                            set.clone(layoutGeneral);
                            set.connect(deleteButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                            set.connect(deleteButton.getId(), ConstraintSet.TOP, restriction.getId(), ConstraintSet.TOP);
                            set.connect(deleteButton.getId(), ConstraintSet.BOTTOM, restriction.getId(), ConstraintSet.BOTTOM);
                            set.applyTo(layoutGeneral);
                            int fila = contador;
                            deleteClick(deleteButton, cl, fila);

                            Button newRes = new Button(InsertRestrictions.this);
                            ConstraintLayout.LayoutParams lpNew = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            newRes.setId(lid);
                            lid++;
                            newRes.setText("Seleccionar otra restricción para " + CCAAs.get(i));
                            newRes.setTextSize(10);
                            layoutGeneral.addView(newRes, lpNew);
                            set.clone(layoutGeneral);
                            set.connect(newRes.getId(), ConstraintSet.TOP, cl.getId(), ConstraintSet.BOTTOM,10);
                            set.connect(newRes.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 20);
                            set.connect(newRes.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 20);
                            set.applyTo(layoutGeneral);
                            int copiaContador = contador;
                            restriccionesClick(newRes, cl, copiaContador, emailUse);

                            String comunidad = CCAAs.get(i);

                            ArrayList<ArrayList<Integer>> idCadaRestriccion = new ArrayList<ArrayList<Integer>>();

                            CollectionReference dbCCAA = db.collection("CCAA").document(comunidad).collection("Restricciones");
                            dbCCAA.get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    List<DocumentSnapshot> documents = task1.getResult().getDocuments();
                                    int idlast = -1;
                                    ConstraintSet setIn = new ConstraintSet();
                                    try {
                                        DocumentSnapshot totry = documents.get(0);
                                        for (int finalI = 0; finalI < documents.size(); finalI++) {
                                            boolean firstIn = true;

                                            ArrayList<Integer> idCadaPalabra = new ArrayList<Integer>();
                                            for (int j = 0; j < documents.get(finalI).getData().size(); j++) {

                                                if ((String) documents.get(finalI).getData().get(Integer.toString(j)) != null) {
                                                    TextView restrictions = new TextView(InsertRestrictions.this);
                                                    restrictions.setId(lid);

                                                    ConstraintLayout.LayoutParams lpRes = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                                                    restrictions.setText((String) documents.get(finalI).getData().get(Integer.toString(j)));
                                                    restrictions.setTextSize(17);
                                                    restrictions.setTextColor(Color.parseColor("#BBBBBB"));
                                                    cl.addView(restrictions, lpRes);
                                                    setIn.clone(cl);

                                                    if (idlast == -1) {
                                                        setIn.connect(restrictions.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                                                        setIn.connect(restrictions.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                                        firstIn = false;
                                                    } else {
                                                        if (firstIn) {
                                                            setIn.connect(restrictions.getId(), ConstraintSet.TOP, idlast, ConstraintSet.BOTTOM);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                                            firstIn = false;
                                                        } else {
                                                            setIn.connect(restrictions.getId(), ConstraintSet.START, idlast, ConstraintSet.END);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.TOP, idlast, ConstraintSet.TOP);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.BOTTOM, idlast, ConstraintSet.BOTTOM);
                                                        }
                                                    }
                                                } else {
                                                    EditText restrictions = new EditText(InsertRestrictions.this);
                                                    restrictions.setId(lid);

                                                    ConstraintLayout.LayoutParams lpRes = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                                                    restrictions.setText((String) documents.get(finalI).getData().get("data" + j));
                                                    restrictions.setTextSize(17);
                                                    restrictions.setTextColor(Color.parseColor("#BBBBBB"));
                                                    cl.addView(restrictions, lpRes);
                                                    setIn.clone(cl);

                                                    if (idlast == -1) {
                                                        setIn.connect(restrictions.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                                                        setIn.connect(restrictions.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                                        firstIn = false;
                                                    } else {
                                                        if (firstIn) {
                                                            setIn.connect(restrictions.getId(), ConstraintSet.TOP, idlast, ConstraintSet.BOTTOM);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                                            firstIn = false;
                                                        } else {
                                                            setIn.connect(restrictions.getId(), ConstraintSet.START, idlast, ConstraintSet.END);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.TOP, idlast, ConstraintSet.TOP);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.BOTTOM, idlast, ConstraintSet.BOTTOM);
                                                        }
                                                    }
                                                }
                                                setIn.applyTo(cl);

                                                idlast = lid;
                                                lid++;
                                                idCadaPalabra.add(idlast);
                                            }
                                            idCadaRestriccion.add(idCadaPalabra);
                                        }

                                    } catch (Exception err) { }
                                }
                                Guardar.add(idCadaRestriccion);
                                contadorAsincrono++;
                            });
                            contador++;
                        }
                        Lugares.add(Lugar);
                    }
                    if (document.getData().get("Prov") != null) {
                        List<String> PROVs = (List<String>) document.getData().get("Prov");
                        ArrayList<String> Lugar = new ArrayList<>();
                        for (int i2 = 0; i2 < PROVs.size(); i2++) {
                            Lugar.add(PROVs.get(i2));
                            ConstraintSet set = new ConstraintSet();
                            TextView restriction = new TextView(InsertRestrictions.this);
                            restriction.setId(lid);
                            lid++;
                            ConstraintLayout.LayoutParams lp2 = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            restriction.setText("Provincia: " + PROVs.get(i2));
                            restriction.setTextSize(17);
                            restriction.setTextColor(Color.parseColor("#BBBBBB"));
                            layoutGeneral.addView(restriction, lp2);
                            set.clone(layoutGeneral);
                            if (lid != 2001)
                                set.connect(restriction.getId(), ConstraintSet.TOP, lid - 2, ConstraintSet.BOTTOM, 20);
                            else
                                set.connect(restriction.getId(), ConstraintSet.TOP, logout.getId(), ConstraintSet.BOTTOM, 20);

                            set.applyTo(layoutGeneral);

                            ScrollView scrollView = new ScrollView(InsertRestrictions.this);
                            scrollView.setId(lid);
                            lid++;
                            ConstraintLayout.LayoutParams sclp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 500);
                            layoutGeneral.addView(scrollView, sclp);
                            set.clone(layoutGeneral);
                            set.connect(scrollView.getId(), ConstraintSet.TOP, restriction.getId(), ConstraintSet.BOTTOM, 20);
                            set.applyTo(layoutGeneral);

                            ConstraintLayout cl = new ConstraintLayout(InsertRestrictions.this);
                            cl.setId(lid);
                            lid++;
                            ConstraintLayout.LayoutParams cllp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 500);
                            layoutGeneral.addView(cl, cllp);
                            set.clone(layoutGeneral);
                            set.connect(cl.getId(), ConstraintSet.TOP, scrollView.getId(), ConstraintSet.TOP);
                            set.connect(cl.getId(), ConstraintSet.BOTTOM, scrollView.getId(), ConstraintSet.BOTTOM);
                            set.connect(cl.getId(), ConstraintSet.END, scrollView.getId(), ConstraintSet.END);
                            set.connect(cl.getId(), ConstraintSet.START, scrollView.getId(), ConstraintSet.START);
                            set.applyTo(layoutGeneral);

                            cls.add(cl);

                            ImageButton deleteButton = new ImageButton(InsertRestrictions.this);
                            ConstraintLayout.LayoutParams lpButton = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            deleteButton.setId(lid);
                            lid++;
                            deleteButton.setImageResource(R.drawable.delete);
                            layoutGeneral.addView(deleteButton, lpButton);
                            set.clone(layoutGeneral);
                            set.connect(deleteButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                            set.connect(deleteButton.getId(), ConstraintSet.TOP, restriction.getId(), ConstraintSet.TOP);
                            set.connect(deleteButton.getId(), ConstraintSet.BOTTOM, restriction.getId(), ConstraintSet.BOTTOM);
                            set.applyTo(layoutGeneral);

                            int fila = contador;
                            deleteClick(deleteButton, cl, fila);

                            Button newRes = new Button(InsertRestrictions.this);
                            ConstraintLayout.LayoutParams lpNew = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            newRes.setId(lid);
                            lid++;
                            newRes.setText("Seleccionar otra restricción para " + PROVs.get(i2));
                            newRes.setTextSize(10);
                            layoutGeneral.addView(newRes, lpNew);
                            set.clone(layoutGeneral);
                            set.connect(newRes.getId(), ConstraintSet.TOP, cl.getId(), ConstraintSet.BOTTOM);
                            set.connect(newRes.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 20);
                            set.connect(newRes.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 20);
                            set.applyTo(layoutGeneral);
                            int copiaContador = contador;
                            restriccionesClick(newRes, cl, copiaContador, emailUse);

                            String prov = PROVs.get(i2);

                            ArrayList<ArrayList<Integer>> idCadaRestriccion = new ArrayList<ArrayList<Integer>>();

                            CollectionReference dbPROV = db.collection("Provincias").document(prov).collection("Restricciones");
                            dbPROV.get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    List<DocumentSnapshot> documents = task1.getResult().getDocuments();
                                    int idlast = -1;
                                    ConstraintSet setIn = new ConstraintSet();
                                    try {
                                        DocumentSnapshot totry = documents.get(0);
                                        for (int finalI = 0; finalI < documents.size(); finalI++) {
                                            boolean firstIn = true;

                                            ArrayList<Integer> idCadaPalabra = new ArrayList<Integer>();
                                            for (int j = 0; j < documents.get(finalI).getData().size(); j++) {

                                                if ((String) documents.get(finalI).getData().get(Integer.toString(j)) != null) {
                                                    TextView restrictions = new TextView(InsertRestrictions.this);
                                                    restrictions.setId(lid);

                                                    ConstraintLayout.LayoutParams lpRes = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                                                    restrictions.setText((String) documents.get(finalI).getData().get(Integer.toString(j)));
                                                    restrictions.setTextSize(17);
                                                    restrictions.setTextColor(Color.parseColor("#BBBBBB"));
                                                    cl.addView(restrictions, lpRes);
                                                    setIn.clone(cl);

                                                    if (idlast == -1) {
                                                        setIn.connect(restrictions.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                                                        setIn.connect(restrictions.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                                        firstIn = false;
                                                    } else {
                                                        if (firstIn) {
                                                            setIn.connect(restrictions.getId(), ConstraintSet.TOP, idlast, ConstraintSet.BOTTOM);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                                            firstIn = false;
                                                        } else {
                                                            setIn.connect(restrictions.getId(), ConstraintSet.START, idlast, ConstraintSet.END);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.TOP, idlast, ConstraintSet.TOP);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.BOTTOM, idlast, ConstraintSet.BOTTOM);
                                                        }
                                                    }
                                                } else {
                                                    EditText restrictions = new EditText(InsertRestrictions.this);
                                                    restrictions.setId(lid);

                                                    ConstraintLayout.LayoutParams lpRes = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                                                    restrictions.setText((String) documents.get(finalI).getData().get("data" + j));
                                                    restrictions.setTextSize(17);
                                                    restrictions.setTextColor(Color.parseColor("#BBBBBB"));
                                                    cl.addView(restrictions, lpRes);
                                                    setIn.clone(cl);

                                                    if (idlast == -1) {
                                                        setIn.connect(restrictions.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                                                        setIn.connect(restrictions.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                                        firstIn = false;
                                                    } else {
                                                        if (firstIn) {
                                                            setIn.connect(restrictions.getId(), ConstraintSet.TOP, idlast, ConstraintSet.BOTTOM);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                                            firstIn = false;
                                                        } else {
                                                            setIn.connect(restrictions.getId(), ConstraintSet.START, idlast, ConstraintSet.END);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.TOP, idlast, ConstraintSet.TOP);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.BOTTOM, idlast, ConstraintSet.BOTTOM);
                                                        }
                                                    }
                                                }
                                                setIn.applyTo(cl);

                                                idlast = lid;
                                                lid++;
                                                idCadaPalabra.add(idlast);
                                            }
                                            idCadaRestriccion.add(idCadaPalabra);
                                        }
                                    } catch (Exception err) { }
                                }
                                Guardar.add(idCadaRestriccion);
                                contadorAsincrono++;
                            });
                            contador++;
                        }
                        Lugares.add(Lugar);
                    }
                    if (document.getData().get("Mun") != null) {
                        List<String> MUNs = (List<String>) document.getData().get("Mun");
                        ArrayList<String> Lugar = new ArrayList<>();
                        for (int i3 = 0; i3 < MUNs.size(); i3++) {
                            Lugar.add(MUNs.get(i3));
                            ConstraintSet set = new ConstraintSet();
                            TextView restriction = new TextView(InsertRestrictions.this);
                            restriction.setId(lid);
                            lid++;
                            ConstraintLayout.LayoutParams lp3 = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            restriction.setText("Municipio: " + MUNs.get(i3));
                            restriction.setTextSize(17);
                            restriction.setTextColor(Color.parseColor("#BBBBBB"));
                            layoutGeneral.addView(restriction, lp3);
                            set.clone(layoutGeneral);
                            if (lid != -1)
                                set.connect(restriction.getId(), ConstraintSet.TOP, lid - 2, ConstraintSet.BOTTOM, 20);
                            else
                                set.connect(restriction.getId(), ConstraintSet.TOP, logout.getId(), ConstraintSet.BOTTOM, 20);

                            set.applyTo(layoutGeneral);

                            ScrollView scrollView = new ScrollView(InsertRestrictions.this);
                            scrollView.setId(lid);
                            lid++;
                            ConstraintLayout.LayoutParams sclp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 500);
                            layoutGeneral.addView(scrollView, sclp);
                            set.clone(layoutGeneral);
                            set.connect(scrollView.getId(), ConstraintSet.TOP, restriction.getId(), ConstraintSet.BOTTOM, 20);
                            set.applyTo(layoutGeneral);

                            ConstraintLayout cl = new ConstraintLayout(InsertRestrictions.this);
                            cl.setId(lid);
                            lid++;
                            ConstraintLayout.LayoutParams cllp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 500);
                            layoutGeneral.addView(cl, cllp);
                            set.clone(layoutGeneral);
                            set.connect(cl.getId(), ConstraintSet.TOP, scrollView.getId(), ConstraintSet.TOP);
                            set.connect(cl.getId(), ConstraintSet.BOTTOM, scrollView.getId(), ConstraintSet.BOTTOM);
                            set.connect(cl.getId(), ConstraintSet.END, scrollView.getId(), ConstraintSet.END);
                            set.connect(cl.getId(), ConstraintSet.START, scrollView.getId(), ConstraintSet.START);
                            set.applyTo(layoutGeneral);

                            cls.add(cl);

                            ImageButton deleteButton = new ImageButton(InsertRestrictions.this);
                            ConstraintLayout.LayoutParams lpButton = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            deleteButton.setId(lid);
                            lid++;
                            deleteButton.setImageResource(R.drawable.delete);
                            layoutGeneral.addView(deleteButton, lpButton);
                            set.clone(layoutGeneral);
                            set.connect(deleteButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                            set.connect(deleteButton.getId(), ConstraintSet.TOP, restriction.getId(), ConstraintSet.TOP);
                            set.connect(deleteButton.getId(), ConstraintSet.BOTTOM, restriction.getId(), ConstraintSet.BOTTOM);
                            set.applyTo(layoutGeneral);

                            int fila = contador;
                            deleteClick(deleteButton, cl, fila);

                            Button newRes = new Button(InsertRestrictions.this);
                            ConstraintLayout.LayoutParams lpNew = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            newRes.setId(lid);
                            lid++;
                            newRes.setText("Seleccionar otra restricción para " + MUNs.get(i3));
                            newRes.setTextSize(10);
                            layoutGeneral.addView(newRes, lpNew);
                            set.clone(layoutGeneral);
                            set.connect(newRes.getId(), ConstraintSet.TOP, cl.getId(), ConstraintSet.BOTTOM);
                            set.connect(newRes.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 20);
                            set.connect(newRes.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 20);
                            set.applyTo(layoutGeneral);
                            int copiaContador = contador;
                            restriccionesClick(newRes, cl, copiaContador, emailUse);

                            String mun = MUNs.get(i3);

                            ArrayList<ArrayList<Integer>> idCadaRestriccion = new ArrayList<ArrayList<Integer>>();

                            CollectionReference dbMUN = db.collection("Municipios").document(mun).collection("Restricciones");
                            dbMUN.get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    List<DocumentSnapshot> documents = task1.getResult().getDocuments();
                                    int idlast = -1;
                                    ConstraintSet setIn = new ConstraintSet();
                                    try {
                                        DocumentSnapshot totry = documents.get(0);
                                        for (int finalI = 0; finalI < documents.size(); finalI++) {
                                            boolean firstIn = true;

                                            ArrayList<Integer> idCadaPalabra = new ArrayList<Integer>();
                                            for (int j = 0; j < documents.get(finalI).getData().size(); j++) {

                                                if ((String) documents.get(finalI).getData().get(Integer.toString(j)) != null) {
                                                    TextView restrictions = new TextView(InsertRestrictions.this);
                                                    restrictions.setId(lid);

                                                    ConstraintLayout.LayoutParams lpRes = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                                                    restrictions.setText((String) documents.get(finalI).getData().get(Integer.toString(j)));
                                                    restrictions.setTextSize(17);
                                                    restrictions.setTextColor(Color.parseColor("#BBBBBB"));
                                                    cl.addView(restrictions, lpRes);
                                                    setIn.clone(cl);

                                                    if (idlast == -1) {
                                                        setIn.connect(restrictions.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                                                        setIn.connect(restrictions.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                                        firstIn = false;
                                                    } else {
                                                        if (firstIn) {
                                                            setIn.connect(restrictions.getId(), ConstraintSet.TOP, idlast, ConstraintSet.BOTTOM);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                                            firstIn = false;
                                                        } else {
                                                            setIn.connect(restrictions.getId(), ConstraintSet.START, idlast, ConstraintSet.END);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.TOP, idlast, ConstraintSet.TOP);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.BOTTOM, idlast, ConstraintSet.BOTTOM);
                                                        }
                                                    }
                                                } else {
                                                    EditText restrictions = new EditText(InsertRestrictions.this);
                                                    restrictions.setId(lid);

                                                    ConstraintLayout.LayoutParams lpRes = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                                                    restrictions.setText((String) documents.get(finalI).getData().get("data" + j));
                                                    restrictions.setTextSize(17);
                                                    restrictions.setTextColor(Color.parseColor("#BBBBBB"));
                                                    cl.addView(restrictions, lpRes);
                                                    setIn.clone(cl);

                                                    if (idlast == -1) {
                                                        setIn.connect(restrictions.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                                                        setIn.connect(restrictions.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                                        firstIn = false;
                                                    } else {
                                                        if (firstIn) {
                                                            setIn.connect(restrictions.getId(), ConstraintSet.TOP, idlast, ConstraintSet.BOTTOM);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                                            firstIn = false;
                                                        } else {
                                                            setIn.connect(restrictions.getId(), ConstraintSet.START, idlast, ConstraintSet.END);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.TOP, idlast, ConstraintSet.TOP);
                                                            setIn.connect(restrictions.getId(), ConstraintSet.BOTTOM, idlast, ConstraintSet.BOTTOM);
                                                        }
                                                    }
                                                }
                                                setIn.applyTo(cl);

                                                idlast = lid;
                                                lid++;
                                                idCadaPalabra.add(idlast);
                                            }
                                            idCadaRestriccion.add(idCadaPalabra);
                                        }

                                    } catch (Exception err) { }
                                }
                                Guardar.add(idCadaRestriccion);
                                contadorAsincrono++;
                            });
                            contador++;
                        }
                        Lugares.add(Lugar);
                    }
                }
            }
        });

        logout.setEnabled(true);
        saveButton.setEnabled(true);

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent myIntent = new Intent(InsertRestrictions.this, MainActivity.class);
            InsertRestrictions.this.startActivity(myIntent);
        });

        saveButton.setOnClickListener(v -> {
            saveButton.setEnabled(false);
            logout.setEnabled(false);
            reestructurar();
            Log.d(TAG,Lugares.toString());
            int nrestriccion = 0;

            for(int lugar = 0; lugar < Lugares.size(); lugar++){
                String collection = "";
                if(lugar == 0)
                    collection = "CCAA";
                else if(lugar == 1)
                    collection = "Provincias";
                else if(lugar == 2)
                    collection = "Municipios";
                for(int zona = 0; zona < Lugares.get(lugar).size(); zona++){
                    CollectionReference colRefCCAA = db.collection(collection).document(Lugares.get(lugar).get(zona)).collection("Restricciones");
                    int finalNrestriccion = nrestriccion;
                    String finalCollection = collection;
                    int finalLugar = lugar;
                    int finalZona = zona;
                    colRefCCAA.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            if(documents.size()>0){
                                int len = documents.size();
                                for(int i = 0; i < len; i++){
                                    WriteBatch batch = db.batch();
                                    DocumentReference deleteRef = db.collection(finalCollection).document(Lugares.get(finalLugar).get(finalZona)).collection("Restricciones").document("r" + i);
                                    batch.delete(deleteRef);
                                    batch.commit().addOnCompleteListener(task1 -> {
                                        insercionDatos(finalCollection,Lugares.get(finalLugar).get(finalZona),finalNrestriccion);
                                    });
                                }
                            }
                            else{
                                Log.d(TAG, finalCollection + ", " + Lugares.get(finalLugar).get(finalZona) + " Esto vacio");
                                insercionDatos(finalCollection,Lugares.get(finalLugar).get(finalZona),finalNrestriccion);
                            }
                        }
                    });
                    nrestriccion++;
                }
            }
            AlertDialog.Builder completo = new AlertDialog.Builder(InsertRestrictions.this);
            completo.setMessage("Restricciones asignadas.");
            completo.setPositiveButton("Aceptar", null);

            AlertDialog dialog = completo.create();
            dialog.show();
            saveButton.setEnabled(true);
            logout.setEnabled(true);
        });
    }

    private void reestructurar(){
        ArrayList<ArrayList<ArrayList<Integer>>> aux = new ArrayList<>(cls.size());
        for(int i=0; i<cls.size(); i++){
            aux.add(new ArrayList<>());
        }
        for(int i=0; i<cls.size(); i++){
            if(Guardar.get(i).size() > 0){
                for(int j=0; j<cls.size(); j++){
                    if(cls.get(j).findViewById(Guardar.get(i).get(0).get(0))!=null){
                        Log.d(TAG, j + "");
                        aux.set(j,Guardar.get(i));
                    }
                }
            }
        }
        Guardar = aux;
        Log.d(TAG, Guardar.toString());
    }

    private void deleteClick(ImageButton button, ConstraintLayout cl, int fila){
        button.setOnClickListener(v -> {
            reestructurar();
            cl.removeAllViewsInLayout();
            Guardar.remove(fila);
            Guardar.add(fila,new ArrayList<ArrayList<Integer>>());
        });
    }

    private void restriccionesClick(Button newRes, ConstraintLayout cl, int fila, String emailUser){
        newRes.setOnClickListener(v -> {
            reestructurar();
            DocumentReference restriccionesLista = db.collection("Listas").document("Restricciones");
            restriccionesLista.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList Rcast = (ArrayList) document.getData().get("r");
                        String[] R = new String[Rcast.size()+1];
                        for(int i=0; i<Rcast.size(); i++)
                            R[i] = Rcast.get(i).toString();
                        R[Rcast.size()] = "Insertar nueva restricción";
                        AlertDialog.Builder opciones = new AlertDialog.Builder(InsertRestrictions.this);
                        opciones.setTitle("Selecciones una restricción");
                        opciones.setSingleChoiceItems(R, -1, (dialog, which) -> {
                            if(R[which].equals("Insertar nueva restricción")){
                                Intent insertIntent = new Intent(InsertRestrictions.this, NewRestriction.class).putExtra("email", emailUser);
                                InsertRestrictions.this.startActivity(insertIntent);
                                dialog.dismiss();
                            }
                            else {
                                insertNewRestriction(R[which], cl, fila);
                                dialog.dismiss();
                            }
                        });
                        opciones.setNeutralButton("Cancel", (dialog, which) -> { });

                        AlertDialog dialog = opciones.create();
                        dialog.show();
                    }
                }
            });
        });
    }

    private void insertNewRestriction(String restriccion, ConstraintLayout cl, int fila){
        DocumentReference nuevaRestriccion = db.collection("Restricciones").document(restriccion);
        nuevaRestriccion.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<Integer> inputs = new ArrayList<>();
                    ConstraintSet set = new ConstraintSet();
                    boolean first = true;
                    for(int i = 0; i < document.getData().size(); i++){
                        if(document.getData().get(Integer.toString(i))!=null){
                            TextView texto = new TextView(InsertRestrictions.this);
                            texto.setId(lid);
                            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                            texto.setText(document.getData().get(Integer.toString(i)).toString());
                            texto.setTextSize(17);
                            texto.setTextColor(Color.parseColor("#BBBBBB"));
                            cl.addView(texto, lp);
                            set.clone(cl);
                            Log.d(TAG,cl.getChildAt(cl.getChildCount()-1).getId() + "TAMAÑO");
                            if (first) {
                                if(cl.getChildCount()<=1)
                                    set.connect(texto.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                                else
                                    set.connect(texto.getId(), ConstraintSet.TOP, cl.getChildAt(cl.getChildCount()-2).getId(), ConstraintSet.BOTTOM);
                                set.connect(texto.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                first = false;
                            } else {
                                set.connect(texto.getId(), ConstraintSet.START, lid-1, ConstraintSet.END);
                                set.connect(texto.getId(), ConstraintSet.TOP, lid-1, ConstraintSet.TOP);
                                set.connect(texto.getId(), ConstraintSet.BOTTOM, lid-1, ConstraintSet.BOTTOM);
                            }
                        }
                        else{
                            EditText texto = new EditText(InsertRestrictions.this);
                            texto.setId(lid);
                            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                            texto.setTextSize(17);
                            texto.setTextColor(Color.parseColor("#BBBBBB"));
                            cl.addView(texto, lp);
                            set.clone(cl);

                            Log.d(TAG,cl.getChildAt(cl.getChildCount()-1).getId() + "TAMAÑO");
                            if (first) {
                                if(cl.getChildCount()<=1)
                                    set.connect(texto.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                                else
                                    set.connect(texto.getId(), ConstraintSet.TOP, cl.getChildAt(cl.getChildCount()-2).getId(), ConstraintSet.BOTTOM);
                                set.connect(texto.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 50);
                                first = false;
                            } else {
                                set.connect(texto.getId(), ConstraintSet.START, lid-1, ConstraintSet.END);
                                set.connect(texto.getId(), ConstraintSet.TOP, lid-1, ConstraintSet.TOP);
                                set.connect(texto.getId(), ConstraintSet.BOTTOM, lid-1, ConstraintSet.BOTTOM);
                            }
                        }
                        inputs.add(lid);
                        lid++;

                        set.applyTo(cl);
                    }
                    Guardar.get(fila).add(inputs);
                    Log.d(TAG, Guardar.toString() + "TODOS");
                }
            }
        });
    }

    private void insercionDatos(String collection, String document, int n){
        for(int i = 0; i < Guardar.get(n).size(); i++){
            DocumentReference docRef = db.collection(collection).document(document).collection("Restricciones").document("r"+ i);
            Map<String, Object> restriccion = new HashMap<>();
            for(int j = 0; j < Guardar.get(n).get(i).size(); j++){
                if(cls.get(n).findViewById(Guardar.get(n).get(i).get(j)).getClass().getName().equals("android.widget.TextView")){
                    TextView v = cls.get(n).findViewById(Guardar.get(n).get(i).get(j));
                    restriccion.put(Integer.toString(j), v.getText().toString());
                }else if(cls.get(n).findViewById(Guardar.get(n).get(i).get(j)).getClass().getName().equals("android.widget.EditText")){
                    EditText v = cls.get(n).findViewById(Guardar.get(n).get(i).get(j));
                    String data = "data"+j;
                    restriccion.put(data, v.getText().toString());
                }
            }
            docRef.set(restriccion).addOnCompleteListener(task -> {Log.d(TAG, "Agregado");});
        }
    }
}
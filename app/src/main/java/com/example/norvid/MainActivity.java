package com.example.norvid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.view.View;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "DocSnippets";
    public static final int DEFAULT_UPDATE_INTERVAL = 20;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private static String[] MUNLIST;
    private String MunViewText;
    private String ProvViewText;
    private String CCAAViewText;
    private boolean firstTime = true;
    private final String nohay = "No hay.";
    private final String ned = "No existen datos.";
    private final String eelp = "Error en la petición.";

    //TextView ccaa, mun, prov;

    TextView bbddtdqA, bbddcpA, bbddrA, bbddhmhA, bbddiA, bbddeA, bbddhmonA;

    TextView pbbddtdqA, pbbddcpA, pbbddrA, pbbddhmhA, pbbddiA, pbbddeA, pbbddhmonA;

    TextView mbbddtdqA, mbbddcpA, mbbddrA, mbbddhmhA, mbbddiA, mbbddeA, mbbddhmonA;

    BottomNavigationView botNav;
    String municipio, provincia, comunidadAutonoma;
    SearchView CCAAView, ProvView, MunView;
    Button loadButton;


    Timer timer;


    // Google's API for location service
    FusedLocationProviderClient fusedLocationProviderClient;

    // Location request
    LocationRequest locationRequest;

    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*ccaa = findViewById(R.id.ccaa);
        prov = findViewById(R.id.prov);
        mun = findViewById(R.id.mun);*/

        bbddtdqA = findViewById(R.id.bbddtdqA);
        bbddcpA = findViewById(R.id.bbddcpA);
        bbddrA = findViewById(R.id.bbddrA);
        bbddhmhA = findViewById(R.id.bbddhmhA);
        bbddiA = findViewById(R.id.bbddiA);
        bbddeA = findViewById(R.id.bbddeA);
        bbddhmonA = findViewById(R.id.bbddhmonA);

        pbbddtdqA = findViewById(R.id.pbbddtdqA);
        pbbddcpA = findViewById(R.id.pbbddcpA);
        pbbddrA = findViewById(R.id.pbbddrA);
        pbbddhmhA = findViewById(R.id.pbbddhmhA);
        pbbddiA = findViewById(R.id.pbbddiA);
        pbbddeA = findViewById(R.id.pbbddeA);
        pbbddhmonA = findViewById(R.id.pbbddhmonA);

        mbbddtdqA = findViewById(R.id.mbbddtdqA);
        mbbddcpA = findViewById(R.id.mbbddcpA);
        mbbddrA = findViewById(R.id.mbbddrA);
        mbbddhmhA = findViewById(R.id.mbbddhmhA);
        mbbddiA = findViewById(R.id.mbbddiA);
        mbbddeA = findViewById(R.id.mbbddeA);
        mbbddhmonA = findViewById(R.id.mbbddhmonA);

        botNav = findViewById(R.id.bottom_navigation);
        CCAAView = (SearchView) findViewById(R.id.CCAAView);
        ProvView = (SearchView) findViewById(R.id.ProvView);
        MunView = (SearchView) findViewById(R.id.MunView);
        loadButton = findViewById(R.id.loadButton);


        // set all properties of LocationRequest
        locationRequest = LocationRequest.create();
        // How often does the location check occur when set to the most frequent update
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // How often does the default location check occur
        locationRequest.setInterval(FAST_UPDATE_INTERVAL * 1000);
        // Event that is triggered whenever the update interval is met
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        updateUIValues(location);
                        if(firstTime==true) {
                            showData();
                            firstTime = false;
                        }
                    }
                }
            }
        };
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }

        // Evento google analytics
        Bundle bundle = new Bundle();
        bundle.putString("message", "Aplicación abierta");
        mFirebaseAnalytics.logEvent("InitScreen", bundle);

        /*municipio = mun.getText().toString();
        provincia = prov.getText().toString();
        comunidadAutonoma = ccaa.getText().toString();*/

        botNav.setOnNavigationItemSelectedListener( item -> {
            return navigation(item);
        });

        loadButton.setOnClickListener(v -> showData());

        MunView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query){
                MunViewText = query;
                municipio = MunViewText;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                MunViewText = newText;
                municipio = MunViewText;
                if(newText.equals("")||newText.contains("/")){
                    loadButton.setEnabled(false);
                }
                return false;
            }
        });

        ProvView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query){
                ProvViewText = query;
                provincia = ProvViewText;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ProvViewText = newText;
                provincia = ProvViewText;
                if(newText.equals("")||newText.contains("/")){
                    loadButton.setEnabled(false);
                }
                return false;
            }
        });

        CCAAView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query){
                CCAAViewText = query;
                comunidadAutonoma = CCAAViewText;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CCAAViewText = newText;
                comunidadAutonoma = CCAAViewText;
                if(newText.equals("")||newText.contains("/")){
                    loadButton.setEnabled(false);
                }
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGPSLoop();
            }
            else{
                Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        }
    }

    private void startGPSLoop(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void updateUIValues(Location location){
        try {
            Geocoder geocoder = new Geocoder(MainActivity.this);

            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            // getCountryName, getLocality, getAddressLine
            /*prov.setText(addresses.get(0).getSubAdminArea());
            mun.setText(addresses.get(0).getLocality());
            ccaa.setText(addresses.get(0).getAdminArea());*/
            if(MunViewText == null || MunViewText.isEmpty())
                municipio = addresses.get(0).getLocality();
            if(ProvViewText == null || ProvViewText.isEmpty())
                provincia = addresses.get(0).getSubAdminArea();
            if(CCAAViewText == null || CCAAViewText.isEmpty())
                comunidadAutonoma = addresses.get(0).getAdminArea();

            MunView.setQueryHint(municipio);
            ProvView.setQueryHint(provincia);
            CCAAView.setQueryHint(comunidadAutonoma);

            if(!comunidadAutonoma.equals("")&&!comunidadAutonoma.contains("/")&&!municipio.equals("")&&!municipio.contains("/")&&!provincia.equals("")&&!provincia.contains("/")) {
                loadButton.setEnabled(true);
            }

        } catch(Exception e){

        }
    }


    private void showData() {
        DocumentReference docRef = db.collection("Municipios").document(municipio);
        DocumentReference docRefProv = db.collection("Provincias").document(provincia);
        DocumentReference docRefCCAA = db.collection("CCAA").document(comunidadAutonoma);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String tdqddbb = document.getData().get("tdq").toString();
                        if(tdqddbb.equals("-"))
                            mbbddtdqA.setText(nohay);
                        else
                            mbbddtdqA.setText(tdqddbb);

                        String cpddbb = document.getData().get("cp").toString();
                        if(cpddbb.equals("false"))
                            mbbddcpA.setText(nohay);
                        else
                            mbbddcpA.setText(cpddbb);

                        String rddbb = document.getData().get("r").toString();
                        if(rddbb.equals(""))
                            mbbddrA.setText(nohay);
                        else
                            mbbddrA.setText(rddbb);

                        String hmhddbb = document.getData().get("hmh").toString();
                        if(hmhddbb.equals(""))
                            mbbddhmhA.setText(nohay);
                        else
                            mbbddhmhA.setText(hmhddbb);

                        String iddbb = document.getData().get("i").toString();
                        if(iddbb.equals(""))
                            mbbddiA.setText(nohay);
                        else
                            mbbddiA.setText(iddbb);

                        String eddbb = document.getData().get("e").toString();
                        if(eddbb.equals(""))
                            mbbddeA.setText(nohay);
                        else
                            mbbddeA.setText(eddbb);

                        String hmonddbb = document.getData().get("hmon").toString();
                        if(hmonddbb.equals(""))
                            mbbddhmonA.setText(nohay);
                        else
                            mbbddhmonA.setText(hmonddbb);
                    }
                    else {
                        mbbddtdqA.setText(ned);
                        mbbddcpA.setText(ned);
                        mbbddrA.setText(ned);
                        mbbddhmhA.setText(ned);
                        mbbddiA.setText(ned);
                        mbbddeA.setText(ned);
                        mbbddhmonA.setText(ned);
                    }
                } else {
                    mbbddtdqA.setText(eelp);
                    mbbddcpA.setText(eelp);
                    mbbddrA.setText(eelp);
                    mbbddhmhA.setText(eelp);
                    mbbddiA.setText(eelp);
                    mbbddeA.setText(eelp);
                    mbbddhmonA.setText(eelp);
                }
            }
        });

        docRefProv.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String tdqddbb = document.getData().get("tdq").toString();
                        if(tdqddbb.equals("-"))
                            pbbddtdqA.setText(nohay);
                        else
                            pbbddtdqA.setText(tdqddbb);

                        String cpddbb = document.getData().get("cp").toString();
                        if(cpddbb.equals("false"))
                            pbbddcpA.setText(nohay);
                        else
                            pbbddcpA.setText(cpddbb);

                        String rddbb = document.getData().get("r").toString();
                        if(rddbb.equals(""))
                            pbbddrA.setText(nohay);
                        else
                            pbbddrA.setText(rddbb);

                        String hmhddbb = document.getData().get("hmh").toString();
                        if(hmhddbb.equals(""))
                            pbbddhmhA.setText(nohay);
                        else
                            pbbddhmhA.setText(hmhddbb);

                        String iddbb = document.getData().get("i").toString();
                        if(iddbb.equals(""))
                            pbbddiA.setText(nohay);
                        else
                            pbbddiA.setText(iddbb);

                        String eddbb = document.getData().get("e").toString();
                        if(eddbb.equals(""))
                            pbbddeA.setText(nohay);
                        else
                            pbbddeA.setText(eddbb);

                        String hmonddbb = document.getData().get("hmon").toString();
                        if(hmonddbb.equals(""))
                            pbbddhmonA.setText(nohay);
                        else
                            pbbddhmonA.setText(hmonddbb);
                    }
                    else {
                        pbbddtdqA.setText(ned);
                        pbbddcpA.setText(ned);
                        pbbddrA.setText(ned);
                        pbbddhmhA.setText(ned);
                        pbbddiA.setText(ned);
                        pbbddeA.setText(ned);
                        pbbddhmonA.setText(ned);
                    }
                } else {
                    pbbddtdqA.setText(eelp);
                    pbbddcpA.setText(eelp);
                    pbbddrA.setText(eelp);
                    pbbddhmhA.setText(eelp);
                    pbbddiA.setText(eelp);
                    pbbddeA.setText(eelp);
                    pbbddhmonA.setText(eelp);
                }
            }
        });

        docRefCCAA.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String tdqddbb = document.getData().get("tdq").toString();
                        if(tdqddbb.equals("-"))
                            bbddtdqA.setText(nohay);
                        else
                            bbddtdqA.setText(tdqddbb);

                        String cpddbb = document.getData().get("cp").toString();
                        if(cpddbb.equals("false"))
                            bbddcpA.setText(nohay);
                        else
                            bbddcpA.setText(cpddbb);

                        String rddbb = document.getData().get("r").toString();
                        if(rddbb.equals(""))
                            bbddrA.setText(nohay);
                        else
                            bbddrA.setText(rddbb);

                        String hmhddbb = document.getData().get("hmh").toString();
                        if(hmhddbb.equals(""))
                            bbddhmhA.setText(nohay);
                        else
                            bbddhmhA.setText(hmhddbb);

                        String iddbb = document.getData().get("i").toString();
                        if(iddbb.equals(""))
                            bbddiA.setText(nohay);
                        else
                            bbddiA.setText(iddbb);

                        String eddbb = document.getData().get("e").toString();
                        if(eddbb.equals(""))
                            bbddeA.setText(nohay);
                        else
                            bbddeA.setText(eddbb);

                        String hmonddbb = document.getData().get("hmon").toString();
                        if(hmonddbb.equals(""))
                            bbddhmonA.setText(nohay);
                        else
                            bbddhmonA.setText(hmonddbb);
                    }
                    else {
                        bbddtdqA.setText(ned);
                        bbddcpA.setText(ned);
                        bbddrA.setText(ned);
                        bbddhmhA.setText(ned);
                        bbddiA.setText(ned);
                        bbddeA.setText(ned);
                        bbddhmonA.setText(ned);
                    }
                } else {
                    bbddtdqA.setText(eelp);
                    bbddcpA.setText(eelp);
                    bbddrA.setText(eelp);
                    bbddhmhA.setText(eelp);
                    bbddiA.setText(eelp);
                    bbddeA.setText(eelp);
                    bbddhmonA.setText(eelp);
                }
            }
        });
    }

    public boolean navigation(MenuItem item){
        switch (item.getItemId()) {
            case R.id.coronavirus:
                Log.d(TAG, "corona");
                return true;

            case R.id.login:
                Log.d(TAG, "upload");
                Intent loginIntent = new Intent(this, loginActivity.class);
                this.startActivity(loginIntent);
                return true;

            default:
                return true;
        }
    }
}
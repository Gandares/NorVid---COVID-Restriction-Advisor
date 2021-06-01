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

    TextView ccaa, mun, prov, restriction, restrictionProv, restrictionCCAA;
    EditText tdqueda;
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

        ccaa = findViewById(R.id.ccaa);
        prov = findViewById(R.id.prov);
        mun = findViewById(R.id.mun);
        restriction = findViewById(R.id.restriction);
        restrictionProv = findViewById(R.id.restrictionProv);
        restrictionCCAA = findViewById(R.id.restrictionCCAA);
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

        municipio = mun.getText().toString();
        provincia = prov.getText().toString();
        comunidadAutonoma = ccaa.getText().toString();

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
            prov.setText(addresses.get(0).getSubAdminArea());
            mun.setText(addresses.get(0).getLocality());
            ccaa.setText(addresses.get(0).getAdminArea());
            if(MunViewText == null || MunViewText.isEmpty())
                municipio = mun.getText().toString();
            if(ProvViewText == null || ProvViewText.isEmpty())
                provincia = prov.getText().toString();
            if(CCAAViewText == null || CCAAViewText.isEmpty())
                comunidadAutonoma = ccaa.getText().toString();

            MunView.setQueryHint(municipio);
            ProvView.setQueryHint(provincia);
            CCAAView.setQueryHint(comunidadAutonoma);

        } catch(Exception e){

        }
    }


    private void showData() {
        Log.d(TAG, municipio + "   dfkdbfksdbfkbdkf");
        DocumentReference docRef = db.collection("Municipios").document(municipio);
        DocumentReference docRefProv = db.collection("Provincias").document(provincia);
        DocumentReference docRefCCAA = db.collection("CCAA").document(comunidadAutonoma);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                        restriction.setText("Toque de queda: " + document.getData().get("Toque de queda").toString());
                    else
                        restriction.setText("Cargando restricción...");
                } else
                    restriction.setText("Fallo en la petición a la base de datos...");
            }
        });

        docRefProv.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                        restrictionProv.setText("Toque de queda: " + document.getData().get("Toque de queda").toString());
                    else
                        restrictionProv.setText("Cargando restricción...");
                } else
                    restrictionProv.setText("Fallo en la petición a la base de datos...");
            }
        });

        docRefCCAA.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                        restrictionCCAA.setText("Toque de queda: " + document.getData().get("Toque de queda").toString());
                    else
                        restrictionCCAA.setText("Cargando restricción...");
                } else
                    restrictionCCAA.setText("Fallo en la petición a la base de datos...");
            }
        });
    }

    public boolean navigation(MenuItem item){
        switch (item.getItemId()) {
            case R.id.coronavirus:
                Log.d(TAG, "corona");
                return true;

            case R.id.upload:
                Log.d(TAG, "upload");
                Intent myIntent = new Intent(this, InsertRestrictions.class);
                this.startActivity(myIntent);
                return true;

            default:
                return true;
        }
    }
}
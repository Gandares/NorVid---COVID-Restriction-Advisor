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

    TextView ccaa, mun, prov, address, restriction;
    EditText tdqueda;
    BottomNavigationView botNav;
    String municipio;


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
        address = findViewById(R.id.address);
        restriction = findViewById(R.id.restriction);
        botNav = findViewById(R.id.bottom_navigation);

        db.collection("Provincias").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){
                ArrayList<String> munAux = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        munAux.add(document.getId());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                String auxString;
                auxString=munAux.toString().substring(1,munAux.toString().length()-1);
                MUNLIST = auxString.split(", ");
            }
        });


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

        showData();
        //setupArrays();

        botNav.setOnNavigationItemSelectedListener( item -> {
            return navigation(item);
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
            address.setText(addresses.get(0).getAddressLine(0));
            prov.setText(addresses.get(0).getSubAdminArea());
            mun.setText(addresses.get(0).getLocality());
            ccaa.setText(addresses.get(0).getAdminArea());

        } catch(Exception e){
            address.setText("No se encontró la ubicación");
        }
    }


    private void showData() {
        timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
            DocumentReference docRef = db.collection("Provincias").document(municipio);
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
            }
        }, 0, 5000);
    }

    public boolean navigation(MenuItem item){
        switch (item.getItemId()) {
            case R.id.coronavirus:
                Log.d(TAG, "corona");
                return true;

            case R.id.upload:
                timer.cancel();
                timer.purge();
                Log.d(TAG, "upload");
                Intent myIntent = new Intent(this, InsertRestrictions.class);
                this.startActivity(myIntent);
                return true;

            default:
                return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_icon).getActionView();
        searchView.setQueryHint("Municipio");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                municipio = query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                /*Cursor cursor = (Cursor) mAdapter.getItem(position);
                String txt = cursor.getString(cursor.getColumnIndex("cityName"));
                searchView.setQuery(munList.get(position),true);*/
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                //searchView.setQuery(munList.get(position),true);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
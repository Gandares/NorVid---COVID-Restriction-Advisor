package com.example.norvid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

    EditText tdqueda;
    String mun, provUser;
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

        provUser = "Prueba";

        tdqueda = findViewById(R.id.bbddtext);
        button = findViewById(R.id.savebutton);
        logout = findViewById(R.id.logOutButton);

        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                Log.d(TAG, emailUse + "/dsfsdfasfdf");

                //  DELETE
           /*
           db.collection("Zona").document("Granada")
                   .delete();
           */
                // GET

                DocumentReference dbgetter = db.collection("users").document(emailUse);
                Log.d(TAG, "Hasta aqui bien");
                dbgetter.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d(TAG, "Entramos, señores");
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "Entramos, señores");
                                provUser = document.getData().get("Prov").toString();
                                Map<String, Object> data = new HashMap<>();
                                data.put("Toque de queda", tdqueda.getText().toString());

                                /*Resto de data.put (valores)*/

                                DocumentReference dbField = db.collection("Provincias").document(provUser);
                                dbField.set(data);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

                // ADD


                // UPDATE
           /*
           lmao.update("Frontera", "Abierta");
            */
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent myIntent = new Intent(InsertRestrictions.this, MainActivity.class);
                InsertRestrictions.this.startActivity(myIntent);
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
package com.example.norvid;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertRestrictions extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "DocSnippets";
    public static final int FAST_UPDATE_INTERVAL = 5;

    EditText tdqueda;
    BottomNavigationView botNav;
    String mun;

    FusedLocationProviderClient fusedLocationProviderClient;

    LocationRequest locationRequest;

    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_restrictions);

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

        botNav = findViewById(R.id.bottom_navigation);
        tdqueda = findViewById(R.id.bbddtext);

        botNav.setOnNavigationItemSelectedListener( item -> {
            return navigation(item);
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
        Button button = (Button) findViewById(R.id.savebutton);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                //  DELETE
               /*
               db.collection("Zona").document("Granada")
                       .delete();
               */
                // GET
               /*
               DocumentReference docRef = db.collection("Zona").document("Granada");
               docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if (task.isSuccessful()) {
                           DocumentSnapshot document = task.getResult();
                           if (document.exists()) {
                               Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                           } else {
                               Log.d(TAG, "No such document");
                           }
                       } else {
                           Log.d(TAG, "get failed with ", task.getException());
                       }
                   }
               });*/

                // ADD

                Map<String, Object> data = new HashMap<>();
                data.put("Toque de queda", tdqueda.getText().toString());

                DocumentReference dbField = db.collection("Provincias").document(mun);
                dbField.set(data);

                // UPDATE
               /*
               lmao.update("Frontera", "Abierta");
                */
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return navigation(item);
    }

    public boolean navigation(MenuItem item){
        switch (item.getItemId()) {
            case R.id.coronavirus:
                Log.d(TAG, "corona");
                Intent myIntent = new Intent(this, MainActivity.class);
                this.startActivity(myIntent);
                return true;

            case R.id.upload:
                Log.d(TAG, "upload");
                return true;

            default:
                return true;
        }
    }
}
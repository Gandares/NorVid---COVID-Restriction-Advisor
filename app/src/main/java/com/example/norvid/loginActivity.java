package com.example.norvid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class loginActivity extends AppCompatActivity {

    BottomNavigationView botNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        botNav = findViewById(R.id.bottom_navigation);

        botNav.setOnNavigationItemSelectedListener( item -> {
            return navigation(item);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return navigation(item);
    }

    public boolean navigation(MenuItem item){
        switch (item.getItemId()) {
            case R.id.coronavirus:
                Intent myIntent = new Intent(this, MainActivity.class);
                this.startActivity(myIntent);
                return true;

            case R.id.upload:
                Intent insertIntent = new Intent(this, InsertRestrictions.class);
                this.startActivity(insertIntent);
                return true;

            default:
                return true;
        }
    }
}
package com.example.norvid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    TextView lol;
    BottomNavigationView botNav;
    Button loginButton;
    EditText email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        botNav = findViewById(R.id.bottom_navigation);
        loginButton = findViewById(R.id.buttonLogin);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        lol = findViewById(R.id.textView);

        botNav.setOnNavigationItemSelectedListener( item -> {
            return navigation(item);
        });

        loginButton.setOnClickListener(v -> {
            if(email.getText()!=null && pass.getText()!=null){
                mAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user.getEmail()!="user@admin.es") {
                                    Intent insertIntent = new Intent(loginActivity.this, InsertRestrictions.class).putExtra("email", user.getEmail());
                                    loginActivity.this.startActivity(insertIntent);
                                }
                            } else {
                                AlertDialog.Builder error = new AlertDialog.Builder(loginActivity.this);
                                error.setTitle("Error");
                                error.setMessage("Correo electrónico o contraseña inválido.");
                                error.setPositiveButton("Aceptar", null);

                                AlertDialog dialog = error.create();
                                dialog.show();
                            }
                        }
                    });
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
                Intent myIntent = new Intent(this, MainActivity.class);
                this.startActivity(myIntent);
                return true;

            default:
                return true;
        }
    }
}
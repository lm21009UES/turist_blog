package com.example.turistblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InitSesion extends AppCompatActivity {
    public Button btnAcceder;
    public ProgressBar progreso;
    public EditText email, password;
    private FirebaseAuth mAuth;
    public TextView registrar;
    private Intent HomeActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_sesion);
        btnAcceder = findViewById(R.id.btnSingUp);
        progreso = findViewById(R.id.progressBar);
        email = findViewById(R.id.email);
        registrar = findViewById(R.id.registrarCuenta);
        password = findViewById(R.id.password);
        progreso.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        HomeActivity = new Intent(this, activityHome.class);
        btnAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progreso.setVisibility(View.VISIBLE);
                btnAcceder.setVisibility(View.INVISIBLE);
                final String correo = email.getText().toString();
                final String contraseña = password.getText().toString();
                if(correo.isEmpty() || contraseña.isEmpty()){
                    Toast.makeText(InitSesion.this, "Por favor verifique y llene todos los campos", Toast.LENGTH_SHORT).show();
                    progreso.setVisibility(View.INVISIBLE);
                    btnAcceder.setVisibility(View.VISIBLE);
                }
                else {
                    accceder(correo, contraseña);
                }
            }
        });
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), activity_register.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void accceder(String correo, String contraseña) {
        mAuth.signInWithEmailAndPassword(correo,contraseña).
                addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progreso.setVisibility(View.INVISIBLE);
                        btnAcceder.setVisibility(View.VISIBLE);
                        UpdateUI();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InitSesion.this, "Correo y contraseña Invalidos", Toast.LENGTH_SHORT).show();
                        progreso.setVisibility(View.INVISIBLE);
                        btnAcceder.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void UpdateUI() {
        startActivity(HomeActivity);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            UpdateUI();
        }
    }

}

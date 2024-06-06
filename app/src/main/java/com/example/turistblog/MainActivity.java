package com.example.turistblog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private boolean isReady = false;
    public Button login, registro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Base_Theme_TuristBlog);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // Comprueba si los datos iniciales están listos.
                if (isReady) {
                    // Los datos están listos; comienza a dibujar.
                    content.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                } else {
                    // Los datos no están listos; suspende el dibujo.
                    return false;
                }
            }
        });

        // Simulación de una tarea que hace que los datos estén listos después de un retraso.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isReady = true;
            }
        }, 1500); // 5 segundos de retraso

        login = findViewById(R.id.btnAcceder);
        registro = findViewById(R.id.btnRegistrar);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InitSesion.class);
                startActivity(intent);
                finish();
            }
        });
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, activity_register.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
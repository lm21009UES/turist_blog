package com.example.turistblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Pattern;

public class activity_register extends AppCompatActivity {
    public ImageView imagenUser;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    public Uri pickedImgUri;
    Intent temporal;
    public EditText userEmail, userPassword, userPassword2, userName;
    public ProgressBar progeso;
    public Button btnRegistrar;
    private FirebaseAuth mAuth;
    public TextView acceder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        imagenUser = findViewById(R.id.imagenUser);
        userName = findViewById(R.id.regName);
        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userPassword2 = findViewById(R.id.regPassword2);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        progeso = findViewById(R.id.progreso);
        acceder = findViewById(R.id.acceder);

        mAuth = FirebaseAuth.getInstance();

        imagenUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(Build.VERSION.SDK_INT >=22){
                  //  checkRequestForPermision();
                //}
                //else{
                    openGallery();
                //}
            }
        });
        acceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InitSesion.class);
                startActivity(intent);
                finish();
            }
        });
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nombre = userName.getText().toString();
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPassword2.getText().toString();
                if(nombre.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty() ){
                    Toast.makeText(activity_register.this, "Por favor verifique todos los Datos", Toast.LENGTH_SHORT).show();
                    progeso.setVisibility(View.INVISIBLE);
                    btnRegistrar.setVisibility(View.VISIBLE);
                } else if (!password.equals(password2)) {
                    Toast.makeText(activity_register.this, "Los campos de contraseña deben coincidir", Toast.LENGTH_SHORT).show();
                    progeso.setVisibility(View.INVISIBLE);
                    btnRegistrar.setVisibility(View.VISIBLE);
                }
                else{
                    boolean cont = isValidPassword(password);
                    boolean correo = isValidEmail(email);
                    if(!cont){
                        Toast.makeText(activity_register.this, "La Contraseña debe Tener minimo 8 Caracteres", Toast.LENGTH_SHORT).show();
                        Toast.makeText(activity_register.this, "Debe incluir Caracteres especiales", Toast.LENGTH_SHORT).show();
                    }
                    if (!correo) {
                        Toast.makeText(activity_register.this, "El correo no tiene formato Valido", Toast.LENGTH_SHORT).show();
                        Toast.makeText(activity_register.this, "Formato Valido: user@gmail.com", Toast.LENGTH_SHORT).show();

                    }
                    if(cont && correo){
                        progeso.setVisibility(View.VISIBLE);
                        btnRegistrar.setVisibility(View.INVISIBLE);
                        createUserAccount(nombre, email, password);
                    }
                    
                }
            }
        });
    }
    //para validar contrasenia
    private boolean isValidPassword(String password) {
        // Password should have at least 8 characters and contain special characters
        if (password.length() >= 8) {
            // Check for special characters
            String specialCharacters = "!@#$%^&*()-_=+|<>?{}[]~";
            for (char c : password.toCharArray()) {
                if (specialCharacters.contains(String.valueOf(c))) {
                    return true;
                }
            }
        }
        return false;
    }
    // para validar Email
    private boolean isValidEmail(String email) {
        // Regular expression to validate email ending with @gmail.com
        String emailPattern = "^[A-Za-z0-9._%+-]+@gmail\\.com$";
        Pattern pattern = Pattern.compile(emailPattern);
        return email != null && pattern.matcher(email).matches();
    }
    private void createUserAccount(String nombre, String email, String password) {
        if(temporal != null) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            //Toast.makeText(activity_register.this, "Todo Salio Bien", Toast.LENGTH_SHORT).show();
                            updateUserInfo(nombre, mAuth.getCurrentUser());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity_register.this, "Salio algo mal " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progeso.setVisibility(View.INVISIBLE);
                            btnRegistrar.setVisibility(View.VISIBLE);
                        }
                    });
        }else{
            progeso.setVisibility(View.INVISIBLE);
            btnRegistrar.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Necesita agregar Una imagen, Haga click en el icono de imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserInfo(String nombre, FirebaseUser currentUser) {
            StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
            StorageReference imageFilePath = mStorage.child(temporal.getData().getLastPathSegment());
            imageFilePath.putFile(temporal.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nombre)
                                    .setPhotoUri(uri)
                                    .build();
                            currentUser.updateProfile(profileUpdate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(activity_register.this, "Registro Completado", Toast.LENGTH_SHORT).show();
                                                UpdateUI();
                                            } else {
                                                Toast.makeText(activity_register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.e("FirebaseUpdateError", "Error updating profile", task.getException());
                                            }
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity_register.this, "Error obteniendo URL de la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("FirebaseStorageError", "Error getting download URL", e);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity_register.this, "Error subiendo imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseStorageError", "Error uploading image", e);
                }
            });
    }

    private void UpdateUI() {
        Intent inten = new Intent(getApplicationContext(), activityHome.class);
        startActivity(inten);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if(resultCode == RESULT_OK && requestCode == REQUESCODE && data != null){
          //  pickedImgUri = data.getData();
            //imagenUser.setImageURI(pickedImgUri);
        //}
        if (requestCode == REQUESCODE && resultCode == RESULT_OK) {
            imagenUser.setImageURI(data.getData());
            temporal = data;
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    private void checkRequestForPermision() {
        if(ContextCompat.checkSelfPermission(activity_register.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity_register.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(activity_register.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);
            }
        }
        else {
            openGallery();
        }
    }
}
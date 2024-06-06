package com.example.turistblog;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.turistblog.Fragment.ConfiguracionesFragment;
import com.example.turistblog.Fragment.HomeFragment;
import com.example.turistblog.Fragment.PerfilFragment;
import com.example.turistblog.Models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class activityHome extends AppCompatActivity {

    static int PReqCode = 2;
    static int REQUESCODE = 2;
    public Uri pickedImgUri = null;
    Dialog add_post;
    ImageView user_image, post_image, add_btn, agregarPost;
    TextView txt_nombre, txt_descripcion, txt_ubicacion;
    ProgressBar click_progress;
    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    public BottomNavigationView menuOpciones;
    public Intent temporal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        menuOpciones = findViewById(R.id.menuOpciones);

        // Cargar el tipo de letra personalizado desde assets
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Aclonica.ttf");

        // Iterar sobre los elementos del menú y aplicar el tipo de letra
        Menu menu = menuOpciones.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            applyFontToMenuItem(menuItem, typeface);
        }

        // Obtener el fragmento que se debe abrir desde el Intent
        String fragmentToOpen = getIntent().getStringExtra("abrirFavoritos");
        if (fragmentToOpen != null) {
            if (fragmentToOpen.equals("favoritos")) {
                // Cargar el fragmento de favoritos
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new ConfiguracionesFragment())
                        .commit();
                menuOpciones.setSelectedItemId(R.id.nav_setting);
            } else {
                // Cargar el fragmento por defecto (por ejemplo, HomeFragment)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new HomeFragment())
                        .commit();
            }
        }
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // ini popup
        ini_popup();

        setup_popup_image_click();

        menuOpciones = findViewById(R.id.menuOpciones);
        agregarPost = findViewById(R.id.agregarPost);
        menuOpciones.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new HomeFragment()).commit();
                        agregarPost.setVisibility(View.VISIBLE);
                        break;
                    case R.id.nav_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new PerfilFragment()).commit();
                        agregarPost.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.nav_setting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new ConfiguracionesFragment()).commit();
                        agregarPost.setVisibility(View.INVISIBLE);
                        break;
                }
                return true;
            }
        });
        agregarPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_post.show();
            }
        });
    }

    private void applyFontToMenuItem(MenuItem menuItem, Typeface typeface) {
        SpannableString title = new SpannableString(menuItem.getTitle());
        title.setSpan(new CustomTypefaceSpan("", typeface), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        menuItem.setTitle(title);
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Cerrar Sesion");
        builder.setMessage("¿Desea Cerrar Sesión?");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(activityHome.this, "Cesión Cerrada", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new HomeFragment()).commit();
                Toast.makeText(activityHome.this, "Cierre de sesión Cancelada", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }

    private void ini_popup() {
        add_post = new Dialog(this);
        add_post.setContentView(R.layout.add_post);
        add_post.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        add_post.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        add_post.getWindow().getAttributes().gravity = Gravity.CENTER;

        // ini poput widgets
        user_image = add_post.findViewById(R.id.imageView3);
        post_image = add_post.findViewById(R.id.imageView5);
        txt_nombre = add_post.findViewById(R.id.editTextText);
        txt_descripcion = add_post.findViewById(R.id.editTextText2);
        txt_ubicacion = add_post.findViewById(R.id.editTextText3);
        click_progress = add_post.findViewById(R.id.progressBar2);
        add_btn = add_post.findViewById(R.id.imageView6);

        // load current user profile photo
        Glide.with(this).load(currentUser.getPhotoUrl()).into(user_image);
        //Glide.with(activityHome.this).load(currentUser.getPhotoUrl()).into(user_image);

        // add post click listener
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_btn.setVisibility(View.INVISIBLE);
                click_progress.setVisibility(View.VISIBLE);
                final String nombre = txt_nombre.getText().toString();
                final String descripcion = txt_descripcion.getText().toString();
                final String ubicacion = txt_ubicacion.getText().toString();
                if (nombre.isEmpty() || descripcion.isEmpty() || ubicacion.isEmpty() || temporal == null ) {
                    showMessage("Por favor llenar todos los elementos y escoger una imagen");
                    click_progress.setVisibility(View.INVISIBLE);
                    add_btn.setVisibility(View.VISIBLE);
                } else {

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                    StorageReference imageFilePath = storageReference.child(temporal.getData().getLastPathSegment());

                    imageFilePath.putFile(temporal.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String image_download_link = uri.toString();

                                    // crear post objeto
                                    Post post = new Post(txt_nombre.getText().toString(), txt_descripcion.getText().toString(),
                                            txt_ubicacion.getText().toString(), image_download_link, currentUser.getPhotoUrl().toString());

                                    // agregar post a firebase
                                    addpost(post);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showMessage(e.getMessage());
                                    click_progress.setVisibility(View.INVISIBLE);
                                    add_btn.setVisibility(View.VISIBLE);

                                }
                            });

                        }
                    });
                }
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(activityHome.this, message, Toast.LENGTH_SHORT).show();
    }

    private void addpost(Post post) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("Posts").push();

        String key = myref.getKey();
        post.setPost_key(key);

        myref.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                showMessage("Post agregado correctamente");
                click_progress.setVisibility(View.INVISIBLE);
                add_btn.setVisibility(View.VISIBLE);
                txt_nombre.setText("");
                txt_descripcion.setText("");
                txt_ubicacion.setText("");
                add_post.dismiss();
            }
        });

    }

    private void setup_popup_image_click() {
        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void checkRequestForPermision() {
        if(ContextCompat.checkSelfPermission(activityHome.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activityHome.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(activityHome.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);
            }
        }
        else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
            pickedImgUri = data.getData();
            temporal = data;
            post_image.setImageURI(pickedImgUri);
        }
    }

}
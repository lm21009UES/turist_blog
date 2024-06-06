package com.example.turistblog;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.turistblog.Adapters.CommentAdapter;
import com.example.turistblog.Fragment.ConfiguracionesFragment;
import com.example.turistblog.Models.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
    public ImageView img_post, img_user_post, img_curren_user, agregarFavorito;
    public TextView txt_post_descripcion, txt_post_data_name, txt_post_nombre, txt_post_ubicacion;
    public RatingBar calificacion;
    public EditText edit_text_comentario;
    public Button btn_add_comentario;
    public String post_key;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    public float puntaje;

    public RecyclerView rv_comment;
    public CommentAdapter commentAdapter;
    public List<Comment> list_comment;
    static String COMMENT_KEY = "Comment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        img_post = findViewById(R.id.post_detail_image);
        img_user_post = findViewById(R.id.post_detail_user_img);
        img_curren_user = findViewById(R.id.post_detail_currentuser_img);
        agregarFavorito = findViewById(R.id.favorito);

        txt_post_nombre = findViewById(R.id.post_detail_nombre);
        txt_post_descripcion = findViewById(R.id.post_detail_descripcion);
        txt_post_ubicacion = findViewById(R.id.post_detail_ubicacion);
        txt_post_data_name = findViewById(R.id.post_detail_date_name);

        edit_text_comentario = findViewById(R.id.post_detail_comentario);
        btn_add_comentario = findViewById(R.id.post_detail_add_comentario);

        rv_comment = findViewById(R.id.rv_comment);

        calificacion = findViewById(R.id.ratingBar);
        calificacion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                puntaje = rating;
                //Toast.makeText(PostDetailActivity.this, "Puntaje "+String.valueOf(puntaje), Toast.LENGTH_SHORT).show();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        btn_add_comentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_add_comentario.setVisibility(View.INVISIBLE);
                if(puntaje != 0.0f) {
                    DatabaseReference comment_reference = firebaseDatabase.getReference(COMMENT_KEY).child(post_key).push();
                    String comment_content = edit_text_comentario.getText().toString();
                    String uid = firebaseUser.getUid();
                    String uname = firebaseUser.getDisplayName();
                    String uimg = firebaseUser.getPhotoUrl().toString();
                    Comment comment = new Comment(comment_content, uid, uimg, uname, puntaje);

                    comment_reference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            showMessage("Comentario agregado correctamente");
                            edit_text_comentario.setText("");
                            btn_add_comentario.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessage("Error al agregar el comentario: " + e.getMessage());
                        }
                    });
                }else{
                    btn_add_comentario.setVisibility(View.VISIBLE);
                    Toast.makeText(PostDetailActivity.this, "Antes de Guardar su comentario por Favor Agregue una Calificación", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String post_image = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(post_image).into(img_post);

        String post_nombre = getIntent().getExtras().getString("nombre");
        txt_post_nombre.setText(post_nombre);

        String user_post_image = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(user_post_image).into(img_user_post);

        String post_descripcion = getIntent().getExtras().getString("descripcion");
        txt_post_descripcion.setText(post_descripcion);

        String post_ubicacion = getIntent().getExtras().getString("ubicacion");
        txt_post_ubicacion.setText(post_ubicacion);
        
        String accion = getIntent().getExtras().getString("favorito");

        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(img_curren_user);

        post_key = getIntent().getExtras().getString("postKey");
        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        String key = getIntent().getExtras().getString("key");

        if (accion.equals("Quitar")) {
            agregarFavorito.setImageResource(R.drawable.quitar_icon);
        }
        
        txt_post_data_name.setText(date);

        agregarFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(accion.equals("Agregar")) {
                    String uid = firebaseUser.getUid();
                    DatabaseReference favoriteRef = firebaseDatabase.getReference().child("Favoritos").child(uid);
                    favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<String> favoritePostIds = new ArrayList<>();
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                String postId = postSnapshot.getValue(String.class);
                                favoritePostIds.add(postId);
                            }
                            if (favoritePostIds.contains(post_key)) {
                                Toast.makeText(PostDetailActivity.this, "Ya esta en su lista de Deseos", Toast.LENGTH_SHORT).show();
                            } else {
                                DatabaseReference favoritos_reference = firebaseDatabase.getReference("Favoritos").child(uid).push();
                                favoritos_reference.setValue(post_key).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        showMessage("Agregado a la lista de Deseos");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showMessage("Error al agregar Favorito: " + e.getMessage());
                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    //Toast.makeText(PostDetailActivity.this, firebaseUser.getUid(), Toast.LENGTH_SHORT).show();
                    DatabaseReference favoriteRef = firebaseDatabase.getReference().child("Favoritos").child(firebaseUser.getUid()).child(key);
                    favoriteRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PostDetailActivity.this, "El Post ha sido removido de su lista de Deseos", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), activityHome.class);
                                intent.putExtra("abrirFavoritos", "favoritos");
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(PostDetailActivity.this, "Ha ocurrido un error "+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        iniRvComment();
    }

    private void iniRvComment() {
        rv_comment.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference comment_ref = firebaseDatabase.getReference(COMMENT_KEY).child(post_key);

        comment_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_comment = new ArrayList<>();
                for (DataSnapshot snap:snapshot.getChildren()) {
                    Comment comment = snap.getValue(Comment.class);
                    list_comment.add(comment);
                }

                commentAdapter = new CommentAdapter(getApplicationContext(), list_comment);
                rv_comment.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showMessage(String comment) {
        Toast.makeText(this, comment, Toast.LENGTH_SHORT).show();
    }

    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;
    }
}
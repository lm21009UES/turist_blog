package com.example.turistblog.Fragment;

import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turistblog.Adapters.PostAdapter;
import com.example.turistblog.Models.Post;
import com.example.turistblog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfiguracionesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfiguracionesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public PostAdapter postAdapter;
    public ListView listapost;
    public ImageView imgVacio;
    public TextView vacio;
    public FirebaseAuth mAuth;
    public DatabaseReference databaseReference;
    public FirebaseDatabase firebaseDatabase;
    public List<Post> listPost;

    public ConfiguracionesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfiguracionesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfiguracionesFragment newInstance(String param1, String param2) {
        ConfiguracionesFragment fragment = new ConfiguracionesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_home, container, false);
        listapost = root.findViewById(R.id.lista_post);
        imgVacio = root.findViewById(R.id.favoritosImg);
        vacio = root.findViewById(R.id.txtVacio);
        mAuth = FirebaseAuth.getInstance();
        listPost = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            String uid = user.getUid();
            DatabaseReference favoriteRef = databaseReference.child("Favoritos").child(uid);
            favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> favoritePostIds = new ArrayList<>();
                    List<String> ids = new ArrayList<>();
                    for(DataSnapshot postSnapshot: snapshot.getChildren()){
                        String postId = postSnapshot.getValue(String.class);
                        String llave = postSnapshot.getKey();
                        favoritePostIds.add(postId);
                        ids.add(llave);
                    }
                    CountDownLatch latch = new CountDownLatch(favoritePostIds.size());

                    for (String postId:favoritePostIds) {
                        firebaseDatabase = FirebaseDatabase.getInstance();
                        databaseReference = firebaseDatabase.getReference("Posts").child(postId);
                        //Toast.makeText(getContext(), postId, Toast.LENGTH_SHORT).show();
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Post post = dataSnapshot.getValue(Post.class);
                                listPost.add(post);
                                //Toast.makeText(getContext(), "Prueba"+post.getPost_key(), Toast.LENGTH_SHORT).show();
                                latch.countDown();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(), "Error al obtener detalles del post", Toast.LENGTH_SHORT).show();
                            }
                            
                        });
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                latch.await();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Notifica al adaptador que los datos han cambiado
                                        if(ids.size() ==0){
                                            imgVacio.setVisibility(View.VISIBLE);
                                            vacio.setVisibility(View.VISIBLE);
                                        }
                                        else{
                                            imgVacio.setVisibility(View.INVISIBLE);
                                            vacio.setVisibility(View.INVISIBLE);
                                        }
                                        postAdapter = new PostAdapter(getContext(),listPost,ids, "Quitar");
                                        listapost.setAdapter(postAdapter);

                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return root;
    }

}
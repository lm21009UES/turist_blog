package com.example.turistblog.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.turistblog.Models.Post;
import com.example.turistblog.PostDetailActivity;
import com.example.turistblog.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends BaseAdapter {
    public Context context;
    public List<Post> mData;
    public List<String> mIds;
    public String accion;

    public PostAdapter(Context context, List<Post> mData, List<String> mIds, String accion) {
        this.context = context;
        this.mData = mData;
        this.mIds = mIds;
        this.accion = accion;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.row_post_item,null);

        TextView titulo = convertView.findViewById(R.id.row_post_title);

        // Cargar el tipo de letra personalizado desde assets
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "Aclonica.ttf");

        // Aplicar el tipo de letra personalizado al TextView
        titulo.setTypeface(typeface);

        ImageView imgpost = convertView.findViewById(R.id.row_post);
        ImageView imguser = convertView.findViewById(R.id.row_post_imgUser);

        // Obtener las dimensiones deseadas para la imagen
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        int targetWidth = widthPixels; // Ancho deseado
        int targetHeight = (int) (targetWidth * 0.75); // Establecer la altura según la relación de aspecto deseada (por ejemplo, 3:4)

        // Establecer las dimensiones de la imagen
        ViewGroup.LayoutParams layoutParams = imgpost.getLayoutParams();
        layoutParams.width = targetWidth;
        layoutParams.height = targetHeight;
        imgpost.setLayoutParams(layoutParams);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent post_detail_activity = new Intent(context, PostDetailActivity.class);
                //int position = getAdapterPosition();
                post_detail_activity.putExtra("nombre", mData.get(position).getNombre());
                post_detail_activity.putExtra("postImage", mData.get(position).getImagen());
                post_detail_activity.putExtra("descripcion", mData.get(position).getDescripcion());
                post_detail_activity.putExtra("ubicacion", mData.get(position).getUbicacion());
                post_detail_activity.putExtra("postKey", mData.get(position).getPost_key());
                post_detail_activity.putExtra("userPhoto", mData.get(position).getUser_foto());
                post_detail_activity.putExtra("favorito",accion);
                if(mIds.size() != 0){
                    post_detail_activity.putExtra("key", mIds.get(position));
                }

                //post_detail_activity.putExtra("userName", mData.get(position).getUsername());
                long timestamp = (long) mData.get(position).getTime_stamp();
                post_detail_activity.putExtra("postDate", timestamp);
                context.startActivity(post_detail_activity);
            }
        });

        titulo.setText(mData.get(position).getNombre());

        Log.d("Ruta", mData.get(position).getImagen());
        //Log.d("Usuario", mData.get(position).getUser_foto());

        Picasso.get().load(mData.get(position).getImagen()).into(imgpost);
        Picasso.get().load(mData.get(position).getUser_foto()).into(imguser);

        return convertView;
    }
}

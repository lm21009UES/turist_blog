package com.example.turistblog.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turistblog.Models.Comment;
import com.example.turistblog.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    public Context context;
    public List<Comment> mData;

    public CommentAdapter(Context context, List<Comment> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.row_comment,parent,false);
        return new CommentViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Picasso.get().load(mData.get(position).getUimg()).into(holder.img_user);
        holder.txt_name.setText(mData.get(position).getUname());
        holder.txt_content.setText(mData.get(position).getContent());
        holder.txt_date.setText(timestampToString((Long)mData.get(position).getTimestamp()));
        holder.calificacion.setRating(mData.get(position).getCalificacion());
        holder.calificacion.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        ImageView img_user;
        TextView txt_name ;
        TextView txt_content;
        TextView txt_date;
        TextView txt_calificacion;
        RatingBar calificacion;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.comment_user_img);
            txt_name = itemView.findViewById(R.id.comment_username);
            txt_content = itemView.findViewById(R.id.comment_content);
            txt_date = itemView.findViewById(R.id.comment_date);
            calificacion = itemView.findViewById(R.id.ratingBar2);
            txt_calificacion = itemView.findViewById(R.id.textView4);

            // Cargar el tipo de letra personalizado desde assets
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "Aclonica.ttf");

            // Aplicar el tipo de letra personalizado al TextView
            txt_name.setTypeface(typeface);
            txt_content.setTypeface(typeface);
            txt_date.setTypeface(typeface);
            txt_calificacion.setTypeface(typeface);
        }
    }

    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm", calendar).toString();
        return date;
    }
}

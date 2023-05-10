package com.br.guardapaginas.classes.holders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.guardapaginas.R;

public class BookViewHolder extends RecyclerView.ViewHolder {

    private Context context;

    ImageView bookCover;
    TextView  bookTitle;
    TextView  bookGenders;

    public BookViewHolder(@NonNull View itemView) {
        super(itemView);
        bookCover   = itemView.findViewById(R.id.bookCover);
        bookTitle   = itemView.findViewById(R.id.bookTitle);
        bookGenders = itemView.findViewById(R.id.bookGenders);
    }
}

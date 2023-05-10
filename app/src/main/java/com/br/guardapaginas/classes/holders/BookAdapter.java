package com.br.guardapaginas.classes.holders;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.guardapaginas.R;
import com.br.guardapaginas.classes.Book;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    Context context;
    List<Book> bookItems;

    public BookAdapter(Context context, List<Book> bookItems) {
        this.context = context;
        this.bookItems = bookItems;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookViewHolder(LayoutInflater.from(context).inflate(R.layout.book_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.bookTitle.setText(bookItems.get(position).getTitle());
        holder.bookGenders.setText(bookItems.get(position).getGenders());
        holder.bookCover.setImageBitmap(bookItems.get(position).getBookCover());
    }

    @Override
    public int getItemCount() {
        return bookItems.size();
    }
}

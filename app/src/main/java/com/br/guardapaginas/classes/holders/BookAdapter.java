package com.br.guardapaginas.classes.holders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.guardapaginas.R;
import com.br.guardapaginas.classes.Book;
import com.br.guardapaginas.helpers.Functions;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder> implements BookRecycleViewInterface {

    private final BookRecycleViewInterface bookRecycleViewInterface;

    Context context;
    List<Book> bookItems;

    public BookAdapter(Context context, List<Book> bookItems, BookRecycleViewInterface bookRecycleViewInterface) {
        this.context = context;
        this.bookItems = bookItems;
        this.bookRecycleViewInterface = bookRecycleViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.book_item, parent, false);
        return new MyViewHolder(view, bookRecycleViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bookTitle.setText(bookItems.get(position).getTitle());
        holder.bookGenders.setText(bookItems.get(position).getGendersName());
        if(bookItems.get(position).getBookCover() != null)
            holder.bookCover.setImageBitmap(Functions.parseByteArrayToBitMap(bookItems.get(position).getBookCover()));
        Book obj = bookItems.get(position);
        if(obj.getTitle().equals("oiyrf")){
            System.out.println("Meus generos: "+obj.getGendersName());
        }
    }

    @Override
    public int getItemCount() {
        return bookItems.size();
    }

    @Override
    public void onItemClick(int Position) {

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView bookCover;
        TextView bookTitle, bookGenders;

        public MyViewHolder(@NonNull View itemView, BookRecycleViewInterface bookRecycleViewInterface) {
            super(itemView);
            bookCover   = itemView.findViewById(R.id.bookCover);
            bookTitle   = itemView.findViewById(R.id.genderName);
            bookGenders = itemView.findViewById(R.id.bookGenders);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bookRecycleViewInterface != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            bookRecycleViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}

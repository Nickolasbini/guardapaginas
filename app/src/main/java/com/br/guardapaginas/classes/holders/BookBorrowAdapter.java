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
import com.br.guardapaginas.classes.BookBorrowing;
import com.br.guardapaginas.helpers.Functions;

import java.util.List;

public class BookBorrowAdapter extends RecyclerView.Adapter<BookBorrowAdapter.MyViewHolder> implements BookBorrowRecycleViewInterface {

    private final BookBorrowRecycleViewInterface bookBorrowRecycleViewInterface;

    Context context;
    List<BookBorrowing> bookBorrowItems;

    public BookBorrowAdapter(Context context, List<BookBorrowing> bookBorrowItems, BookBorrowRecycleViewInterface bookBorrowRecycleViewInterface) {
        this.context = context;
        this.bookBorrowItems = bookBorrowItems;
        this.bookBorrowRecycleViewInterface = bookBorrowRecycleViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.book_borrow_item, parent, false);
        return new MyViewHolder(view, bookBorrowRecycleViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        List<String> data = bookBorrowItems.get(position).getBookBorrowData();
        holder.readerName.setText(data.get(0));
        holder.readerEmail.setText(data.get(1));
        holder.bookName.setText(data.get(2));
        holder.borrowDate.setText(data.get(3));
    }

    @Override
    public int getItemCount() {
        return bookBorrowItems.size();
    }

    @Override
    public void onItemClick(int Position) {

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView readerName, readerEmail, bookName, borrowDate;

        public MyViewHolder(@NonNull View itemView, BookBorrowRecycleViewInterface bookBorrowRecycleViewInterface) {
            super(itemView);
            readerName  = itemView.findViewById(R.id.readerName);
            readerEmail = itemView.findViewById(R.id.readerEmail);
            bookName    = itemView.findViewById(R.id.bookName);
            borrowDate  = itemView.findViewById(R.id.borrowDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bookBorrowRecycleViewInterface != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            bookBorrowRecycleViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}

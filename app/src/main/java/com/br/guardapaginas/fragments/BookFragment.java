package com.br.guardapaginas.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.br.guardapaginas.R;
import com.br.guardapaginas.SaveBookView;
import com.br.guardapaginas.classes.Book;
import com.br.guardapaginas.classes.holders.BookAdapter;
import com.br.guardapaginas.classes.holders.BookRecycleViewInterface;
import com.br.guardapaginas.helpers.Functions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookFragment extends Fragment implements BookRecycleViewInterface {

    List<Book> listOfMyBooks;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View currentView;
    RecyclerView recyclerView;

    public BookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookFragment newInstance(String param1, String param2) {
        BookFragment fragment = new BookFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view   = inflater.inflate(R.layout.fragment_book, container, false);
        currentView = view;
        listBooks();

        ImageView saveBookBtn = (ImageView) view.findViewById(R.id.addNewBookBtn);
        saveBookBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent saveBookIntent = new Intent(getActivity().getApplicationContext(), SaveBookView.class);
                saveBookIntent.putExtra("BOOK_ID", "0");
                startActivityForResult(saveBookIntent, 1);
            }
        });

        ImageView goBackBtn = (ImageView) view.findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // This works but, it would be best to send something as a flag in order to perform the click
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public List<Book> getMyBooks(String status){
        Book obj = new Book(getContext());
        List<Book> results = obj.fetchAll(status);
        return results;
    }

    public void listBooks(){
        recyclerView = currentView.findViewById(R.id.listOfBooks);
        listOfMyBooks  = getMyBooks("1");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new BookAdapter(getContext(), listOfMyBooks, this));
    }

    @Override
    public void onItemClick(int position) {
        Intent saveBookIntent = new Intent(getActivity().getApplicationContext(), SaveBookView.class);
        saveBookIntent.putExtra("BOOK_ID", Functions.parseToString(listOfMyBooks.get(position).getId()));
        startActivityForResult(saveBookIntent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Aqui esta o código: "+requestCode);
        switch(requestCode) {
            case 1:
                if(resultCode == getActivity().RESULT_OK){
                    listBooks();
                }
                break;
        }
    };
}
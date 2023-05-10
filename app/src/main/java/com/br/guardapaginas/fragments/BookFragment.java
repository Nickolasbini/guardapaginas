package com.br.guardapaginas.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.br.guardapaginas.R;
import com.br.guardapaginas.SaveBookView;
import com.br.guardapaginas.classes.Book;
import com.br.guardapaginas.classes.holders.BookAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookFragment extends Fragment {

    public static final String BOOK_ID = "0";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        // code goes here
        RecyclerView recyclerView = view.findViewById(R.id.listOfBooks);
        List<Book> listOfMyBooks  = getMyBooks("1");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new BookAdapter(getContext(), listOfMyBooks));

//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imageView.setImageBitmap(imageBitmap);
//        }

//        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        getIntent.setType("image/*");
//        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        pickIntent.setType("image/*");
//        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
//        startActivityForResult(chooserIntent, 1);
//        Bundle extras = data.getExtras();
//        Bitmap imageBitmap = (Bitmap) extras.get("data");
//        imageView.setImageBitmap(imageBitmap);

        ImageView saveBookBtn = (ImageView) view.findViewById(R.id.addNewBookBtn);
        saveBookBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent saveBookIntent = new Intent(getActivity().getApplicationContext(), SaveBookView.class);
                saveBookIntent.putExtra("BOOK_ID", "50");
                getActivity().startActivity(saveBookIntent);
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
}
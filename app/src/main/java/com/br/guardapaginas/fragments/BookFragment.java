package com.br.guardapaginas.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.br.guardapaginas.R;
import com.br.guardapaginas.SaveBookView;
import com.br.guardapaginas.classes.Book;
import com.br.guardapaginas.classes.holders.BookAdapter;
import com.br.guardapaginas.classes.holders.BookRecycleViewInterface;
import com.br.guardapaginas.helpers.Functions;
import com.br.guardapaginas.helpers.SessionManagement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
    LinearLayout filterDropdownOptions;
    ImageView filterDropdown;
    Boolean filterOptionsOpen = false;
    Spinner statusSelect;
    EditText searchInput;
    TextView noResultLabel;
    ScrollView bookListScroll;
    Integer currentPage = 1;
    List<Boolean> listParameters = new ArrayList<>();

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

        noResultLabel = (TextView) currentView.findViewById(R.id.noResultLabel);

        String[] statusOption = {"Ativo", "inativo"};
        statusSelect          = (Spinner) view.findViewById(R.id.statusSpinner);
        ArrayAdapter<String> genderSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, statusOption);
        statusSelect.setAdapter(genderSpinnerAdapter);

        searchInput = (EditText) view.findViewById(R.id.searchInput);

        listParameters.add(true);
        listParameters.add(false);
        listBooks(true, false, currentPage);

        filterDropdownOptions = (LinearLayout) view.findViewById(R.id.filterDropdownOptions);
        filterDropdownOptions.setVisibility(View.GONE);
        filterDropdown        = (ImageView) view.findViewById(R.id.showFilterOptions);
        filterDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filterOptionsOpen){
                    filterOptionsOpen = false;
                    filterDropdownOptions.setVisibility(View.GONE);
                }else{
                    filterOptionsOpen = true;
                    filterDropdownOptions.setVisibility(View.VISIBLE);
                }
            }
        });

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

        ImageView refreshButton = (ImageView) view.findViewById(R.id.refreshList);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listBooks(true, false, currentPage);
                searchInput.setText("");
            }
        });

        ImageView searchButton = (ImageView) view.findViewById(R.id.searchBtn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listBooks(false, true, currentPage);
            }
        });

        bookListScroll = (ScrollView) view.findViewById(R.id.bookListScroll);
        bookListScroll.setOnTouchListener(this::onTouch);
        bookListScroll.getViewTreeObserver().addOnScrollChangedListener(this::onScrollChanged);

        return view;
    }

    // We want to detect scroll and not touch,
    // so returning false in this member function
    @SuppressLint("ClickableViewAccessibility")
    public Boolean onTouch(View po, MotionEvent pi) {
        return false;
    }

    // Member function to detect Scroll,
    // when detected 0, it means bottom is reached
    public void onScrollChanged() {
        return;
//        View view = bookListScroll.getChildAt(bookListScroll.getChildCount() - 1);
//        Integer topDetector = bookListScroll.getScrollY();
//        Integer bottomDetector = view.getBottom() - (bookListScroll.getHeight() + bookListScroll.getScrollY());
//        if (bottomDetector == 0) {
//
//            listBooks(listParameters.get(0), listParameters.get(1), currentPage + 1);
//
//            Toast.makeText(getContext(), "Scroll View bottom reached", Toast.LENGTH_SHORT).show();
//        }
//        if (topDetector <= 0) {
//            Toast.makeText(getContext(), "Scroll View top reached", Toast.LENGTH_SHORT).show();
//        }
    }

    Integer formerResults = null;
    public List<Book> getMyBooks(String status, String name, Integer pageNumber){
        Book obj = new Book(getContext());
        List<Book> results = obj.fetchAll(status, name);
//        if(formerResults == null) {
//            formerResults = results.size();
//        }else{
//            if(results.size() > formerResults) {
//                currentPage++;
//                formerResults = results.size();
//            }
//        }
        return results;
    }

    public void listBooks(Boolean filterByStatus, Boolean filterByName, Integer page){
        listParameters.set(0, filterByStatus);
        listParameters.set(1, filterByName);
        String status = null;
        if(filterByStatus) {
            status  = statusSelect.getSelectedItem().toString();
            status = (status == "Ativo" ? "1" : "0");
        }
        String name = null;
        if(filterByName){
            name = searchInput.getText().toString();
            name = (name.equals("") ? null : name);
        }
        recyclerView   = currentView.findViewById(R.id.listOfBooks);
        listOfMyBooks  = getMyBooks(status, name, page);
        if(listOfMyBooks.size() > 0) {
            noResultLabel.setVisibility(View.GONE);
        }else{
            noResultLabel.setVisibility(View.VISIBLE);
        }
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
                    listBooks(true, false, currentPage);
                    searchInput.setText("");
                }
                break;
        }
    };
}
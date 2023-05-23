package com.br.guardapaginas.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.br.guardapaginas.ProfileView;
import com.br.guardapaginas.R;
import com.br.guardapaginas.SaveBookView;
import com.br.guardapaginas.classes.Book;

import org.w3c.dom.Text;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View currentView;
    private Book bookObj;
    private TextView userNameLabel;
    private TextView totalNumberOfBooks;
    private ImageView openTotalOfBookBtn;
    private TextView totalNumberOfBorrowedBooks;
    private ImageView openTotalOfBorrowedBookBtn;
    private TextView totalNumberOfDelayedBook;
    private ImageView openTotalOfDelayedBook;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        currentView = inflater.inflate(R.layout.fragment_home, container, false);

        userNameLabel              = (TextView) currentView.findViewById(R.id.userNameLabel);
        totalNumberOfBooks         = (TextView) currentView.findViewById(R.id.totalNumberOfBooks);
        openTotalOfBookBtn         = (ImageView) currentView.findViewById(R.id.openTotalOfBookBtn);
        totalNumberOfBorrowedBooks = (TextView) currentView.findViewById(R.id.totalNumberOfBorrowedBooks);
        openTotalOfBorrowedBookBtn = (ImageView) currentView.findViewById(R.id.openTotalOfBorrowedBookBtn);
        totalNumberOfDelayedBook   = (TextView) currentView.findViewById(R.id.totalNumberOfDelayedBook);
        openTotalOfDelayedBook     = (ImageView) currentView.findViewById(R.id.openTotalOfDelayedBook);
        bookObj = new Book(getContext());
        fillBookStatistics();

        ImageView profileButton = (ImageView) currentView.findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent saveBookIntent = new Intent(getActivity().getApplicationContext(), ProfileView.class);
                startActivityForResult(saveBookIntent, 1);
            }
        });

        return currentView;
    }

    public void fillBookStatistics(){
        userNameLabel.setText(bookObj.getUserName());
        List<String> result = bookObj.gatherStatistics();
        totalNumberOfBooks.setText(result.get(0));
        totalNumberOfBorrowedBooks.setText(result.get(1));
        totalNumberOfDelayedBook.setText(result.get(2));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1:
                if(resultCode == getActivity().RESULT_OK){
                    fillBookStatistics();
                }
                break;
        }
    };
}
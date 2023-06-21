package com.br.guardapaginas.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.br.guardapaginas.HelpPage;
import com.br.guardapaginas.MainActivity;
import com.br.guardapaginas.R;
import com.br.guardapaginas.classes.User;
import com.br.guardapaginas.classes.holders.UserAdapter;
import com.br.guardapaginas.classes.holders.UserRecycleViewInterface;
import com.br.guardapaginas.helpers.Functions;
import com.br.guardapaginas.SaveUserView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReaderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReaderFragment extends Fragment implements UserRecycleViewInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View currentView;
    private ListView listOfReaders;
    private EditText searchInput;
    private Spinner statusSpinner;
    private RecyclerView recyclerView;
    private TextView noResultLabel;
    private List<User> listOfReadersObjects;
    Boolean filterOptionsOpen = false;

    public ReaderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReaderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReaderFragment newInstance(String param1, String param2) {
        ReaderFragment fragment = new ReaderFragment();
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
        currentView         = inflater.inflate(R.layout.fragment_reader, container, false);

        getActivity().setTitle("Leitores");

        searchInput         = currentView.findViewById(R.id.toDate);
        recyclerView        = currentView.findViewById(R.id.listOfBookBorrowings);
        noResultLabel       = currentView.findViewById(R.id.noResultLabel);

        String[] statusOption = {"Ativo", "inativo"};
        statusSpinner         = currentView.findViewById(R.id.statusSpinner);
        ArrayAdapter<String> genderSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, statusOption);
        statusSpinner.setAdapter(genderSpinnerAdapter);


        fetchReaders(true, false);

        ImageView searchBtn = currentView.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchReaders(true, true);
            }
        });

        ImageView refreshList = currentView.findViewById(R.id.refreshList);
        refreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchReaders(true, false);
                searchInput.setText("");
            }
        });

        ImageView goBackBtn = (ImageView) currentView.findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BottomNavigationView bottomMenu = MainActivity.bottomNavBar;
                if(bottomMenu == null)
                    return;
                bottomMenu.findViewById(R.id.homeTab).performClick();
            }
        });

        ImageView saveBookBtn = (ImageView) currentView.findViewById(R.id.addNewBorrowingBtn);
        saveBookBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent saveReaderIntent = new Intent(getActivity().getApplicationContext(), SaveUserView.class);
                saveReaderIntent.putExtra("USER_ID", "0");
                saveReaderIntent.putExtra("USER_TYPE", "READER");
                startActivityForResult(saveReaderIntent, 1);
            }
        });

        ImageView helpBtn = (ImageView) currentView.findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent helpIntent = new Intent(getContext(), HelpPage.class);
                startActivity(helpIntent);
            }
        });

        LinearLayout filterDropdownOptions = (LinearLayout) currentView.findViewById(R.id.filterDropdownOptions);
        filterDropdownOptions.setVisibility(View.GONE);
        ImageView filterDropdown = (ImageView) currentView.findViewById(R.id.showFilterOptions);
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

        return currentView;
    }

    public void fetchReaders(Boolean filterByStatus, Boolean filterByName){
        String status = null;
        if(filterByStatus) {
            status  = statusSpinner.getSelectedItem().toString();
            status = (status == "Ativo" ? User.ACTIVE_READER.toString() : User.INACTIVE_READER.toString());
        }
        String name = null;
        if(filterByName){
            name = searchInput.getText().toString();
            name = (name.equals("") ? null : name);
        }
        User obj = new User(getContext());
        listOfReadersObjects = obj.fetchAll(status, name);
        if(listOfReadersObjects.size() > 0) {
            noResultLabel.setVisibility(View.GONE);
        }else{
            noResultLabel.setVisibility(View.VISIBLE);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new UserAdapter(getContext(), listOfReadersObjects, this));
    }

    @Override
    public void onItemClick(int position) {
        Intent saveReaderIntent = new Intent(getActivity().getApplicationContext(), SaveUserView.class);
        saveReaderIntent.putExtra("USER_ID", Functions.parseToString(listOfReadersObjects.get(position).getId()));
        saveReaderIntent.putExtra("USER_TYPE", "READER");
        startActivityForResult(saveReaderIntent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1:
                if(resultCode == getActivity().RESULT_OK){
                    fetchReaders(true, false);
                    searchInput.setText("");
                }
                break;
        }
    };
}
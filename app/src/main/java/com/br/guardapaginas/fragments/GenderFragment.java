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

import com.br.guardapaginas.MainActivity;
import com.br.guardapaginas.R;
import com.br.guardapaginas.SaveGenderView;
import com.br.guardapaginas.classes.Gender;
import com.br.guardapaginas.classes.holders.GenderAdapter;
import com.br.guardapaginas.classes.holders.GenderRecycleViewInterface;
import com.br.guardapaginas.helpers.Functions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GenderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenderFragment extends Fragment implements GenderRecycleViewInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View currentView;
    private ListView listOfGenders;
    private EditText searchInput;
    private Spinner statusSpinner;
    private RecyclerView recyclerView;
    private TextView noResultLabel;
    private List<Gender> listOfGenderObjects;
    Boolean filterOptionsOpen = false;

    public GenderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GenderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GenderFragment newInstance(String param1, String param2) {
        GenderFragment fragment = new GenderFragment();
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
        currentView = inflater.inflate(R.layout.fragment_gender, container, false);

        getActivity().setTitle("Gêneros");

        searchInput         = currentView.findViewById(R.id.searchInput);
        recyclerView        = currentView.findViewById(R.id.listOfBookBorrowings);
        noResultLabel       = currentView.findViewById(R.id.noResultLabel);

        String[] statusOption = {"Ativo", "inativo"};
        statusSpinner         = currentView.findViewById(R.id.statusSpinner);
        ArrayAdapter<String> genderSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, statusOption);
        statusSpinner.setAdapter(genderSpinnerAdapter);

        fetchGenders(true, false);

        ImageView searchBtn = currentView.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchGenders(true, true);
            }
        });

        ImageView refreshList = currentView.findViewById(R.id.refreshList);
        refreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchGenders(true, false);
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
                Intent saveGenderIntent = new Intent(getActivity().getApplicationContext(), SaveGenderView.class);
                saveGenderIntent.putExtra("GENDER_ID", "0");
                startActivityForResult(saveGenderIntent, 1);
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

    public void fetchGenders(Boolean filterByStatus, Boolean filterByName){
        Gender obj = new Gender(getContext());
        String status = null;
        if(filterByStatus) {
            status  = statusSpinner.getSelectedItem().toString();
            status = (status == "Ativo" ? obj.ACTIVE : obj.INACTIVE);
        }
        String name = null;
        if(filterByName){
            name = searchInput.getText().toString();
            name = (name.equals("") ? null : name);
        }
        listOfGenderObjects = obj.fetchAll(status, name);
        if(listOfGenderObjects.size() > 0) {
            noResultLabel.setVisibility(View.GONE);
        }else{
            noResultLabel.setVisibility(View.VISIBLE);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new GenderAdapter(getContext(), listOfGenderObjects, this));
    }

    @Override
    public void onItemClick(int position) {
        Intent saveGenderIntent = new Intent(getActivity().getApplicationContext(), SaveGenderView.class);
        saveGenderIntent.putExtra("GENDER_ID", Functions.parseToString(listOfGenderObjects.get(position).getId()));
        startActivityForResult(saveGenderIntent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1:
                if(resultCode == getActivity().RESULT_OK){
                    fetchGenders(true, false);
                    searchInput.setText("");
                }
                break;
        }
    };
}
package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.br.guardapaginas.classes.User;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.fragments.BookFragment;
import com.br.guardapaginas.fragments.HomeFragment;
import com.br.guardapaginas.fragments.ReaderFragment;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "nickolas";
    Button btn;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*Gender genderObj = new Gender();
        genderObj.setName("romance");
        genderObj.setCreatedAt("2022-0307");
        genderObj.saveGender(genderObj);*/

        //DBHandler connectionObj = new DBHandler(MainActivity.this);
        //Gender genderObj = new Gender(this);
        //System.out.println("Oi");
        //System.out.println(genderObj.getDBConnection());
        /*
        SQLiteDatabase db = connectionObj.getConnection();

        String query   = "SELECT * FROM gender";
        Cursor results = db.rawQuery(query, null);
        // Index of values
        int indexName = results.getColumnIndex("name");
        int indexDate = results.getColumnIndex("date");
        results.moveToFirst();
        String[] data = null;
        int position = 0;
        while(results.equals(null)){
            String name = results.getString(indexName);
            String date = results.getString(indexDate);
            data[position] = name + date;
            position++;
            results.moveToNext();
        }
        System.out.println(data);
        */

//        Intent intent = new Intent(this, HomePage.class);
//        startActivity(intent);

        //performLogin("nickolas@hotmail.com", "12345");
    User obj = new User(getApplicationContext());
    obj.setName("Nickolas Alvaro Bini");
    obj.setEmail("nickolasbini@hotmail.com");
    obj.setPassword("12345");
    Boolean re = obj.saveUser(obj);
    System.out.println(obj.getResults());
        System.out.println(obj.getResults().getCount());
        System.out.println(obj.getResults().getColumnCount());
    System.out.println("Seraaa: "+re);

        replaceFragment(new HomeFragment());
        binding.bottomNavigationView2.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.homeTab:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.bookTab:
                    replaceFragment(new BookFragment());
                    break;
                case R.id.readerTab:
                    replaceFragment(new ReaderFragment());
                    break;
            }

            return true;
        });
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void goToHomePage(){
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(EXTRA_MESSAGE, "user name");
        startActivity(intent);
    }

    private void whatsAPP(){
        Intent view = new Intent(Intent.ACTION_VIEW);
        view.setData(Uri.parse("https://api.whatsapp.com/send?text=Olá&phone=5541984320432"));
        startActivity(view);
    }
    private void openPhoneContact(){
        Intent view = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:991019001"));
        startActivity(view);
    }

    private void openWebSite(String url){
        Intent view = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(view);
    }

    public Boolean performLogin(String email, String pass){
        User userObj = new User(getApplicationContext());

        String v = userObj.hashMake("12345");
        System.out.println("Nickolas");
        System.out.println(userObj.hashCheck(v, "12345"));

        return true;
    }
}
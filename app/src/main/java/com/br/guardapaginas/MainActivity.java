package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.br.guardapaginas.classes.Book;
import com.br.guardapaginas.classes.DBHandler;
import com.br.guardapaginas.classes.Gender;
import com.br.guardapaginas.classes.User;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.fragments.BookFragment;
import com.br.guardapaginas.fragments.HomeFragment;
import com.br.guardapaginas.fragments.ReaderFragment;
import com.br.guardapaginas.helpers.Functions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "nickolas";
    Button btn;
    ActivityMainBinding binding;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        User u = new User(getApplicationContext());
//        ArrayList<User> p = u.fetchAll();
//        System.out.println("Total:  "+p);
//        Integer po = 0;
//        for(User i : p){
//            System.out.println("Id:  "+i.getId()+"  |   Nome: "+i.getName() + "   Email: "+i.getEmail()+"   Senha: "+i.getPassword());
//            if(po.equals(0)){
//                i.setEmail("nickolasbini@hotmail.com");
//                i.setPassword("12345");
//                System.out.println("MEEE");
//            }else{
//                System.out.println("DEEEE");
//                i.setEmail("email" + po.toString() + "@hotmail.com");
//                i.setPassword("1");
//            }
//            System.out.println("Nova senha: "+i.getPassword()+ "  Mail: "+i.getEmail());
//            System.out.println(i.saveUser(i));
//            po++;
//        }
//
//        performLogin("nickolasbini@hotmail.com", "12345");

        Gender genderObj = new Gender(getApplicationContext());
//        genderObj.recyclyBD();
        String[] defaultGender = {"drama", "romance", "terror", "misterio", "aventura", "ação"};
        for(Integer i = 0; i < defaultGender.length; i++){
            Gender obj = new Gender(getApplicationContext());
            List<Gender> result = obj.fetchByName(defaultGender[i]);
            if(result.size() != 0)
                continue;
            obj.setName(defaultGender[i]);
            obj.save(obj);
        }


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
        User userObj            = new User(getApplicationContext());
        ArrayList<User> results = userObj.findBy("email", email, false);
        if(results.size() < 1)
            return false;
        User object = results.get(0);
        System.out.println(Functions.hashCheck(object.getPassword(), pass));
        if(!Functions.hashCheck(object.getPassword(), pass))
            return false;
        System.out.println("will logg in");
        return true;
    }
}
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
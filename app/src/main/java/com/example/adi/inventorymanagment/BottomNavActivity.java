package com.example.adi.inventorymanagment;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class BottomNavActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        BottomNavigationView botNav = findViewById(R.id.bottomNav);
        botNav.setOnNavigationItemSelectedListener(navListener);

        TraziFragment traziFragment = new TraziFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                traziFragment).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment odabraniFragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.navTrazi:
                            odabraniFragment = new TraziFragment();
                            break;
                        case R.id.navLista:
                            odabraniFragment = new ListaFragment();
                            break;
                        case R.id.navDodaj:
                            odabraniFragment = new DodajFragment();
                            break;
                    }
                    if (odabraniFragment != null){
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainer, odabraniFragment).commit();
                    }

                    return true;
                }
            };
}

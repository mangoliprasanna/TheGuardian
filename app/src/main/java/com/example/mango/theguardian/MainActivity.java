package com.example.mango.theguardian;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.mango.theguardian.fragments.MainScreenFragment;
import com.example.mango.theguardian.fragments.SectionFragment;

public class MainActivity extends AppCompatActivity {

    final MainScreenFragment mainScreen = new MainScreenFragment();
    final SectionFragment sectionFragment = new SectionFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();

    Fragment mFragment = null;

    // Bottom Navigation Click Handeling.
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(mFragment != mainScreen)
                        fragmentManager.beginTransaction().show(mainScreen).hide(mFragment).commit();
                    mFragment = mainScreen;
                    return true;
                case R.id.navigation_section:
                    if(mFragment != sectionFragment)
                        fragmentManager.beginTransaction().show(sectionFragment).hide(mFragment).commit();
                    mFragment = sectionFragment;
                    return true;
                case R.id.navigation_search:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Creating FragmentManager to handel the visibility of Fragments.
        fragmentManager.beginTransaction().add(R.id.frameLayout, sectionFragment, "BLANK_KSCREEN").hide(sectionFragment).commit();
        fragmentManager.beginTransaction().add(R.id.frameLayout, mainScreen, "MAIN_SCREEN").commit();

        // MainScreen as Current Fragment.
        mFragment = mainScreen;

        // Setting up bottomNavigation.
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}

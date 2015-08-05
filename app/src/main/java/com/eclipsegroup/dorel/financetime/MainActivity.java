package com.eclipsegroup.dorel.financetime;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.SearchView;

import com.eclipsegroup.dorel.financetime.database.Database;
import com.eclipsegroup.dorel.financetime.database.DatabaseHelper;
import com.eclipsegroup.dorel.financetime.main.fragments.InfoFragment;
import com.eclipsegroup.dorel.financetime.main.fragments.MainPageFragment;
import com.eclipsegroup.dorel.financetime.main.fragments.PortfolioFragment;
import com.eclipsegroup.dorel.financetime.main.fragments.SettingsFragment;
import com.eclipsegroup.dorel.financetime.models.IndicesData;
import com.eclipsegroup.dorel.financetime.tabs.SlidingTabLayout;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationDrawerFragment drawerFragment;
    private FragmentTransaction transaction;
    private MainPageFragment mainPageFragment;
    private PortfolioFragment portfolioFragment;
    private IndicesData indicesData;
    private DatabaseHelper dbHelper;
    private Database db;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        db = new Database(dbHelper);

        toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.main);
        searchView = (SearchView) findViewById(R.id.search_space);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("SEARCH_STRING", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mainPageFragment = new MainPageFragment();
        portfolioFragment = new PortfolioFragment();

        drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, mainPageFragment).commit();

    }

    @Override
    protected void onResume(){
        super.onResume();
        searchView.setIconified(true);
    }

    public void onClickMain(View view){
        toolbar.setTitle(R.string.main);

        transactionTo(mainPageFragment);
        drawerFragment.setAsHamNavigationIcon();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        searchView.setVisibility(View.VISIBLE);
        searchView.setIconified(true);
        drawerFragment.closeDrawer();
    }

    public void onClickFavorites(View view){

        toolbar.setTitle(R.string.favorites);

        transactionTo(portfolioFragment);
        drawerFragment.setAsHamNavigationIcon();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        searchView.setVisibility(View.VISIBLE);
        searchView.setIconified(true);
        drawerFragment.closeDrawer();
    }

    public void onClickSettings(View view){

        toolbar.setTitle(R.string.action_settings);
        SettingsFragment settingsFragment = new SettingsFragment();

       /* Bundle args = new Bundle();
        args.putInt(ArticleFragment.ARG_POSITION, position);
        newFragment.setArguments(args); */

        transactionTo(settingsFragment);
        drawerFragment.setAsUpNavigationIcon();
        setNavigationButtonToBackMainPage();
        searchView.setVisibility(View.INVISIBLE);
        drawerFragment.closeDrawer();
    }

    public void onClickAbout(View view){

        toolbar.setTitle(R.string.about);
        InfoFragment infoFragment = new InfoFragment();

       /* Bundle args = new Bundle();
        args.putInt(ArticleFragment.ARG_POSITION, position);
        newFragment.setArguments(args); */

        transactionTo(infoFragment);
        drawerFragment.setAsUpNavigationIcon();
        setNavigationButtonToBackMainPage();

        searchView.setVisibility(View.INVISIBLE);
        drawerFragment.closeDrawer();
    }

    private void setNavigationButtonToBackMainPage(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                toolbar.setTitle(R.string.main);
                transactionTo(mainPageFragment);
                drawerFragment.setAsHamNavigationIcon();
                searchView.setVisibility(View.VISIBLE);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawerLayout.openDrawer(Gravity.LEFT);
                    }
                });
            }
        });

    }

    private void transactionTo(Fragment fragment){

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

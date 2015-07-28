package com.eclipsegroup.dorel.financetime;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;

import com.eclipsegroup.dorel.financetime.main.fragments.InfoFragment;
import com.eclipsegroup.dorel.financetime.main.fragments.MainPageFragment;
import com.eclipsegroup.dorel.financetime.main.fragments.PortfolioFragment;
import com.eclipsegroup.dorel.financetime.main.fragments.SettingsFragment;
import com.eclipsegroup.dorel.financetime.tabs.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager mPager;
    private SlidingTabLayout mTabs;
    private NavigationDrawerFragment drawerFragment;
    private MainPageFragment mainPageFragment;
    private PortfolioFragment portfolioFragment;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.main);


        mainPageFragment = new MainPageFragment();
        portfolioFragment = new PortfolioFragment();

        drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, mainPageFragment).commit();

    }

    public void onClickMain(View view){
        toolbar.setTitle(R.string.main);

       /* Bundle args = new Bundle();
        args.putInt(ArticleFragment.ARG_POSITION, position);
        newFragment.setArguments(args); */
        transactionTo(mainPageFragment);
        drawerFragment.setAsHamNavigationIcon();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        drawerFragment.closeDrawer();
    }

    public void onClickFavorites(View view){

        toolbar.setTitle(R.string.favorites);
        transactionTo(portfolioFragment);
        drawerFragment.setAsHamNavigationIcon();
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

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
        drawerFragment.closeDrawer();
    }

    private void setNavigationButtonToBackMainPage(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

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

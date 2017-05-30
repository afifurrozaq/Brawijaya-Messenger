package com.yokoding.afifur.brawijayamessenger.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.yokoding.afifur.brawijayamessenger.BottomNavigationViewHelper;
import com.yokoding.afifur.brawijayamessenger.R;
import com.yokoding.afifur.brawijayamessenger.ui.fragment.ChatFragment;
import com.yokoding.afifur.brawijayamessenger.ui.fragment.ContactFragment;
import com.yokoding.afifur.brawijayamessenger.ui.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);

        this.setUiFragment();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigationView.setNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.disableShiftMode(navigation);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new ContactFragment();
                    break;
                case R.id.navigation_dashboard:
                    fragment = new ChatFragment();
                    break;
                case R.id.navigation_notifications:
                    fragment = new ProfileFragment();
                    break;

            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame,fragment)
                    .commit();
            return true;
        }

    };


    private void setUiFragment(){
        Intent intent = getIntent();
        Fragment fragment = null;
        if(intent.hasExtra("fragment")){
            String status_fragment = intent.getExtras().getString("fragment");
            if (status_fragment.equals("profile")){
                fragment = new ProfileFragment();
                bottomNavigation.setSelectedItemId(R.id.navigation_notifications);

            }
        }
        else{
            fragment = new ContactFragment();
            bottomNavigation.setSelectedItemId(R.id.navigation_home);
        }


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame,fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}

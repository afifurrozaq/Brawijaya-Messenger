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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yokoding.afifur.brawijayamessenger.BottomNavigationViewHelper;
import com.yokoding.afifur.brawijayamessenger.R;
import com.yokoding.afifur.brawijayamessenger.adapter.UsersChatAdapter;
import com.yokoding.afifur.brawijayamessenger.model.User;
import com.yokoding.afifur.brawijayamessenger.ui.fragment.ChatFragment;
import com.yokoding.afifur.brawijayamessenger.ui.fragment.ContactFragment;
import com.yokoding.afifur.brawijayamessenger.ui.fragment.NamaFragment;
import com.yokoding.afifur.brawijayamessenger.ui.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigation;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRefDatabase;
    private FirebaseUser user;
    private TextView email;
    private ImageView img;
    private TextView nama;

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
        View header=navigationView.getHeaderView(0);
        email = (TextView) header.findViewById(R.id.email_nav);
        nama = (TextView) header.findViewById(R.id.name_nav);
        img = (ImageView) header.findViewById(R.id.imageView_nav);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);

        this.setUiFragment();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigationView.setNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        setAuthInstance() ;
        setUsersDatabase();
        selectprofile();
    }
    private void setAuthInstance() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void selectprofile() {

        mUserRefDatabase = FirebaseDatabase.getInstance().getReference();
        mUserRefDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    User note = noteDataSnapshot.getValue(User.class);
                    user =  FirebaseAuth.getInstance().getCurrentUser();
                    if(note.getId().equals(user.getUid())) {
                        email.setText(note.getEmail());
                        Picasso.with(MainActivity.this)
                                .load(note.getImage())
                                .into(img);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    private void setUsersDatabase() {
        mUserRefDatabase = FirebaseDatabase.getInstance().getReference().child("users");
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
                case R.id.navigation_reminder:
                    fragment = new NamaFragment();
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
    private void logout() {
        mAuth.signOut();
    }

    private void setUserOffline() {
        if(mAuth.getCurrentUser()!=null ) {
            String userId = mAuth.getCurrentUser().getUid();
            mUserRefDatabase.child(userId).child("connection").setValue(UsersChatAdapter.OFFLINE);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            logout();
        } else if (id == R.id.nav_manage) {
            Intent b = new Intent(getApplicationContext(), EditActivity.class);
            startActivity(b);
        } else if (id == R.id.nav_logout) {
        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}

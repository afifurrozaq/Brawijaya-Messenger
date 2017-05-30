package com.yokoding.afifur.brawijayamessenger.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yokoding.afifur.brawijayamessenger.R;
import com.yokoding.afifur.brawijayamessenger.model.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    TextView email, nama;
    private DatabaseReference mDatabase;


   @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View a =  inflater.inflate(R.layout.fragment_profile, container, false);
       email = (TextView)a.findViewById(R.id.email);
       nama = (TextView)a.findViewById(R.id.nama);
       selectprofile();
       return a;
    }

    public void selectprofile() {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    User note = noteDataSnapshot.getValue(User.class);
                    nama.setText(note.getDisplayName());
                    email.setText(note.getEmail());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}

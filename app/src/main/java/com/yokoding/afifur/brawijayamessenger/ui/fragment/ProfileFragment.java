package com.yokoding.afifur.brawijayamessenger.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.yokoding.afifur.brawijayamessenger.R;
import com.yokoding.afifur.brawijayamessenger.model.User;
import com.yokoding.afifur.brawijayamessenger.ui.Activity.EditActivity;

import java.net.URL;


public class ProfileFragment extends Fragment {
    private static final int REQUEST_CODE = 1234;
    TextView email, nama;
    Button edit;
    ImageView img;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private TextView lokasi;
    private TextView address;
    private TextView job;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View a =  inflater.inflate(R.layout.fragment_profile, container, false);
       email = (TextView)a.findViewById(R.id.email);
       nama = (TextView)a.findViewById(R.id.nama);
       lokasi = (TextView)a.findViewById(R.id.lokasi);
        address = (TextView)a.findViewById(R.id.address);
        job = (TextView)a.findViewById(R.id.job);
       img = (ImageView) a.findViewById(R.id.banar1) ;
       nama.setText("");
       email.setText("");

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
                    user =  FirebaseAuth.getInstance().getCurrentUser();
                    if(note.getId().equals(user.getUid())) {
                        nama.setText(note.getDisplayName());
                        email.setText(note.getEmail());
                        Picasso.with(getActivity())
                                .load(note.getImage())
                                .into(img);
                        lokasi.setText(note.getLokasi());
                        address.setText(note.getAddress());
                        job.setText(note.getJob());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }


}

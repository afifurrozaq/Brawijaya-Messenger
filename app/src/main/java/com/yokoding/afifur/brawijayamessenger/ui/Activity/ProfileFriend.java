package com.yokoding.afifur.brawijayamessenger.ui.Activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yokoding.afifur.brawijayamessenger.Helper.ExtraIntent;
import com.yokoding.afifur.brawijayamessenger.R;
import com.yokoding.afifur.brawijayamessenger.adapter.MessageChatAdapter;
import com.yokoding.afifur.brawijayamessenger.model.User;



public class ProfileFriend extends AppCompatActivity {

    private EditText job,nomor,email,nama,lokasi,address;
    private ImageView img;
    private String recipientid;

    private MessageChatAdapter messageChatAdapter;
    private ChildEventListener messageChatListener;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        String app = getIntent().getStringExtra(ExtraIntent.EXTRA_DISPLAY);
//        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#012154")));
//        actionBar.setTitle("Profile "+app);
        setContentView(R.layout.activity_profile_friend);
        job = (EditText) findViewById(R.id.job);
        img = (ImageView) findViewById(R.id.banar1);
        nomor = (EditText) findViewById(R.id.nomor);
        email = (EditText) findViewById(R.id.email);
        nama = (EditText) findViewById(R.id.nama);
        lokasi = (EditText) findViewById(R.id.lokasi);
        address = (EditText) findViewById(R.id.address);
//        Toast.makeText(this, getIntent().getStringExtra(ExtraIntent.EXTRA_RECIPIENT_ID), Toast.LENGTH_SHORT).show();
//        selectprofile();
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
                        Picasso.with(ProfileFriend.this)
                                .load(note.getImage())
                                .into(img);
                        job.setText(note.getJob());
                        address.setText(note.getAddress());
                        lokasi.setText(note.getLokasi());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
    @Override
    protected void onStop() {
        super.onStop();

        if(messageChatListener != null) {
            mDatabase.removeEventListener(messageChatListener);
        }
        messageChatAdapter.cleanUp();

    }

}

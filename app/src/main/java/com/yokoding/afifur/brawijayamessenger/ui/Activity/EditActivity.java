package com.yokoding.afifur.brawijayamessenger.ui.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yokoding.afifur.brawijayamessenger.Helper.ExtraIntent;
import com.yokoding.afifur.brawijayamessenger.R;
import com.yokoding.afifur.brawijayamessenger.model.User;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import static com.yokoding.afifur.brawijayamessenger.R.id.add;
import static com.yokoding.afifur.brawijayamessenger.R.id.email;
import static com.yokoding.afifur.brawijayamessenger.R.id.nama;

public class EditActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private ImageView imageView;
    private Uri imgUri;


    private DatabaseReference mDatabase;
    private FirebaseUser user;

    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "image";
    public static final int REQUEST_CODE = 1234;

    EditText email, nama, nomor, job, lokasi, address;
    Button save, upload;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#012154")));
        actionBar.setTitle("Edit Profile");
        setContentView(R.layout.activity_edit);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        job = (EditText) findViewById(R.id.job);
        img = (ImageView) findViewById(R.id.banar1);
        nomor = (EditText) findViewById(R.id.nomor);
        email = (EditText) findViewById(R.id.email);
        nama = (EditText) findViewById(R.id.nama);
        lokasi = (EditText) findViewById(R.id.lokasi);
        address = (EditText) findViewById(R.id.address);
        save = (Button) findViewById(R.id.save);
        upload = (Button) findViewById(R.id.upload);
        imageView = (ImageView) findViewById(R.id.banar1);
        selectprofile();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editprofile();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unduh();
            }
        });

    }

    public void unduh() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                imageView.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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
                        Picasso.with(EditActivity.this)
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

    public void editprofile() {
        if (imgUri != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading image");
            dialog.show();

            //Get the storage reference
            StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUri));

            //Add file to reference

            ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    //Dimiss dialog when success
                    dialog.dismiss();
                    //Display success toast msg
                    Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    User baru = new User(nama.getText().toString(),
                            email.getText().toString(), "Online", 2,
                            new Date().getTime(),
                            user.getUid(),
                            nomor.getText().toString(), job.getText().toString(), lokasi.getText().toString(),
                            address.getText().toString(),downloadUrl.toString());

                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    mDatabase.child("users").child(user.getUid()).setValue(baru);
                    //Save image info in to firebase database

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            //Dimiss dialog when error
                            dialog.dismiss();
                            //Display err toast msg
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") long ani = taskSnapshot.getBytesTransferred();
                            @SuppressWarnings("VisibleForTests") long budi = taskSnapshot.getTotalByteCount();

                            double progress = (100 * ani) / budi;
                            dialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Please select image", Toast.LENGTH_SHORT).show();
        }



    }


}

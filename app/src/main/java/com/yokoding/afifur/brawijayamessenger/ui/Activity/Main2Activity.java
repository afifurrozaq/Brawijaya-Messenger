package com.yokoding.afifur.brawijayamessenger.ui.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
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
import com.yokoding.afifur.brawijayamessenger.model.ChatMessage;
import com.yokoding.afifur.brawijayamessenger.model.User;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1234;
    private Uri imgUri;
    private Bitmap bm;
    public static final String FB_STORAGE_PATH = "image/";
    private StorageReference mStorageRef;
    private DatabaseReference messageChatDatabase;
    private ChatMessage chatM;
    private String chatRef;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent inten = this.getIntent();
        Bundle bundle = inten.getExtras();
        chatM= (ChatMessage) bundle.getSerializable("chat");
        Toast.makeText(this, ""+chatM.getRecipient(), Toast.LENGTH_SHORT).show();
        chatRef = getIntent().getStringExtra("referance");
        messageChatDatabase = FirebaseDatabase.getInstance().getReference().child(chatRef);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE);

    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();

            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                Toast.makeText(getApplicationContext(), bm +" ", Toast.LENGTH_LONG).show();
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

                        chatM.setMessage(downloadUrl.toString());
                        messageChatDatabase.push().setValue(chatM);



                        final Intent chatIntent = new Intent(Main2Activity.this, ChatActivity.class);
                        chatIntent.putExtra(ExtraIntent.EXTRA_CURRENT_USER_ID, chatM.getSender());
                        chatIntent.putExtra(ExtraIntent.EXTRA_RECIPIENT_ID, chatM.getRecipient());
                        chatIntent.putExtra(ExtraIntent.EXTRA_CHAT_REF, chatRef);
                        // Start new activity

                        startActivity(chatIntent);


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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

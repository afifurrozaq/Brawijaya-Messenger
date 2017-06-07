package com.yokoding.afifur.brawijayamessenger.ui.Activity;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yokoding.afifur.brawijayamessenger.Helper.ExtraIntent;
import com.yokoding.afifur.brawijayamessenger.R;
import com.yokoding.afifur.brawijayamessenger.adapter.MessageChatAdapter;
import com.yokoding.afifur.brawijayamessenger.model.ChatMessage;
import com.yokoding.afifur.brawijayamessenger.model.User;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();

    private RecyclerView mChatRecyclerView;
    private EditText mUserMessageChatText;
    private String mRecipientId;
    private String mCurrentUserId;
    private MessageChatAdapter messageChatAdapter;
    private DatabaseReference messageChatDatabase;
    private ChildEventListener messageChatListener;
    private String mTime;
    private Uri imgUri;
    private  Bitmap bm;


    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "image";
    public static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String nama = getIntent().getStringExtra(ExtraIntent.EXTRA_DISPLAY);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#012154")));
        actionBar.setTitle(nama);
        setContentView(R.layout.activity_chat);
        Toast.makeText(this, nama, Toast.LENGTH_SHORT).show();
        mUserMessageChatText = (EditText) findViewById(R.id.edit_text_message);
        mChatRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_chat);
        Button btnSendMsgListener = (Button) findViewById(R.id.btn_send_message);
        ImageButton attch = (ImageButton) findViewById(R.id.bt_attachment);
        attch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i=new Intent(getApplicationContext(),Main2Activity.class);
                String chatRef = getIntent().getStringExtra(ExtraIntent.EXTRA_CHAT_REF);

                i.putExtra("referance",chatRef);
                ChatMessage newMessage = new ChatMessage("",mCurrentUserId,mRecipientId, System.currentTimeMillis(),mTime,"image" );

                i.putExtra("chat",newMessage);
                startActivity(i);

            }
        });
        btnSendMsgListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String senderMessage = mUserMessageChatText.getText().toString().trim();

                if(!senderMessage.isEmpty()){

                    ChatMessage newMessage = new ChatMessage(senderMessage,mCurrentUserId,mRecipientId, System.currentTimeMillis(),mTime,"text" );
                    messageChatDatabase.push().setValue(newMessage);

                    mUserMessageChatText.setText("");
                }
            }
        });
        setDatabaseInstance();
        setUsersId();
        setChatRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();

            try {
               bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                Toast.makeText(getApplicationContext(), bm +" ", Toast.LENGTH_LONG).show();
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





    private void setDatabaseInstance() {
        String chatRef = getIntent().getStringExtra(ExtraIntent.EXTRA_CHAT_REF);
        messageChatDatabase = FirebaseDatabase.getInstance().getReference().child(chatRef);
    }

    private void setUsersId() {
        mRecipientId = getIntent().getStringExtra(ExtraIntent.EXTRA_RECIPIENT_ID);
        mCurrentUserId = getIntent().getStringExtra(ExtraIntent.EXTRA_CURRENT_USER_ID);
    }

    private void setChatRecyclerView() {
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mChatRecyclerView.setHasFixedSize(true);
        messageChatAdapter = new MessageChatAdapter(new ArrayList<ChatMessage>());
        messageChatAdapter.context=getApplicationContext();
        mChatRecyclerView.setAdapter(messageChatAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        messageChatListener = messageChatDatabase.limitToFirst(20).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {

                if(dataSnapshot.exists()){
                    ChatMessage newMessage = dataSnapshot.getValue(ChatMessage.class);
                    if(newMessage.getSender().equals(mCurrentUserId)){
                        newMessage.setRecipientOrSenderStatus(MessageChatAdapter.SENDER);
                    }else{
                        newMessage.setRecipientOrSenderStatus(MessageChatAdapter.RECIPIENT);
                    }
                    messageChatAdapter.refillAdapter(newMessage);
                    mChatRecyclerView.scrollToPosition(messageChatAdapter.getItemCount()-1);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
            messageChatDatabase.removeEventListener(messageChatListener);
        }
        messageChatAdapter.cleanUp();

    }
}

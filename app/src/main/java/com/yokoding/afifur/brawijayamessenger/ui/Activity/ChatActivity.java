package com.yokoding.afifur.brawijayamessenger.ui.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yokoding.afifur.brawijayamessenger.Helper.ExtraIntent;
import com.yokoding.afifur.brawijayamessenger.R;
import com.yokoding.afifur.brawijayamessenger.adapter.MessageChatAdapter;
import com.yokoding.afifur.brawijayamessenger.model.ChatMessage;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();

    //    @BindView(R.id.recycler_view_chat) RecyclerView mChatRecyclerView;
//    @BindView(R.id.edit_text_message) EditText mUserMessageChatText;
    private RecyclerView mChatRecyclerView;
    private EditText mUserMessageChatText;
    private String mRecipientId;
    private String mCurrentUserId;
    private MessageChatAdapter messageChatAdapter;
    private DatabaseReference messageChatDatabase;
    private ChildEventListener messageChatListener;
    private String mTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mUserMessageChatText = (EditText) findViewById(R.id.edit_text_message);
        mChatRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_chat);
        Button btnSendMsgListener = (Button) findViewById(R.id.btn_send_message);
        btnSendMsgListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String senderMessage = mUserMessageChatText.getText().toString().trim();

                if(!senderMessage.isEmpty()){

                    ChatMessage newMessage = new ChatMessage(senderMessage,mCurrentUserId,mRecipientId, System.currentTimeMillis(),mTime );
                    messageChatDatabase.push().setValue(newMessage);

                    mUserMessageChatText.setText("");
                }
            }
        });
//        bindButterKnife();
        setDatabaseInstance();
        setUsersId();
        setChatRecyclerView();
    }

    //    private void bindButterKnife() {
//        ButterKnife.bind(this);
//    }
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

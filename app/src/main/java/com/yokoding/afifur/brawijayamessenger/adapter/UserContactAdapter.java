package com.yokoding.afifur.brawijayamessenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.yokoding.afifur.brawijayamessenger.Helper.ChatHelper;
import com.yokoding.afifur.brawijayamessenger.Helper.ExtraIntent;
import com.yokoding.afifur.brawijayamessenger.R;
import com.yokoding.afifur.brawijayamessenger.model.ChatMessage;
import com.yokoding.afifur.brawijayamessenger.model.User;
import com.yokoding.afifur.brawijayamessenger.ui.Activity.ChatActivity;
import com.yokoding.afifur.brawijayamessenger.ui.Activity.ProfileFriend;

import java.util.List;

/**
 * Created by afifur on 31/05/17.
 */

public class UserContactAdapter extends RecyclerView.Adapter<UserContactAdapter.ViewHolderUsers> {

    public static final String ONLINE = "Online";
    public static final String OFFLINE = "Offline";
    private List<User> mUsers;
    public Context mContext;
    private String mCurrentUserEmail;
    private Long mCurrentUserCreatedAt;
    private String mCurrentUserId;

    public FragmentActivity fragmentActivity;
    private DatabaseReference mDatabase;

    public UserContactAdapter(Context context, List<User> fireChatUsers) {
        mUsers = fireChatUsers;
        mContext = context;
    }



    @Override
    public UserContactAdapter.ViewHolderUsers onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserContactAdapter.ViewHolderUsers(mContext, LayoutInflater.from(parent.getContext()).inflate(R.layout.user_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(final UserContactAdapter.ViewHolderUsers holder, int position) {


        User fireChatUser = mUsers.get(position);
        int userAvatarId= ChatHelper.getDrawableAvatarId(fireChatUser.getAvatarId());
        Drawable avatarDrawable = ContextCompat.getDrawable(mContext,userAvatarId);
        holder.getUserAvatar().setImageDrawable(avatarDrawable);
        String chatRef = ExtraIntent.EXTRA_CHAT_REF;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(chatRef).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    ChatMessage note = noteDataSnapshot.getValue(ChatMessage.class);
                    holder.getStatusMassage().setText(note.getMessage());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        // Set avatar
        //int userAvatarId= ChatHelper.getDrawableAvatarId(fireChatUser.getAvatarId());

//        Drawable avatarDrawable = ContextCompat.getDrawable(mContext,userAvatarId);
//        holder.getUserAvatar().setImageDrawable(avatarDrawable);

        // Set display name
        holder.getUserDisplayName().setText(fireChatUser.getDisplayName());
        Picasso.with(mContext).load(fireChatUser.getImage()).into(holder.getUserAvatar());
        // Set presence status
        holder.getStatusConnection().setText(fireChatUser.getConnection());

        // Set presence text color
        if(fireChatUser.getConnection().equals(ONLINE)) {
            // Green color
            holder.getStatusConnection().setTextColor(Color.parseColor("#00FF00"));
        }else {
            // Red color
            holder.getStatusConnection().setTextColor(Color.parseColor("#FF0000"));
        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void refill(User users) {
        mUsers.add(users);
        notifyDataSetChanged();
    }

    public void changeUser(int index, User user) {
        mUsers.set(index,user);
        notifyDataSetChanged();
    }

    public void setCurrentUserInfo(String userUid, String email, long createdAt) {
        mCurrentUserId = userUid;
        mCurrentUserEmail = email;
        mCurrentUserCreatedAt = createdAt;
    }

    public void clear() {
        mUsers.clear();
    }


    /* ViewHolder for RecyclerView */
    public class ViewHolderUsers extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mStatusMassage;
        private ImageView mUserAvatar;
        private TextView mUserDisplayName;
        private TextView mStatusConnection;
        private Context mContextViewHolder;

        public ViewHolderUsers(Context context, View itemView) {
            super(itemView);
            mUserAvatar = (ImageView)itemView.findViewById(R.id.img_avatar);
            mUserDisplayName = (TextView)itemView.findViewById(R.id.text_view_display_name);
            mStatusConnection = (TextView)itemView.findViewById(R.id.text_view_connection_status);
            mStatusMassage = (TextView)itemView.findViewById(R.id.tv_last_chat);
            mContextViewHolder = context;

            itemView.setOnClickListener(this);
        }

        public ImageView getUserAvatar() {
            return mUserAvatar;
        }

        public TextView getStatusMassage() {
            return mStatusMassage;
        }
        public TextView getUserDisplayName() {
            return mUserDisplayName;
        }
        public TextView getStatusConnection() {
            return mStatusConnection;
        }


        @Override
        public void onClick(View view) {

            User user = mUsers.get(getLayoutPosition());

            String chatRef = user.createUniqueChatRef(mCurrentUserCreatedAt,mCurrentUserEmail);

            Intent chatIntent = new Intent(mContextViewHolder, ChatActivity.class);
            chatIntent.putExtra(ExtraIntent.EXTRA_CURRENT_USER_ID, mCurrentUserId);
            chatIntent.putExtra(ExtraIntent.EXTRA_RECIPIENT_ID, user.getRecipientId());
            chatIntent.putExtra(ExtraIntent.EXTRA_CHAT_REF, chatRef);
            chatIntent.putExtra(ExtraIntent.EXTRA_DISPLAY,user.getDisplayName());
            chatIntent.putExtra(ExtraIntent.EXTRA_DISPLAY_IMAGE,user.getImage());

            // Start new activity

            mContextViewHolder.startActivity(chatIntent);

        }
    }

}

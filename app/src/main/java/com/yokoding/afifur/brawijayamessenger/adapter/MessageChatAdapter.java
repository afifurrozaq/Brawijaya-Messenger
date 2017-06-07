package com.yokoding.afifur.brawijayamessenger.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yokoding.afifur.brawijayamessenger.R;
import com.yokoding.afifur.brawijayamessenger.model.ChatMessage;

import java.util.List;

/**
 * Created by afifur on 25/05/17.
 */

public class MessageChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> mChatList;
    public static final int SENDER = 0;
    public static final int RECIPIENT = 1;
    public Context context;

    public MessageChatAdapter(List<ChatMessage> listOfFireChats) {
        mChatList = listOfFireChats;
    }

    @Override
    public int getItemViewType(int position) {
        if (mChatList.get(position).getRecipientOrSenderStatus() == SENDER) {
            return SENDER;
        } else {
            return RECIPIENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case SENDER:
                View viewSender = inflater.inflate(R.layout.layout_sender_message, viewGroup, false);
                viewHolder = new ViewHolderSender(viewSender);
                break;
            case RECIPIENT:
                View viewRecipient = inflater.inflate(R.layout.layout_recipient_message, viewGroup, false);
                viewHolder = new ViewHolderRecipient(viewRecipient);
                break;
            default:
                View viewSenderDefault = inflater.inflate(R.layout.layout_sender_message, viewGroup, false);
                viewHolder = new ViewHolderSender(viewSenderDefault);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case SENDER:
                ViewHolderSender viewHolderSender = (ViewHolderSender) viewHolder;
                configureSenderView(viewHolderSender, position);
                break;
            case RECIPIENT:
                ViewHolderRecipient viewHolderRecipient = (ViewHolderRecipient) viewHolder;
                configureRecipientView(viewHolderRecipient, position);
                break;
        }


    }

    private void configureSenderView(ViewHolderSender viewHolderSender, int position) {
        ChatMessage senderFireMessage = mChatList.get(position);
        if (senderFireMessage.getStatus_message().equals("text")){
            viewHolderSender.getmSenderMessageImage().setVisibility(View.GONE);
        }else {
            viewHolderSender.getSenderMessageTextView().setVisibility(View.GONE);
            viewHolderSender.getmSenderMessageImage().setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(senderFireMessage.getMessage())
                    .into(viewHolderSender.getmSenderMessageImage());
        }

        System.out.println(senderFireMessage.getStatus_message()+" "+senderFireMessage.getMessage());
        viewHolderSender.getSenderMessageTextView().setText(senderFireMessage.getMessage());

        viewHolderSender.getSenderMessageTime().setText(senderFireMessage.getFormattedTime());
    }


    private void configureRecipientView(ViewHolderRecipient viewHolderRecipient, int position) {
        ChatMessage recipientFireMessage = mChatList.get(position);
        if (recipientFireMessage.getStatus_message().equals("text")){
            viewHolderRecipient.getmRecipientMessageImage().setVisibility(View.GONE);
        }else {
            viewHolderRecipient.getRecipientMessageTextView().setVisibility(View.GONE);
            viewHolderRecipient.getmRecipientMessageImage().setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(recipientFireMessage.getMessage())
                    .into(viewHolderRecipient.getmRecipientMessageImage());
        }
        viewHolderRecipient.getRecipientMessageTextView().setText(recipientFireMessage.getMessage());
        viewHolderRecipient.getRecipientMessageTime().setText(recipientFireMessage.getFormattedTime());
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }


    public void refillAdapter(ChatMessage newFireChatMessage) {

        /*add new message chat to list*/
        mChatList.add(newFireChatMessage);

        /*refresh view*/
        notifyItemInserted(getItemCount() - 1);
    }


    public void cleanUp() {
        mChatList.clear();
    }


    /*==============ViewHolder===========*/

    /*ViewHolder for Sender*/

    public class ViewHolderSender extends RecyclerView.ViewHolder {

        private TextView mSenderMessageTextView;
        private TextView mSenderMessageTime;
        private ImageView mSenderMessageImage;

        public ViewHolderSender(View itemView) {
            super(itemView);
            mSenderMessageTextView = (TextView) itemView.findViewById(R.id.text_view_sender_message);
            mSenderMessageTime = (TextView) itemView.findViewById(R.id.time);
            mSenderMessageImage = (ImageView) itemView.findViewById(R.id.image_chat);
        }

        public TextView getSenderMessageTextView() {
            return mSenderMessageTextView;
        }

        public TextView getSenderMessageTime() {
            return mSenderMessageTime;
        }

        public ImageView getmSenderMessageImage(){return mSenderMessageImage;}
    }


    /*ViewHolder for Recipient*/
    public class ViewHolderRecipient extends RecyclerView.ViewHolder {

        private ImageView mRecipientMessageImage;
        private TextView mRecipientMessageTextView;
        private TextView mRecipientMessageTime;


        public ViewHolderRecipient(View itemView) {
            super(itemView);
            mRecipientMessageTextView = (TextView) itemView.findViewById(R.id.text_view_recipient_message);
            mRecipientMessageTime = (TextView) itemView.findViewById(R.id.time_r);
            mRecipientMessageImage = (ImageView) itemView.findViewById(R.id.image_chat);

        }

        public TextView getRecipientMessageTextView() {
            return mRecipientMessageTextView;
        }

        public TextView getRecipientMessageTime() {
            return mRecipientMessageTime;
        }
        public ImageView getmRecipientMessageImage() {
            return mRecipientMessageImage;
        }

    }


}
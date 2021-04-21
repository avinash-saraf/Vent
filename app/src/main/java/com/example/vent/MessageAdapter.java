package com.example.vent;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vent.model.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    public MessageAdapter(List<Messages> userMessagesList){
        this.userMessagesList = userMessagesList;
    }

    // Setting up the ViewHolder so we can change the fields of each message/create new messages
    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText, receiverMessageText;
        public ImageView messageSenderPicture, messageReceiverPicture;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);


        }
    }

    // creates the ViewHolder which is one message
    // every ViewHolder has a text message layout and image message layout
    // depending the the message the appropriate layout will be shown and the other one will be hidden
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();


        return  new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        // gets the user that sent the particular message
        String fromUserId = messages.getFrom();
        String fromMessageType = messages.getType();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);

        holder.receiverMessageText.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);


        // every ViewHolder has a text message layout and image message layout
        // depending the the message the appropriate layout will be shown and the other one will be hidden

        //message content is text, the use text message layout
        if(fromMessageType.equals("text")){

            //if the message was sent by currentUser, display messages on the left hand side
            if(fromUserId.equals(messageSenderId)){

                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setTextColor(Color.BLACK);
                //holder.senderMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
                holder.senderMessageText.setText(messages.getMessage());
            }
            //if the message was sent by other user, display messages on the right hand side
            else {
                holder.receiverMessageText.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                holder.receiverMessageText.setTextColor(Color.BLACK);
               // holder.receiverMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
                holder.receiverMessageText.setText(messages.getMessage());
            }

        }
        // using Glide library to display image using url of image stored in FirebaseStorage
        else if(fromMessageType.equals("image")){
            if(fromUserId.equals(messageSenderId)){
                holder.messageSenderPicture.setVisibility(View.VISIBLE);

                Glide.with(holder.itemView.getContext()).load(messages.getMessage()).into(holder.messageSenderPicture);
            } else{
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                Glide.with(holder.itemView.getContext()).load(messages.getMessage()).into(holder.messageReceiverPicture);
            }
        }

    }

    @Override
    public int getItemCount() {
        return  userMessagesList.size();
    }


}

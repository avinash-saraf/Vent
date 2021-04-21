package com.example.vent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private String recieverUserId,senderUserId, currentState;
    private TextView userProfileName, userProfileStatus;
    private Button chatButton, removeContactButton;

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //receiving the users id to display profile
        recieverUserId = getIntent().getExtras().get("visit_user_id").toString();
        senderUserId = mAuth.getCurrentUser().getUid();

        userProfileName = findViewById(R.id.visit_user_name);
        userProfileStatus = findViewById(R.id.visit_profile_status);
        chatButton = findViewById(R.id.chat_button);
        removeContactButton = findViewById(R.id.remove_contact_button);

        RetreiveUserInfo();

        //start chatActivity to chat with the user

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(ProfileActivity.this, ChatActivity.class);

                //sending user id and name to chat activity
                chatIntent.putExtra("visit_user_id", recieverUserId);
                chatIntent.putExtra("visit_user_name", userProfileName.getText());
                startActivity(chatIntent);

            }
        });

        removeContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveContact();
            }
        });
    }

    //retrieve all the user's info (username and status)
    private void RetreiveUserInfo() {
        rootRef.child("Users").child(recieverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String userName = snapshot.child("displayname").getValue().toString();
                    String userStatus = snapshot.child("status").getValue().toString();

                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //removes contact from both the users (removes each other from contact list)
    private void RemoveContact(){
        rootRef.child("Contacts").child(senderUserId).child(recieverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            rootRef.child("Contacts").child(recieverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(ProfileActivity.this, "Contact removed", Toast.LENGTH_SHORT).show();
                                                Intent contactsIntent = new Intent(ProfileActivity.this, ContactsActivity.class);
                                                startActivity(contactsIntent);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

}

package com.example.vent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vent.model.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ContactsActivity extends AppCompatActivity {

    private RecyclerView myContactsList;
    private DatabaseReference contactsRef, userRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        myContactsList = findViewById(R.id.contacts_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        myContactsList.addItemDecoration(itemDecoration);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    //displays all the contacts the user has using FirebaseRecycler
    //display the status of the contacts using small green dot if the contact is online
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder contactsViewHolder, int position, @NonNull Contacts contacts) {

                //retrieves the contact at the given position
                final String userIDs = getRef(position).getKey();

                //retrieves all the contact information
                userRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            if(dataSnapshot.child("userState").hasChild("state")){
                                String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                if(state.equals("online")) {
                                    String saveCurrentTime, saveCurrentDate;

                                    Calendar calendar = Calendar.getInstance();

                                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                                    saveCurrentDate = currentDate.format(calendar.getTime());

                                    SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
                                    saveCurrentTime = currentTime.format(calendar.getTime());

                                    // if the the current date is same as last date seen of the contact
                                    // and the current hour is the same as the last hour seen of the contact
                                    // display the contact as online
                                    if(date.equals(saveCurrentDate) && time.substring(0,2).equals(saveCurrentTime.substring(0,2))){
                                        contactsViewHolder.onlineIcon.setVisibility(View.VISIBLE);
                                    }else{
                                        contactsViewHolder.onlineIcon.setVisibility(View.INVISIBLE);
                                    }

                                } else if(state.equals("offline")){
                                    contactsViewHolder.onlineIcon.setVisibility(View.INVISIBLE);
                                }
                            } else
                                {
                                contactsViewHolder.onlineIcon.setVisibility(View.INVISIBLE);

                            }

                            String userName = dataSnapshot.child("displayname").getValue().toString();
                            String userStatus = dataSnapshot.child("status").getValue().toString();


                            contactsViewHolder.userName.setText(userName);
                            contactsViewHolder.userStatus.setText(userStatus);
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //when contact is clicked, send user to profile of the contact
                contactsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profileIntent = new Intent(ContactsActivity.this, ProfileActivity.class);
                        //sending the contact id
                        profileIntent.putExtra("visit_user_id", userIDs);
                        startActivity(profileIntent);

                    }
                });
            }

            //creates the view holder for each contact
            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                return new ContactsViewHolder(view);
            }
        };

        myContactsList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView userName, userStatus;
        ImageView onlineIcon;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            userStatus = itemView.findViewById(R.id.user_status);
            onlineIcon = itemView.findViewById(R.id.user_online_status);
        }
    }
}

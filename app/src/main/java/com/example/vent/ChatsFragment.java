package com.example.vent;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vent.model.Messages;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private View rootView;
    private FloatingActionButton addContacts;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private RecyclerView mRecyclerView;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_chats, container, false);

        //getting necessary info from database
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        // initializing recycler view for displaying all the users chats
        mRecyclerView = rootView.findViewById(R.id.users_messages);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //puts a divider(black line) between every two 'chats'
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        mRecyclerView.addItemDecoration(itemDecoration);

        // sets up a floating action button to add new users
        // when the button is clicked, dialog box will appear
        // user can choose whether or not to add a new user
        addContacts = rootView.findViewById(R.id.fab_add);
        addContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{
                        "Yes","No"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Do you want to add a new contact: ");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            //user wishes to add new contact
                            //find random user to add in contacts
                           findRandomUser();
                        }
                        if(which == 1){
                            dialog.cancel();
                        }
                    }
                });

               builder.show();
            }
        });

        return rootView;
    }

    //displays all the users that the current user has chatted with
    // userName and last message sent displayed
    @Override
    public void onStart() {
        super.onStart();

        // FirebaseRecycler will retrieve all the current user's messages
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Messages>()
                .setQuery(rootRef.child("Messages").child(currentUserId), Messages.class)
                .build();

        // setting up the recycler
        FirebaseRecyclerAdapter<Messages, ChatsFragment.ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Messages, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder contactsViewHolder, int position, @NonNull Messages messages) {

                //this gets the id of the user of the current position
                //concretely, if the position passed is 1
                // then it will get the id of the user at position 1
                final String userIDs = getRef(position).getKey();

                //retrieving all the details of the user
                rootRef.child("Users").child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            final String userName = dataSnapshot.child("displayname").getValue().toString();

                            // sets the user name of the ViewHolder
                            contactsViewHolder.userName.setText(userName);

                            // if user clicks on the ViewHolder, i.e. wishes to chat with another user
                            // chat activity started and user id and name passed to the chat activity
                            contactsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("visit_user_id", userIDs);
                                    chatIntent.putExtra("visit_user_name", userName);
                                    startActivity(chatIntent);
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                    // gets the last message sent between two users and displays it in the view holder
                    rootRef.child("Messages").child(currentUserId).child(userIDs).orderByKey().limitToLast(1)
                            .addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    if(snapshot.exists()){
                                        Messages message = snapshot.getValue(Messages.class);
                                        String lastMsg = message.getMessage();
                                        String type = message.getType();
                                        if(type.equals("image")){
                                            lastMsg = "image";
                                        }
                                        contactsViewHolder.lastmessage.setText(lastMsg);
                                    }
                                    else {
                                        contactsViewHolder.lastmessage.setText("Start a conversation!");
                                    }
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

            }


            // actually creates the ViewHolder, user sees this as one 'chat' having userName and lastMessageSent
            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                return new ContactsViewHolder(view);
            }
        };

        mRecyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    //Each ViewHolder is basically one 'chat' having userName and lastMessage
    //this allows us to change the userName and lastMessage of a ViewHolder
    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView userName, lastmessage;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            lastmessage = itemView.findViewById(R.id.user_status);
        }
    }

    // find a random user from the entire user base
    // this can be performed much more efficiently through Firebase Cloud Functions for large user bases
    // since userbase for testing is quite small, this function could be stuck in a long loop
    // if we were to loop through again to find user that is not a current user
    // to make sure that this doesn't keep looping to find a user that is not a current user
    // end function and display "random user was same as current user"

    // if the app were to be deployed, then there could be a limit to how many random users could be added per day
    private void findRandomUser() {
        rootRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int usersCount = (int) snapshot.getChildrenCount();
                    Random random = new Random();
                    int rand = random.nextInt(usersCount);
                    Iterator iterator = snapshot.getChildren().iterator();
                    for(int i=0;i<rand;i++){
                        iterator.next();
                    }
                    DataSnapshot childSnapshot = (DataSnapshot) iterator.next();
                    String uid = childSnapshot.child("uid").getValue().toString();
                    if(!uid.equals(currentUserId)){
                        checkIfContact(uid);
                    }else{
                        Toast.makeText(getContext(), "random user was same as current user", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // checks if the randomUserSelected is saved in contacts of the current user
    // to prevent adding user again
    private void checkIfContact(final String userId){

        rootRef.child("Contacts").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    boolean saved = false;
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        if(dataSnapshot.getKey().equals(userId)){
                            saved = true;
                            break;
                        }
                    }
                    if(!saved){
                        addContact(userId);
                    } else{Toast.makeText(getContext(), "Already saved in contacts", Toast.LENGTH_SHORT).show();}
                } else{
                    Toast.makeText(getContext(), "User has no contacts", Toast.LENGTH_SHORT).show();
                    addContact(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // adds randomUser selected as contact for both the users(current user and random user)
    private void addContact(final String receiverUserId){
        final DatabaseReference contactsRef = rootRef.child("Contacts");
        contactsRef.child(currentUserId).child(receiverUserId).child("Contacts")
                .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactsRef.child(receiverUserId).child(currentUserId).child("Contacts")
                                    .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        displayContactAdded(receiverUserId);
                                        //Toast.makeText(rootView.getContext(), "Contact Added: "+ receiverUserId, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
    }


    private void displayContactAdded(final String contactId){
        final DatabaseReference userRef = rootRef.child("Users");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String contactName = snapshot.child("displayname").getValue().toString();
                    Toast.makeText(rootView.getContext(), "Contact Added: " + contactName, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userRef.child(contactId).addListenerForSingleValueEvent(valueEventListener);
        userRef.removeEventListener(valueEventListener);
    }
}

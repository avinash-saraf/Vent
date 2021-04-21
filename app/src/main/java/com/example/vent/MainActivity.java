package com.example.vent;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.example.vent.welcome.LoginActivity;
import com.example.vent.welcome.WelcomeActivity;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.vent.ui.main.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String currentUserId;
    private Toolbar mToolbar;
    private DatabaseReference songsRef;
    private MediaPlayer song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setting up TabLayout using ViewPager
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // retrieving all the required info from database
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        songsRef = FirebaseDatabase.getInstance().getReference().child("Songs");
        currentUserId = mAuth.getCurrentUser().getUid();

        // setting up the toolbar
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Vent");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mToolbar.showOverflowMenu();

    }

    // creating the overflow menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    // setting up the overflow menu options to do their intended task
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.overflow_contacts:
                Intent contactsIntent = new Intent(MainActivity.this, ContactsActivity.class);
                startActivity(contactsIntent);
                return true;
            case R.id.overflow_music_on:
                play();
                return true;
            case R.id.overflow_music_off:
               stop();
                return true;
            case R.id.overflow_logout:
                mAuth.signOut();
                SendUserToWelcomeActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    // plays a non-copyrighted song
    private void play(){
        if(song == null){
            song = MediaPlayer.create(this, R.raw.soothing_music);
            song.start();
        }

    }


    private void stop(){
        stopPlayer();
    }

    //stops the current song being played and de-allocates memory
    private void stopPlayer(){
        if(song!=null){
            song.release();
            song = null;
            Toast.makeText(this, "Music Stopped", Toast.LENGTH_SHORT).show();
        }
    }


    // If user has not created an account, send to LoginActivity
    // Otherwise check user existence in database
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser == null){
            sendUserToLoginActivity();
        } else{
            CheckUserExistenceInDatabase();
        }
    }

    // when the MainActivity is stopped,  set user status to 'offline' and stop the song being played
    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUserStatus("offline");
        }
        stopPlayer();
    }

    // when the MainActivity is closed, set user status to offline and stop the song being played
    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUserStatus("offline");
        }
        stopPlayer();
    }

    // Pre-condition: user has already created an account and details saved to FirebaseAuth
    // retrieve the current user id from FirebaseAuthentication
    // if user has not completed two-factor authentication, send to the SetupActivity for two-factor auth
    // otherwise set user status to online
    private void CheckUserExistenceInDatabase() {
        final String userId = mAuth.getCurrentUser().getUid();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(userId)){
                    SendUserToSetupActivity();
                }else{
                    updateUserStatus("online");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void SendUserToSetupActivity() {
        Intent loginIntent = new Intent(MainActivity.this, SetupActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToWelcomeActivity() {
        Intent loginIntent = new Intent(MainActivity.this, WelcomeActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    // updateUserStatus with the current time and date
    private void updateUserStatus(String state){
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        userRef.child(currentUserId).child("userState")
                .updateChildren(onlineStateMap);

    }

    // feature to support playing random songs from library of songs
    // plays random song url from all the available song urls saved in the database

      /*
    private void playOnline(){
        if(song==null){
            String url = getRandomSong();
            song = new MediaPlayer();
            song.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            try {
                song.setDataSource(url);
                song.prepare();  //might take long! (for buffering, etc)
            } catch (IOException e) {
                e.printStackTrace();
            }
            song.start();
            song.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    play();
                }
            });
        }
    }



    private String getRandomSong() {
        final String[] songUrl= new String[1];
        songsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int songsCount = (int) snapshot.getChildrenCount();
                    Random random= new Random();
                    int i = random.nextInt(songsCount);
                    songsRef.child(""+i).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            songUrl[0] = snapshot.getValue().toString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else{
                    Toast.makeText(MainActivity.this, "No songs", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return songUrl[0];
    }
*/

}
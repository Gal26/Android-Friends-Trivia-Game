package com.example.friendstr_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

public class HomeGameActivity extends AppCompatActivity {
    //ActivityVariable
    private Button btnStartQuiz, btnRankingPage;
    private TextView userNameDisplay, highScoreDisplay;
    GameSounds gameSounds;
    //LocalVariable
    private ArrayList<Questions> questionList;
    private ArrayList<User> usersList;
    private int userHighScore = 0;
    //FireBaseVariable
    private FirebaseDatabase database;
    private DatabaseReference QuestionsReference, currentUserReference, UsersReference;
    private FirebaseUser myUser;
    //AuthenticationVariable
    private FirebaseAuth myAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_game);
        //EditText & Buttons
        btnStartQuiz = (Button) findViewById(R.id.start_quiz_btn);
        userNameDisplay = (TextView) findViewById(R.id.user_name_display);
        highScoreDisplay = (TextView) findViewById(R.id.high_score_display);
        btnRankingPage = (Button) findViewById(R.id.ranking_page_btn);
        //Authentication
        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();
        //DataBase
        database = FirebaseDatabase.getInstance();
        String uid = myUser.getUid();
        currentUserReference = database.getReference().child("Users").child(uid);
        currentUserReference.keepSynced(true);

        QuestionsReference = database.getReference().child("Questions");
        QuestionsReference.keepSynced(true);
        questionList = new ArrayList<>();

        UsersReference = database.getReference().child("Users");
        UsersReference.keepSynced(true);
        usersList = new ArrayList<>();


        btnStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameSounds = new GameSounds(HomeGameActivity.this, "Default Sound");

                Intent intent = new Intent(HomeGameActivity.this, QuestionsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("myQuestionList", (ArrayList<? extends Parcelable>) questionList);
                intent.putExtra("userHighScore", userHighScore);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btnRankingPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeGameActivity.this, HighScoreActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("myUsersList", (ArrayList<? extends Parcelable>) usersList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (usersList.size() != 0)
            usersList.clear();

        currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    userHighScore = user.getHighScore();
                    userNameDisplay.setText(user.getName() + "");
                    highScoreDisplay.setText(userHighScore + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        QuestionsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Questions questions = dataSnapshot.getValue(Questions.class);
                if (questions != null)
                    questionList.add(questions);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UsersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User users = dataSnapshot.getValue(User.class);
                if (users != null) {
                    usersList.add(users);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}

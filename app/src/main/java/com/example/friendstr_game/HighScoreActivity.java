package com.example.friendstr_game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HighScoreActivity extends AppCompatActivity {
    private ArrayList<User> usersList = new ArrayList<>();
    public ArrayList<UsersAndScore> userAndScore = new ArrayList<UsersAndScore>();
    private RecyclerView recyclerView;
    UserAdapterRanking userAdapterRanking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        Bundle bundle = getIntent().getExtras();
        usersList = bundle.getParcelableArrayList("myUsersList");
        recyclerView.setHasFixedSize(true);

        init();

    }

    //set data to userAdapterRanking which connect between the userInformation layout to activity_high_score layout
    private void init() {

        userAdapterRanking = new UserAdapterRanking(HighScoreActivity.this);
        recyclerView.setAdapter(userAdapterRanking);

        for (int i = 0; i < usersList.size(); i++) {
            userAndScore.add(new UsersAndScore(usersList.get(i).getName(), usersList.get(i).getHighScore()));
        }
        sortScore(userAndScore);

        //in case there is no enough users to display Top 10
        try {
            userAdapterRanking.setUsersData(userAndScore.subList(0, 10));
        }catch (ArrayIndexOutOfBoundsException e)
        {
            System.out.println("Array Index is out of bounds");
        }
        userAdapterRanking.notifyDataSetChanged();

    }

    //Sort all existing users by score desc
    public ArrayList<UsersAndScore> sortScore(ArrayList<UsersAndScore> usersAndScores) {
        for (int i = 0; i < usersAndScores.size() - 1; i++) {
            for (int j = 0; j < usersAndScores.size() - i - 1; j++) {
                if (usersAndScores.get(j).getUserScore() < usersAndScores.get(j + 1).getUserScore()) {
                    ArrayList<UsersAndScore> temp = new ArrayList<UsersAndScore>();
                    temp.add(usersAndScores.get(j));
                    usersAndScores.set(j, usersAndScores.get(j + 1));
                    usersAndScores.set(j + 1, temp.get(0));
                }
            }
        }
        return usersAndScores;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

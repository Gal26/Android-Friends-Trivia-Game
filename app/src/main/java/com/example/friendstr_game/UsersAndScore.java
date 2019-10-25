package com.example.friendstr_game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsersAndScore {

    private String username;
    int userHighScore;

    public UsersAndScore(String username, int userScore) {
        this.username = username;
        this.userHighScore = userScore;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserScore() {
        return userHighScore;
    }

    public void setUserScore(int userScore) {
        this.userHighScore = userScore;
    }
}

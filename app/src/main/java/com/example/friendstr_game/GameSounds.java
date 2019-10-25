package com.example.friendstr_game;
import android.content.Context;
import android.media.MediaPlayer;

import androidx.fragment.app.FragmentActivity;

public class GameSounds {
     MediaPlayer buttonSound;

    public GameSounds(FragmentActivity fragmentActivity, String soundType) {
        switch (soundType) {
            case "Default Sound": {
                buttonSound = MediaPlayer.create(fragmentActivity,R.raw.button_sound);
                playSound();
                break;
            }
            case "Game Over Sound": {
                buttonSound = MediaPlayer.create(fragmentActivity,R.raw.game_over);
                playSound();
                break;
            }

        }

    }

    public GameSounds(Context activity, String soundType) {
        switch (soundType) {
            case "Default Sound": {
                buttonSound = MediaPlayer.create(activity,R.raw.button_sound);
                playSound();
                break;
            }
            case "Game Over Sound": {
                buttonSound = MediaPlayer.create(activity,R.raw.game_over);
                playSound();
                break;
            }
        }
    }

    public void playSound()
    {
        try {
            if (buttonSound.isPlaying()) {
                buttonSound.stop();
                buttonSound.release();
            }
            buttonSound.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

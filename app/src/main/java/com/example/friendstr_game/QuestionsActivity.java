package com.example.friendstr_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class QuestionsActivity extends AppCompatActivity {

    private TextView question;
    private TextView timeLeft;
    private TextView real_time_score;
    private Button optionA_Btn;
    private Button optionB_Btn;
    private Button optionC_Btn;
    private Button optionD_Btn;
    private Button next_Btn;
    private MyCountDownTimer myCountDownTimer;
    private ProgressBar progressBar;
    private LinearLayout option_container;
    private ImageView life_heart_top;
    private ImageView life_heart_mid;
    private ImageView life_heart_bottom;
    private Random random = new Random();
    Handler handler = new Handler();
    private int n;
    private int cur_score = 0;
    private int count_life_left = 3;
    private boolean optionA_Btn_Pressed = false;
    private boolean optionB_Btn_Pressed = false;
    private boolean optionC_Btn_Pressed = false;
    private boolean optionD_Btn_Pressed = false;
    private boolean chooseAns = true;
    private ProgressDialog mProgressDialog;
    //AuthenticationVariable
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;
    //FireBaseVariables
    private FirebaseDatabase database;
    private DatabaseReference useresRef;
    private FirebaseUser fbUser;
    private int userHighScore = -1;
    private User user;
    private ArrayList<Questions> questionList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        //EditText & Buttons
        question = (TextView) findViewById(R.id.question);
        real_time_score = (TextView) findViewById(R.id.real_time_score);
        optionA_Btn = (Button) findViewById(R.id.option1_btn);
        optionB_Btn = (Button) findViewById(R.id.option2_btn);
        optionC_Btn = (Button) findViewById(R.id.option3_btn);
        optionD_Btn = (Button) findViewById(R.id.option4_btn);
        next_Btn = (Button) findViewById(R.id.next_btn);

        //timerProgressBar and displaytime
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        timeLeft = (TextView) findViewById(R.id.display_time);

        //life symbols
        life_heart_top = (ImageView) findViewById(R.id.life_display_top);
        life_heart_mid = (ImageView) findViewById(R.id.life_display_mid);
        life_heart_bottom = (ImageView) findViewById(R.id.life_display_bottom);

        option_container = (LinearLayout) findViewById(R.id.options_container);
        mProgressDialog = new ProgressDialog(this);
        //FireBase
        database = FirebaseDatabase.getInstance();
        useresRef = database.getReference("Users");
        //Authentication
        mAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                fbUser = firebaseAuth.getCurrentUser();
            }
        };

        Bundle bundle = getIntent().getExtras();
        questionList = bundle.getParcelableArrayList("myQuestionList");
        userHighScore = bundle.getInt("userHighScore");

        //initial first question
        updateQuestion();
        myCountDownTimer = new MyCountDownTimer(20000, 100);
        myCountDownTimer.start();

//choose possible answer buttons
        optionA_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionA_Btn.setBackgroundResource(R.drawable.button_color_changer);
                handleButtonSelected(view);
            }
        });

        optionB_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionB_Btn.setBackgroundResource(R.drawable.button_color_changer);
                handleButtonSelected(view);
            }
        });

        optionC_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionC_Btn.setBackgroundResource(R.drawable.button_color_changer);
                handleButtonSelected(view);
            }
        });

        optionD_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionD_Btn.setBackgroundResource(R.drawable.button_color_changer);
                handleButtonSelected(view);
            }
        });

        //next button - after choosing answer
        next_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!optionA_Btn_Pressed && !optionB_Btn_Pressed && !optionC_Btn_Pressed && !optionD_Btn_Pressed) {
                    chooseAns = false;
                    showUncheckAns();
                }
                checkAns();

                if (chooseAns) {
                    handleButtonSelected(view);
                    myCountDownTimer.cancel();
                    myCountDownTimer.start();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            defaultColorButtons();

                            real_time_score.setText("Score " + String.valueOf(cur_score));
                            questionList.remove(n);
                            if (count_life_left > 0) {
                                updateQuestion();
                                if (count_life_left == 2) {
                                    life_heart_top.setBackgroundResource(R.drawable.heart_less);
                                } else if (count_life_left == 1) {
                                    life_heart_mid.setBackgroundResource(R.drawable.heart_less);
                                }
                            } else {
                                life_heart_bottom.setBackgroundResource(R.drawable.heart_less);
                                updateUserScore(cur_score);
                                myCountDownTimer.cancel();
                                gameOver();
                            }
                        }
                    }, 1000);

                }
            }
        });


    }

    //changing button color only for the pressed one
    public void handleButtonSelected(View v) {
        switch (v.getId()) {
            case R.id.option1_btn: {
                optionC_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionB_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionD_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionA_Btn_Pressed = true;
                optionB_Btn_Pressed = false;
                optionC_Btn_Pressed = false;
                optionD_Btn_Pressed = false;
                chooseAns = true;
            }
            break;

            case R.id.option2_btn: {
                optionA_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionC_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionD_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionA_Btn_Pressed = false;
                optionB_Btn_Pressed = true;
                optionC_Btn_Pressed = false;
                optionD_Btn_Pressed = false;
                chooseAns = true;


            }
            break;

            case R.id.option3_btn: {
                optionA_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionB_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionD_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionA_Btn_Pressed = false;
                optionB_Btn_Pressed = false;
                optionC_Btn_Pressed = true;
                optionD_Btn_Pressed = false;
                chooseAns = true;

            }
            break;

            case R.id.option4_btn: {
                optionA_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionB_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionC_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionA_Btn_Pressed = false;
                optionB_Btn_Pressed = false;
                optionC_Btn_Pressed = false;
                optionD_Btn_Pressed = true;
                chooseAns = true;

            }
            break;

            default:
                optionA_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionD_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionB_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionC_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
                optionA_Btn_Pressed = false;
                optionB_Btn_Pressed = false;
                optionC_Btn_Pressed = false;
                optionD_Btn_Pressed = false;
        }
    }

    //check if the option button that was pressed, is also the correct answer
    public void checkAns() {

        if (optionA_Btn_Pressed) {

            if (optionA_Btn.getText().toString().equals(questionList.get(n).getAnswerNumber())) {
                cur_score += 5;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        optionA_Btn.setBackgroundResource(R.drawable.correct_answer);
                        //Using Open Source lib - Animate the update Question
                        YoYo.with(Techniques.BounceIn).duration(900).playOn(optionA_Btn);
                    }
                }, 100);

            } else {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        optionA_Btn.setBackgroundResource(R.drawable.wrong_answer);
                        checkCorrectAnswer();
                        YoYo.with(Techniques.Shake).duration(1000).playOn(optionA_Btn);
                    }
                }, 100);
            }

        }
        if (optionB_Btn_Pressed) {

            if (optionB_Btn.getText().toString().equals(questionList.get(n).getAnswerNumber())) {
                cur_score += 5;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        optionB_Btn.setBackgroundResource(R.drawable.correct_answer);
                        YoYo.with(Techniques.BounceIn).duration(900).playOn(optionB_Btn);
                    }
                }, 100);

            } else {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        optionB_Btn.setBackgroundResource(R.drawable.wrong_answer);
                        checkCorrectAnswer();
                        YoYo.with(Techniques.Shake).duration(1000).playOn(optionB_Btn);
                    }
                }, 100);
            }

        }
        if (optionC_Btn_Pressed) {

            if (optionC_Btn.getText().toString().equals(questionList.get(n).getAnswerNumber())) {
                cur_score += 5;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        optionC_Btn.setBackgroundResource(R.drawable.correct_answer);
                        YoYo.with(Techniques.BounceIn).duration(900).playOn(optionC_Btn);
                    }
                }, 100);

            } else {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        optionC_Btn.setBackgroundResource(R.drawable.wrong_answer);
                        checkCorrectAnswer();
                        YoYo.with(Techniques.Shake).duration(1000).playOn(optionC_Btn);
                    }
                }, 100);
            }

        }
        if (optionD_Btn_Pressed) {

            if (optionD_Btn.getText().toString().equals(questionList.get(n).getAnswerNumber())) {
                cur_score += 5;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        optionD_Btn.setBackgroundResource(R.drawable.correct_answer);
                        YoYo.with(Techniques.BounceIn).duration(900).playOn(optionD_Btn);
                    }
                }, 100);

            } else {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        optionD_Btn.setBackgroundResource(R.drawable.wrong_answer);
                        checkCorrectAnswer();
                        YoYo.with(Techniques.Shake).duration(1000).playOn(optionD_Btn);
                    }
                }, 100);
            }

        }
    }

    //inner class for timerProgressBar and displaytime
    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            int progress = (int) (millisUntilFinished / 1000);

            progressBar.setProgress(progressBar.getMax() - progress);
            timeLeft.setText(String.valueOf(millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            count_life_left--;
            if (count_life_left > 0) {
                updateQuestion();
                if (count_life_left == 2) {
                    life_heart_top.setBackgroundResource(R.drawable.heart_less);
                    myCountDownTimer.start();
                } else if (count_life_left == 1) {
                    life_heart_mid.setBackgroundResource(R.drawable.heart_less);
                    updateQuestion();
                    myCountDownTimer.start();
                }
            } else {
                life_heart_bottom.setBackgroundResource(R.drawable.heart_less);
                myCountDownTimer.cancel();
                updateUserScore(cur_score);
                gameOver();
            }
        }


    }

    //Update next Qurstion
    public void updateQuestion() {

        if (questionList.size() != 0) {
            n = random.nextInt(questionList.size() - 1);
            question.setText(questionList.get(n).getQuestion());
            YoYo.with(Techniques.BounceInLeft).duration(1300).playOn(question);
            YoYo.with(Techniques.BounceInLeft).duration(1300).playOn(option_container);
            optionA_Btn.setText(questionList.get(n).getOption1());
            optionB_Btn.setText(questionList.get(n).getOption2());
            optionC_Btn.setText(questionList.get(n).getOption3());
            optionD_Btn.setText(questionList.get(n).getOption4());
        }
    }

    //None of option buttons selected
    public void defaultColorButtons() {
        optionA_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
        optionD_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
        optionB_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
        optionC_Btn.setBackgroundResource(R.drawable.rounded_borders_answer);
    }

    //check which button is holding the correct answer, and change it color accordingly
    public void checkCorrectAnswer() {
        if (optionA_Btn.getText().toString().equals(questionList.get(n).getAnswerNumber())) {
            optionA_Btn.setBackgroundResource(R.drawable.correct_answer);

        }

        if (optionB_Btn.getText().toString().equals(questionList.get(n).getAnswerNumber())) {
            optionB_Btn.setBackgroundResource(R.drawable.correct_answer);

        }
        if (optionC_Btn.getText().toString().equals(questionList.get(n).getAnswerNumber())) {
            optionC_Btn.setBackgroundResource(R.drawable.correct_answer);

        }
        if (optionD_Btn.getText().toString().equals(questionList.get(n).getAnswerNumber())) {
            optionD_Btn.setBackgroundResource(R.drawable.correct_answer);

        }

        count_life_left--;
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }

    private void updateUserScore(int cur_score) {
        if (cur_score > userHighScore) {
            String userId = mAuth.getCurrentUser().getUid();
            mProgressDialog.setMessage("Nice Job, Updating Your Score...");
            mProgressDialog.show();
            useresRef.child(userId).child("highScore").setValue(cur_score);
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        showQuit();
    }

    private void showQuit() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("Are you sure?")
                .setContentText("You won't be able to recover!").setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        finish();
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }

    private void gameOver() {
        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE).setCustomImage(R.drawable.ic_sentiment_dissatisfied_black_24dp)
                .setTitleText("Game Over")
                .setContentText("score " + cur_score)
                .setConfirmText("Try Again")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        count_life_left = 3;
                        cur_score = 0;
                        myCountDownTimer.cancel();
                        myCountDownTimer.start();
                        defaultColorButtons();
                        Bundle bundle = getIntent().getExtras();
                        questionList = bundle.getParcelableArrayList("myQuestionList");
                        updateQuestion();
                        life_heart_bottom.setBackgroundResource(R.drawable.heart);
                        life_heart_mid.setBackgroundResource(R.drawable.heart);
                        life_heart_top.setBackgroundResource(R.drawable.heart);
                        real_time_score.setText("Score 0");

                        QuestionsActivity.super.onStart();
                    }
                })
                .setCancelButton("Maybe Later", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        myCountDownTimer.cancel();
                        sDialog.dismissWithAnimation();
                        finish();
                    }
                }).show();
    }

    private void showUncheckAns() {
        new SweetAlertDialog(QuestionsActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("HOW YOU DOIN'?")
                .setContentText("you must choose one of the 4 possible answers")
                .setConfirmText("Got it!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }

}

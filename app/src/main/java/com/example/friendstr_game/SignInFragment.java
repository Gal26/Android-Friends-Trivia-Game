package com.example.friendstr_game;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class SignInFragment extends Fragment {
    //EditText & Buttons
    private Button signInBtn;
    private EditText userNameInput, passInput;
    private ProgressDialog mProgressDialog;
    GameSounds gameSounds;
    //AuthenticationVariable
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser fbUser;
    //LocalVariables
    private String passText, userNameText;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        //EditText & Buttons
        userNameInput = (EditText) view.findViewById(R.id.userNameInput);
        passInput = (EditText) view.findViewById(R.id.passInput);
        signInBtn = (Button) view.findViewById(R.id.signInBtn);
        //Authentication
        mAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                fbUser = firebaseAuth.getCurrentUser();
            }
        };


        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameSounds = new GameSounds(getActivity(), "Default Sound");
                signIn();
            }
        });

        return view;
    }

    private void openHomeGameActivity() {
        Intent in = new Intent(getActivity(), HomeGameActivity.class);
        startActivity(in);
    }

    private void signIn() {
        boolean status = getFieldsInput();
        status &= validateStrings();

        if (status) {
           final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();

            mAuth.signInWithEmailAndPassword(userNameText, passText).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        pDialog.dismissWithAnimation();
                        openHomeGameActivity();
                    } else {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...").setContentText("Something went wrong!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        pDialog.dismissWithAnimation();
                                    }
                                }).show();
                    }
                }
            });
        }

    }

    private boolean validateStrings() {
        boolean status = true;

        if (passText.equals("") || userNameText.equals("")) {
            status = false;
            Toast.makeText(getActivity(), "One of the fields is empty", Toast.LENGTH_LONG).show();
        }

        return status;
    }

    private boolean getFieldsInput() {
        boolean status = true;

        try {
            passText = passInput.getText().toString().trim();
            userNameText = userNameInput.getText().toString().trim();
        } catch (Exception e) {
            status = false;
            Log.d("SignInFragment", e + "");
        }

        return status;
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


}

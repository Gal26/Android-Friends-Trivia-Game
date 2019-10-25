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
import com.google.android.gms.tasks.OnFailureListener;
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


public class RegistrationFragment extends Fragment {
    //FragmentVariable
    private Button signUpBtn;
    private EditText emailInput, passInput, confPassInput, userNameInput;
    GameSounds gameSounds;
    //AuthenticationVariable
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;
    //FireBaseVariables
    private FirebaseDatabase database;
    private DatabaseReference useresRef;
    private FirebaseUser fbUser;
    //LocalVariables
    private String emailText, passText, confPassText, userNameText;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        //EditText & Buttons
        emailInput = (EditText) view.findViewById(R.id.emailInput);
        passInput = (EditText) view.findViewById(R.id.newPassInput);
        confPassInput = (EditText) view.findViewById(R.id.confPasswordInput);
        userNameInput = (EditText) view.findViewById(R.id.newUserNameInput);
        signUpBtn = (Button) view.findViewById(R.id.signUpBtn);
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


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameSounds = new GameSounds(getActivity(), "Default Sound");
                createAccount();
            }
        });

        return view;
    }

    private void createAccount() {
        boolean status = getFieldsInput();
        status &= validateStrings();

        if (status) {
            final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Creating Account");
            pDialog.setCancelable(false);
            pDialog.show();

            mAuth.createUserWithEmailAndPassword(emailText, passText).addOnSuccessListener(getActivity(), new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if (authResult != null) {
                        String userId = mAuth.getCurrentUser().getUid();
                        addUserToDataBase(userId);
                        pDialog.dismissWithAnimation();
                        openHomeGameActivity();
                    }else {
                        pDialog.dismissWithAnimation();
                    }
                }
            });
        }
    }

    private void openHomeGameActivity() {
        Intent in = new Intent(getActivity(), HomeGameActivity.class);
        startActivity(in);
    }

    private void addUserToDataBase(String uId) {
        User user = new User(emailText, userNameText, uId);
        useresRef.child(uId).setValue(user);
    }

    private boolean validateStrings() {
        boolean status = true;

        if (emailText.equals("") || passText.equals("") || confPassText.equals("") || userNameText.equals("")) {
            status = false;
            Toast.makeText(getActivity(), "One of the fields is empty", Toast.LENGTH_LONG).show();
        } else if (passText.length() < 6 || !passText.equals(confPassText)) {
            status = false;
            Toast.makeText(getActivity(), "Passwords does not match OR less then 6", Toast.LENGTH_LONG).show();
        }

        return status;
    }

    private boolean getFieldsInput() {
        boolean status = true;

        try {
            emailText = emailInput.getText().toString().trim();
            passText = passInput.getText().toString().trim();
            confPassText = confPassInput.getText().toString().trim();
            userNameText = userNameInput.getText().toString().trim();
        } catch (Exception e) {
            status = false;
            Log.d("RegistrationFragment", e + "");
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

package com.yokoding.afifur.brawijayamessenger.ui.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yokoding.afifur.brawijayamessenger.Helper.ChatHelper;
import com.yokoding.afifur.brawijayamessenger.R;
import com.yokoding.afifur.brawijayamessenger.adapter.UsersChatAdapter;
import com.yokoding.afifur.brawijayamessenger.model.User;

import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText mUserFirstNameRegister;
    private EditText mUserEmailRegister;
    private EditText mUserPassWordRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        hideActionBar();
        mUserFirstNameRegister = (EditText) findViewById(R.id.edit_text_display_name);
        mUserEmailRegister = (EditText) findViewById(R.id.edit_text_email_register);
        mUserPassWordRegister = (EditText) findViewById(R.id.edit_text_password_register);
        Button register = (Button) findViewById(R.id.btn_register_user);
        Button cancel = (Button) findViewById(R.id.btn_cancel_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterUser();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setAuthInstance();
        setDatabaseInstance();
    }

    private void hideActionBar() {
        this.getSupportActionBar().hide();
    }

    private void setAuthInstance() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void setDatabaseInstance() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    private void onRegisterUser() {
        if(getUserDisplayName().equals("") || getUserEmail().equals("") || getUserPassword().equals("")){
            showFieldsAreRequired();
        }else if(isIncorrectEmail(getUserEmail()) || isIncorrectPassword(getUserPassword())) {
            showIncorrectEmailPassword();
        }else {
            signUp(getUserEmail(), getUserPassword());
        }
    }

    private boolean isIncorrectEmail(String userEmail) {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches();
    }

    private boolean isIncorrectPassword(String userPassword) {
        return !(userPassword.length() >= 6);
    }

    private void showIncorrectEmailPassword() {
        showAlertDialog(getString(R.string.error_incorrect_email_pass), true);
    }

    private void showFieldsAreRequired() {
        showAlertDialog(getString(R.string.error_fields_empty), true);
    }

    private void showAlertDialog(String message, boolean isCancelable){

        dialog = ChatHelper.buildAlertDialog(getString(R.string.login_error_title),message,isCancelable,RegisterActivity.this);
        dialog.show();
    }

    private String getUserDisplayName() {
        return mUserFirstNameRegister.getText().toString().trim();
    }

    private String getUserEmail() {
        return mUserEmailRegister.getText().toString().trim();
    }

    private String getUserPassword() {
        return mUserPassWordRegister.getText().toString().trim();
    }


    private void signUp(String email, String password) {

        showAlertDialog("Registering...",true);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                dismissAlertDialog();

                if(task.isSuccessful()){
                    onAuthSuccess(task.getResult().getUser());
                }else {
                    showAlertDialog(task.getException().getMessage(), true);
                }
            }
        });
    }

    private void dismissAlertDialog() {
        dialog.dismiss();
    }

    private void onAuthSuccess(FirebaseUser user) {
        createNewUser(user.getUid());
        goToMainActivity();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void createNewUser(String userId){
        User user = buildNewUser();
        mDatabase.child("users").child(userId).setValue(user);
    }

    private User buildNewUser() {
        return new User(
                getUserDisplayName(),
                getUserEmail(),
                UsersChatAdapter.ONLINE,
                ChatHelper.generateRandomAvatarForUser(),
                new Date().getTime()
        );
    }

}

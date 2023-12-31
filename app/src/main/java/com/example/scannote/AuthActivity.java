package com.example.scannote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scannote.database.entity.User;
import com.example.scannote.viewmodel.AuthActivityViewModel;

import java.util.ArrayList;

public class AuthActivity extends AppCompatActivity {

    EditText mEmailEt, mPasswordEt;
    Button mSubmitBtn;
    TextView mAuthTypeSwitch;
    AuthActivityViewModel mAuthActivityViewModel;
    boolean isRegister = true;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mEmailEt = findViewById(R.id.email_et);
        mPasswordEt = findViewById(R.id.psw_et);
        mSubmitBtn = findViewById(R.id.submit_btn);
        mAuthTypeSwitch = findViewById(R.id.auth_type_switch);

        mAuthActivityViewModel = new ViewModelProvider(this).get(AuthActivityViewModel.class);

        mSubmitBtn.setOnClickListener(v -> {
            if (isRegister) {
                String email = mEmailEt.getText().toString();
                String password =  mPasswordEt.getText().toString();
                if (password.isEmpty() || email.isEmpty()) {
                    Toast.makeText(this, "Both email and password are required", Toast.LENGTH_SHORT).show();
                }
                User user = new User(email, password);
                mAuthActivityViewModel.createNewUser(user);
            } else {
                String email = mEmailEt.getText().toString();
                String password =  mPasswordEt.getText().toString();
                if (password.isEmpty() || email.isEmpty()) {
                    Toast.makeText(this, "Both email and password are required", Toast.LENGTH_SHORT).show();
                }
                 mAuthActivityViewModel.getUserByEmailAndPassword(email, password).observe(this, user -> {
                    this.mUser = user;
                 });


            }
        });

    }
}
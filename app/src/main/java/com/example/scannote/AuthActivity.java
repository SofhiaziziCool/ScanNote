package com.example.scannote;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Collections;
import java.util.List;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";

    // UI Views
    EditText mEmailEt, mPasswordEt;
    Button mSubmitBtn;
    TextView mAuthTypeSwitch;

    // vars
    private FirebaseAuth mAuth;
    List<AuthUI.IdpConfig> providers = Collections.singletonList(
            new AuthUI.IdpConfig.EmailBuilder().build()
    );
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
        } else {
            if (response != null) {
                Toast.makeText(this, "Sign in failed with Error: " + response.getError(), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Sign in failed" , Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mAuth = FirebaseAuth.getInstance();

        mEmailEt = findViewById(R.id.email_et);
        mPasswordEt = findViewById(R.id.psw_et);
        mSubmitBtn = findViewById(R.id.submit_btn);
        mAuthTypeSwitch = findViewById(R.id.auth_type_switch);
        mAuthTypeSwitch.setOnClickListener(view -> {
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build();
            signInLauncher.launch(signInIntent);
        });

        mSubmitBtn.setOnClickListener(v -> {
                String email = mEmailEt.getText().toString();
                String password =  mPasswordEt.getText().toString();
                if (password.isEmpty() || email.isEmpty()) {
                    Toast.makeText(this, "Both email and password are required", Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            });
                }
        });
    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
}
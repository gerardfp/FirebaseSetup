package com.example.firebasesetup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 1234;
    private FirebaseAuth mAuth;

    TextView userTextView;
    EditText emailEditText, passwordEditText;
    Button signOutButton, createAccountButton, emailSignInButton;
    SignInButton googleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build());

        userTextView = findViewById(R.id.userTextView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signOutButton = findViewById(R.id.signOutButton);
        createAccountButton = findViewById(R.id.createAccountButton);
        emailSignInButton = findViewById(R.id.emailSignInButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);

        signOutButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
        emailSignInButton.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);

        displayAutentication();

    }

    void createAccount(){
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Crear email/password OK", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("ABCD", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Crear email/password FAIL", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void emailSignIn() {
        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Email signin OK", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("ABCD", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Email signin FAIL", Toast.LENGTH_SHORT).show();
                        }
                        displayAutentication();
                    }
                });
    }

    private void googleSignIn() {
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                firebaseAuthWithGoogle(GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class));
            } catch (ApiException e) {
                Log.e("ABCD", "Google sign in FAIL", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(acct.getIdToken(), null))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("ABCD", "SignIn with google OK");
                        } else {
                            Log.w("ABCD", "SignIn with google FAIL", task.getException());
                            Toast.makeText(MainActivity.this, "Google authentication FAIL.", Toast.LENGTH_SHORT).show();
                        }
                        displayAutentication();

                    }
                });
    }

    void signOut(){
        mAuth.signOut();

        mGoogleSignInClient.signOut();

        displayAutentication();
    }

    void displayAutentication(){
        if(mAuth.getCurrentUser() != null){
            userTextView.setText(mAuth.getCurrentUser().getEmail());
            signOutButton.setVisibility(View.VISIBLE);
        } else {
            userTextView.setText("Usuario no autenticado");
            signOutButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signOutButton:
                signOut();
                break;
            case R.id.createAccountButton:
                createAccount();
                break;
            case R.id.emailSignInButton:
                emailSignIn();
                break;
            case R.id.googleSignInButton:
                googleSignIn();
                break;
        }
    }
}

package com.example.sandwraith8.gr_final;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.sandwraith8.gr_final.model.Contact;
import com.example.sandwraith8.gr_final.repository.local.ContactRepository;
import com.example.sandwraith8.gr_final.service.GoogleService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class GoogleSignInTest extends AppCompatActivity implements View.OnClickListener {
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 123;
    private SignInButton signInButton;
    private Button guestSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in_test);

        mGoogleSignInClient = GoogleService.getInstance().getClient(this);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
        guestSignin = findViewById(R.id.guest_sign_in);
        guestSignin.setOnClickListener(this);

    }

    private void changeToMain(String id) {
        Intent main = new Intent(this, Homepage.class);
        main.putExtra("account", id);
        startActivity(main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            changeToMain(account.getId());
        } else {
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            String email = account.getEmail();
//            String id = account.getId();
//            Person person = new Person();
//            person.setGoogleId(id);
//            person.setEmail(email);
//            FirebasePersonRepository.getInstance().find(id, new PersonAction() {
//                @Override
//                public void onGettingSuccessful(Person person) {
//                    if (person == null) {
//                        FirebasePersonRepository.getInstance().add(person);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Welcome " + person.getEmail(), Toast.LENGTH_LONG).show();
//                    }
//                }
//
//                @Override
//                public void onError(String message) {
//
//                }
//            });
            changeToMain(account.getId());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
}

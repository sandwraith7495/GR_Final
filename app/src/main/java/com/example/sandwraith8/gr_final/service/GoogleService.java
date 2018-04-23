package com.example.sandwraith8.gr_final.service;

import android.app.Activity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * Created by sandwraith8 on 08/04/2018.
 */

public class GoogleService {
    private static final GoogleService ourInstance = new GoogleService();

    public static GoogleService getInstance() {
        return ourInstance;
    }

    private GoogleService() {
    }

    public GoogleSignInClient getClient(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(activity, gso);
    }
}

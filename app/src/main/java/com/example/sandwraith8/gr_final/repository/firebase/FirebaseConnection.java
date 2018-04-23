package com.example.sandwraith8.gr_final.repository.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by sandwraith8 on 24/03/2018.
 */

public abstract class FirebaseConnection {
    protected DatabaseReference connection;

    public FirebaseConnection() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        connection = database.getReference();
    }
}

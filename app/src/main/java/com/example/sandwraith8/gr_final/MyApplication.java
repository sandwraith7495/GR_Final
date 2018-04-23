package com.example.sandwraith8.gr_final;

import android.app.Application;

import com.example.sandwraith8.gr_final.repository.local.ContactRepository;

/**
 * Created by sandwraith8 on 14/04/2018.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ContactRepository.init(getApplicationContext());
    }
}

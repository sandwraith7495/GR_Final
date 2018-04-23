package com.example.sandwraith8.gr_final.repository.firebase;

import com.example.sandwraith8.gr_final.model.Person;

/**
 * Created by sandwraith8 on 24/03/2018.
 */

public interface PersonAction {
    void onGettingSuccessful(Person person);
    void onError(String message);
}

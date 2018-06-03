package com.example.sandwraith8.gr_final.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by sandwraith8 on 24/03/2018.
 */

@Getter
@Setter
public class Person {
    private String googleId;
    private String email;
    private List<Contact> contacts = new ArrayList<>();

    public Person(String googleId) {
        this.googleId = googleId;
    }

    public Person() {
    }

    @Getter
    @Setter
    public static class FirebasePerson {
        private String googleId;
        private String email;
        private Map<String, Contact> contacts = new HashMap<>();

        public FirebasePerson() {
        }
    }
}
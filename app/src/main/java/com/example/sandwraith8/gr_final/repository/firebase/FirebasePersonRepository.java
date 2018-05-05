package com.example.sandwraith8.gr_final.repository.firebase;

import com.example.sandwraith8.gr_final.model.Contact;
import com.example.sandwraith8.gr_final.model.Person;
import com.example.sandwraith8.gr_final.repository.PersonRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.UUID;

/**
 * Created by sandwraith8 on 24/03/2018.
 */

public class FirebasePersonRepository extends FirebaseConnection implements PersonRepository {

    private static FirebasePersonRepository instance = new FirebasePersonRepository();

    public static FirebasePersonRepository getInstance() {
        return instance;
    }

    @Override
    public void add(Person person) {
        connection.child("persons").child(person.getGoogleId()).setValue(person);
    }

    public void addContact(Person person, Contact contact) {
        connection.child("persons").child(person.getGoogleId()).child("contacts").child(contact.getEmail()).setValue(contact);
    }

    public void addContact(Person person, List<Contact> contacts) {
        DatabaseReference p = connection.child("persons").child(person.getGoogleId()).child("contacts");
        for (Contact contact : contacts) {
            p.child(UUID.randomUUID().toString()).setValue(contact);
        }
    }

    public void find(String id, final PersonAction action) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Person person = dataSnapshot.getValue(Person.class);
                action.onGettingSuccessful(person);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                action.onError(databaseError.getMessage());
            }
        };
        connection.child("persons").child(id).addListenerForSingleValueEvent(listener);
    }

    public void findLive(String id, final PersonAction action) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Person person = dataSnapshot.getValue(Person.class);
                action.onGettingSuccessful(person);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                action.onError(databaseError.getMessage());
            }
        };
        connection.child("persons").child(id).addValueEventListener(listener);
    }

    @Override
    public Person find(String name) {
        return null;
    }
}

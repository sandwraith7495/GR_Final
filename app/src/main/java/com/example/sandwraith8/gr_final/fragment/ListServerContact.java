package com.example.sandwraith8.gr_final.fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sandwraith8.gr_final.R;
import com.example.sandwraith8.gr_final.RVAdapter;
import com.example.sandwraith8.gr_final.model.Contact;
import com.example.sandwraith8.gr_final.model.Person;
import com.example.sandwraith8.gr_final.repository.firebase.FirebasePersonRepository;
import com.example.sandwraith8.gr_final.repository.firebase.PersonAction;
import com.example.sandwraith8.gr_final.repository.local.ContactRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by sandwraith8 on 01/05/2018.
 */

public class ListServerContact extends BaseFragment {
    private Button syncButton;
    private RVAdapter adapter;
    private RecyclerView cv;

    public ListServerContact() {
        super(R.layout.activity_list_server_contact);
    }

    @Override
    public void init(View v) {
        syncButton = v.findViewById(R.id.sync);
        cv = v.findViewById(R.id.cv);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        cv.setLayoutManager(mLayoutManager);
        cv.setItemAnimator(new DefaultItemAnimator());
        final List<Contact> personContacts = new ArrayList<>();
        adapter = new RVAdapter(personContacts);
        cv.setAdapter(adapter);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        final String id = account.getId();
        FirebasePersonRepository.getInstance().findLive(id, new PersonAction() {
            @Override
            public void onGettingSuccessful(Person person) {
                personContacts.removeAll(personContacts);
                personContacts.addAll(person.getContacts().values());
                adapter.notifyItemRangeChanged(0, personContacts.size());
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Loi", Toast.LENGTH_LONG).show();
            }
        });

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebasePersonRepository.getInstance().find(id, new PersonAction() {
                    @Override
                    public void onGettingSuccessful(Person person) {
                        List<Contact> contacts = ContactRepository.getInstance().findAll();
                        List<Contact> personContacts = new ArrayList<>(person.getContacts().values());
                        Iterator<Contact> iterator = personContacts.iterator();
                        while (iterator.hasNext()) {
                            Contact contact = iterator.next();
                            if (contacts.contains(contact)) {
                                iterator.remove();
                            }
                        }
                        for (Contact contact : personContacts) {
                            ContactRepository.getInstance().add(contact);
                        }
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String message) {
                        Log.e(TAG, "onError: " + message);
                        Toast.makeText(getContext(), "Loi", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

}

package com.example.sandwraith8.gr_final.fragment;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import static com.example.sandwraith8.gr_final.RVAdapter.DELETE;

/**
 * Created by sandwraith8 on 01/05/2018.
 */

public class ListServerContact extends BaseFragment {
    private Button syncButton;
    private TextView delete_server;
    private RVAdapter adapter;
    private RecyclerView cv;
    private GetStatus setStatus;

    public ListServerContact() {
        super(R.layout.activity_list_server_contact);
    }

    @Override
    public void init(View v) {
        setStatus = new GetStatus() {
            @Override
            public void setStatus(int status) {
                if (status == DELETE) {
                    delete_server.setVisibility(View.VISIBLE);
                }
            }
        };
        delete_server = v.findViewById(R.id.delete_server);
        syncButton = v.findViewById(R.id.sync);
        cv = v.findViewById(R.id.cv);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        cv.setLayoutManager(mLayoutManager);
        cv.setItemAnimator(new DefaultItemAnimator());
        final List<Contact> personContacts = new ArrayList<>();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        adapter = new RVAdapter(personContacts, fragmentManager, setStatus);
        cv.setAdapter(adapter);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        final String id = account.getId();
        FirebasePersonRepository.getInstance().findLive(id, new PersonAction() {
            @Override
            public void onGettingSuccessful(Person person) {
                personContacts.removeAll(personContacts);
                personContacts.addAll(person.getContacts());
                adapter.notifyItemRangeChanged(0, personContacts.size());
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Loi", Toast.LENGTH_LONG).show();
            }
        });

        delete_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebasePersonRepository.getInstance().find(id, new PersonAction() {
                    @Override
                    public void onGettingSuccessful(Person person) {
                        adapter.removeserverItems(person);
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
            }
        });

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebasePersonRepository.getInstance().find(id, new PersonAction() {
                    @Override
                    public void onGettingSuccessful(Person person) {
                        List<Contact> contacts = ContactRepository.getInstance().findAll();
                        Iterator<Contact> iterator = personContacts.iterator();
                        while (iterator.hasNext()) {
                            Contact contact = iterator.next();
                            if (contacts.contains(contact)) {
                                iterator.remove();
                            }
                        }
//                        for (int i = 0; i < personContacts.size(); i++) {
//                            Contact contact = personContacts.get(i);
//                            if (contacts.contains(contact)) {
//                                personContacts.remove(i);
//                            }
//                        }
                        for (int i = 0; i < personContacts.size(); i++) {
                            Contact contact = personContacts.get(i);
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

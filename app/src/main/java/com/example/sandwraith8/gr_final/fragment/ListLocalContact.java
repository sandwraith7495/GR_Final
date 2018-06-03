package com.example.sandwraith8.gr_final.fragment;

import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
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

import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by sandwraith8 on 25/04/2018.
 */

public class ListLocalContact extends BaseFragment {

    private Button syncButton;
    private TextView delete;
    private RecyclerView cv;
    private RVAdapter adapter;
    private List<Contact> contacts;
    public static final int DELETE = 1;
    public static final int SYNC = 2;
    private GetStatus setStatus;

    public ListLocalContact() {
        super(R.layout.activity_list_contact);
    }

    @Override
    public void init(View v) {
        syncButton = v.findViewById(R.id.sync);
        delete = v.findViewById(R.id.delete);
        cv = v.findViewById(R.id.cv);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        cv.setLayoutManager(mLayoutManager);
        cv.setItemAnimator(new DefaultItemAnimator());
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        contacts = ContactRepository.getInstance().findAll();
        setStatus = new GetStatus() {
            @Override
            public void setStatus(int status) {
                if (status == DELETE) {
                    delete.setVisibility(View.VISIBLE);
                }
            }
        };

        adapter = new RVAdapter(contacts, fragmentManager, setStatus);
        cv.setAdapter(adapter);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        final String id = account.getId();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.removeItems();
            }
        });

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebasePersonRepository.getInstance().find(id, new PersonAction() {
                    @Override
                    public void onGettingSuccessful(Person person) {
                        List<Contact> contacts = ContactRepository.getInstance().findAll();
                        Iterator<Contact> iterator = contacts.iterator();
                        List<Contact> personContacts = person.getContacts();
                        while (iterator.hasNext()) {
                            Contact contact = iterator.next();
                            if (personContacts.contains(contact)) {
                                iterator.remove();
                            }
                        }
                        FirebasePersonRepository.getInstance().addContact(person, contacts);
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

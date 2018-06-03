package com.example.sandwraith8.gr_final.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sandwraith8.gr_final.R;
import com.example.sandwraith8.gr_final.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandwraith8 on 14/05/2018.
 */

public class CustomDialogFragment extends DialogFragment {
    private TextView contactName;
    private TextView contactEmail;
    private Button addContact;
    private ImageView contactImage;
    private List<TextView> contactPhones;

    public CustomDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.contact_details, container, false);
        contactName = v.findViewById(R.id.person_name);
        contactEmail = v.findViewById(R.id.person_email);
        contactImage = v.findViewById(R.id.person_photo);
        addContact = v.findViewById(R.id.addContact);

        Bundle bundle = getArguments();
        Contact contact = (Contact) bundle.get("contact");
        contactName.setText(contact.getName());
        contactEmail.setText(contact.getEmail());
        LinearLayout my_root = v.findViewById(R.id.my_root);
        my_root.setOrientation(LinearLayout.VERTICAL);
        contactPhones = new ArrayList<>();
        for (int i = 0; i < contact.getPhones().size(); i++) {
            TextView textView = new TextView(v.getContext());
            textView.setText(contact.getPhones().get(i));
            my_root.addView(textView);
            contactPhones.add(textView);
        }
        byte[] decodedString = Base64.decode(contact.getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        contactImage.setImageBitmap(decodedByte);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToContacts();
            }
        });
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void addToContacts() {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        for (int i = 0; i< contactPhones.size(); i++){
            intent.putExtra(ContactsContract.Intents.Insert.NAME, contactName.getText());
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, contactEmail.getText());
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, contactPhones.get(i).getText());
            intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
            startActivity(intent);
        }

    }
}
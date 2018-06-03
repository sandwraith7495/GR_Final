package com.example.sandwraith8.gr_final.repository.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.sandwraith8.gr_final.model.Contact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sandwraith8 on 14/04/2018.
 */

public class ContactRepository {
    private Database database;

    private static ContactRepository ourInstance;

    private ContactRepository(Context context) {
        database = new Database(context);
    }

    public static void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context is null");
        }
        ourInstance = new ContactRepository(context);
    }

    public static ContactRepository getInstance() {
        if (ourInstance == null) {
            throw new RuntimeException("Not initialized");
        }
        return ourInstance;
    }

    public void add(Contact contact) {
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", contact.getId());
        values.put("name", contact.getName());
        values.put("email", contact.getEmail());
        values.put("image", contact.getImage());
        StringBuilder phone = new StringBuilder();
        Iterator<String> iterator = contact.getPhones().iterator();
        while (iterator.hasNext()) {
            phone.append(iterator.next());
            if (iterator.hasNext()) {
                phone.append(";");
            }
        }
        values.put("phone", phone.toString());
        db.insert("contact", null, values);
    }

    public List<Contact> findAll() {
        SQLiteDatabase db = database.getReadableDatabase();
        String[] projection = {
                "id",
                "name",
                "email",
                "phone",
                "image"
        };
        Cursor cursor = db.query(
                "contact",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                new String[]{},          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null
        );
        List<Contact> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            Contact contact = new Contact();
            contact.setName(cursor.getString(cursor.getColumnIndex("name")));
            contact.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            contact.setId(cursor.getString(cursor.getColumnIndex("id")));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            contact.setImage(cursor.getString(cursor.getColumnIndex("image")));
            if (phone != null) {
                contact.setPhones(Arrays.asList(phone.split(";")));
            }
            contacts.add(contact);
        }
        cursor.close();
        return contacts;
    }

    public Contact find(String email) {
        SQLiteDatabase db = database.getReadableDatabase();
        String[] projection = {
                "name",
                "email",
                "phone"
        };
        String selection = "email = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(
                "contact",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null
        );
        Contact contact = new Contact();
        while (cursor.moveToNext()) {
            contact.setName(cursor.getString(0));
            contact.setEmail(cursor.getString(1));
            String phone = cursor.getString(0);
            contact.setPhones(Arrays.asList(phone.split(";")));
            break;
        }
        cursor.close();
        return contact;
    }

    public void delete(String id){
        SQLiteDatabase db = database.getWritableDatabase();
        db.delete("contact","id = ?", new String[]{id});
        db.close();
    }

}

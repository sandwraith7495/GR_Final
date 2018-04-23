package com.example.sandwraith8.gr_final;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.sandwraith8.gr_final.repository.local.ContactRepository;

public class ListContact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contact);
        RecyclerView cv = (RecyclerView)findViewById(R.id.cv);
        RVAdapter adapter = new RVAdapter(ContactRepository.getInstance().findAll());
        cv.setAdapter(adapter);
    }
}

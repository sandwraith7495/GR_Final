package com.example.sandwraith8.gr_final;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sandwraith8.gr_final.model.Contact;

import java.util.List;

/**
 * Created by sandwraith8 on 17/04/2018.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ContactViewHolder> {
    List<Contact> contacts;

    public RVAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        ContactViewHolder pvh = new ContactViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.contactName.setText(contacts.get(position).getName());
        holder.contactEmail.setText(contacts.get(position).getEmail());
        if (contacts.get(position).getImage() != null) {
            byte[] decodedString = Base64.decode(contacts.get(position).getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.contactImage.setImageBitmap(decodedByte);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView contactName;
        TextView contactEmail;
        ListView contactPhone;
        ImageView contactImage;

        ContactViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            contactName = itemView.findViewById(R.id.person_name);
            contactEmail = itemView.findViewById(R.id.person_email);
            contactImage = itemView.findViewById(R.id.person_photo);
            contactPhone = itemView.findViewById(R.id.person_phone);
        }
    }

}
package com.example.sandwraith8.gr_final;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sandwraith8.gr_final.model.Contact;
import com.example.sandwraith8.gr_final.repository.local.ContactRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandwraith8 on 17/04/2018.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ContactViewHolder> {
    List<Contact> contacts;
    private List<Contact> contactListFiltered;

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
    public void onBindViewHolder(final ContactViewHolder holder, final int position) {
        final Contact model = contacts.get(position);
//        holder.view.setBackgroundColor(model.isSelected() ? Color.CYAN : Color.WHITE);
        holder.contactName.setText(contacts.get(position).getName());
        holder.contactEmail.setText(contacts.get(position).getEmail());
        holder.contactPhone.setText(contacts.get(position).getPhones().get(0));
        if (contacts.get(position).getImage() != null) {
            byte[] decodedString = Base64.decode(contacts.get(position).getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.contactImage.setImageBitmap(decodedByte);
        }
        holder.contactImage.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                removeItem(position);
                return true;
            }
        });
        holder.contactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setSelected(!model.isSelected());
                holder.cv.setBackgroundColor(model.isSelected() ? Color.CYAN : Color.WHITE);
            }
        });
    }

    private void removeItem(int position) {
        contacts.remove(position);
        notifyItemRemoved(position);
//        ContactRepository.getInstance().delete(contacts.get(position).getId());
    }

    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contacts;
                } else {
                    List<Contact> filteredList = new ArrayList<>();
                    for (Contact row : contacts) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getName().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
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
//        ListView contactPhone;
        TextView contactPhone;
        ImageView contactImage;
        View view;

        ContactViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            cv = itemView.findViewById(R.id.cv);
            contactName = itemView.findViewById(R.id.person_name);
            contactEmail = itemView.findViewById(R.id.person_email);
            contactImage = itemView.findViewById(R.id.person_photo);
            contactPhone = itemView.findViewById(R.id.person_phone);
        }
    }

}
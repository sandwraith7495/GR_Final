package com.example.sandwraith8.gr_final;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sandwraith8.gr_final.fragment.CustomDialogFragment;
import com.example.sandwraith8.gr_final.fragment.GetStatus;
import com.example.sandwraith8.gr_final.model.Contact;
import com.example.sandwraith8.gr_final.model.Person;
import com.example.sandwraith8.gr_final.repository.firebase.FirebasePersonRepository;
import com.example.sandwraith8.gr_final.repository.local.ContactRepository;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by sandwraith8 on 17/04/2018.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ContactViewHolder> {
    List<Contact> contacts;
    private List<Contact> contactListFiltered;
    private Context context;
    private FragmentManager fragmentManager;
    public int status;
    public static final int NORMAL = 0;
    public static final int DELETE = 1;
    public static final int SYNC = 2;
    private GetStatus setStatus;
    private ArrayList<Integer> selectedItem = new ArrayList<>();

    public RVAdapter(List<Contact> contacts, FragmentManager fragmentManager, GetStatus setStatus) {
        this.contacts = contacts;
        this.fragmentManager = fragmentManager;
        this.setStatus = setStatus;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        ContactViewHolder pvh = new ContactViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder holder, final int position) {
        final Contact model = contacts.get(position);
        holder.view.setBackgroundColor(model.isSelected() ? Color.CYAN : Color.WHITE);
        holder.contactName.setText(contacts.get(position).getName());
        holder.contactEmail.setText(contacts.get(position).getEmail());
        holder.contactPhone.setText(contacts.get(position).getPhones().get(0));
        if (contacts.get(position).getImage() != null) {
            byte[] decodedString = Base64.decode(contacts.get(position).getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.contactImage.setImageBitmap(decodedByte);
        }
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                status = DELETE;
                setStatus.setStatus(status);
                holder.view.setBackgroundColor(Color.CYAN);
                model.setSelected(!model.isSelected());
                selectedItem.add(position);
                return true;
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status != DELETE) {
                    CustomDialogFragment newFragment = new CustomDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("contact", model);
                    newFragment.setArguments(bundle);
                    newFragment.show(fragmentManager, "");
                } else {
                    if (!model.isSelected()){
                        holder.view.setBackgroundColor(Color.CYAN);
                        model.setSelected(!model.isSelected());
                        selectedItem.add(position);
                    }
                    else {
                        holder.view.setBackgroundColor(Color.WHITE);
                        model.setSelected(false);
                        selectedItem.remove(position);
                    }
                }
            }
        });
        for (Integer item : selectedItem) {
            Log.d(TAG, "value " + item);
        }
    }

    public void removeserverItems(final Person person) {
        new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        for (int position : selectedItem) {
                            Contact contact = contacts.get(position);
                            String id = contact.getId();
                            FirebasePersonRepository.getInstance().delete(id, person);
                            contacts.remove(position);
                            notifyItemRemoved(position);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();

    }

    public void removeItems() {
        new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        for (int item : selectedItem) {
                            removeItem(item);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();

    }

    private void removeItem(final int position) {
        ContactRepository.getInstance().delete(contacts.get(position).getId());
        contacts.remove(position);
        notifyItemRemoved(position);
    }

    public Filter getFilter() {
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
        TextView contactName;
        TextView contactEmail;
        TextView contactPhone;
        ImageView contactImage;
        View view;
        TextView delete, delete_server;

        ContactViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            delete = itemView.findViewById(R.id.delete);
            delete_server = itemView.findViewById(R.id.delete_server);
            contactName = itemView.findViewById(R.id.person_name);
            contactEmail = itemView.findViewById(R.id.person_email);
            contactImage = itemView.findViewById(R.id.person_photo);
            contactPhone = itemView.findViewById(R.id.person_phone);
        }
    }

}
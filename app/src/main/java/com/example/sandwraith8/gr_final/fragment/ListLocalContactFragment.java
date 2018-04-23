package com.example.sandwraith8.gr_final.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sandwraith8.gr_final.R;

/**
 * Created by sandwraith8 on 21/04/2018.
 */

public class ListLocalContactFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_list_contact, container, false);
    }
}
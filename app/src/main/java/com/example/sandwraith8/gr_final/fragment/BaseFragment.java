package com.example.sandwraith8.gr_final.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sandwraith8 on 21/04/2018.
 */

public abstract class BaseFragment extends Fragment {

    private int viewId;

    public BaseFragment(int viewId) {
        this.viewId = viewId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(viewId, container, false);
        init(v);
        restoreView(savedInstanceState);
        return v;
    }

    public void restoreView(Bundle savedInstanceState) {

    }

    public abstract void init(View v);
}

package com.example.sandwraith8.gr_final.fragment;

import android.content.Intent;

/**
 * Created by sandwraith8 on 21/04/2018.
 */

public interface ActivityResultEvent {
    void onListener(int requestCode, int resultCode, Intent data);
}

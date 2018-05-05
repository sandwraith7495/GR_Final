package com.example.sandwraith8.gr_final;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.sandwraith8.gr_final.fragment.ListLocalContact;
import com.example.sandwraith8.gr_final.fragment.ListServerContact;
import com.example.sandwraith8.gr_final.fragment.MainFragment;
import com.example.sandwraith8.gr_final.service.GoogleService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Homepage extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;
    private View headerView;
    private MenuItem login, logout, server_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        mGoogleSignInClient = GoogleService.getInstance().getClient(this);
        NavigationView navigationView = findViewById(R.id.nav_view);
        login = navigationView.getMenu().getItem(4);
        logout = navigationView.getMenu().getItem(3);
        server_list = navigationView.getMenu().getItem(2);

        headerView = navigationView.getHeaderView(0);
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            ((TextView) headerView.findViewById(R.id.account)).setText("Khách");
            login.setVisible(true);
            logout.setVisible(false);
            server_list.setVisible(false);
        }
        else {
            ((TextView) headerView.findViewById(R.id.account)).setText(account.getDisplayName());
            login.setVisible(false);
            logout.setVisible(true);
            server_list.setVisible(true);
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_camera:
                                changeFragment(new MainFragment());
                                break;
                            case R.id.nav_local:
                                changeFragment(new ListLocalContact());
                                break;
                            case R.id.nav_server:
                                changeFragment(new ListServerContact());
                                break;
                            case R.id.nav_logout:
                                mGoogleSignInClient.signOut()
                                        .addOnCompleteListener(Homepage.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                login.setVisible(true);
                                                server_list.setVisible(false);
                                                logout.setVisible(false);
                                                ((TextView) headerView.findViewById(R.id.account)).setText("Khách");
                                            }
                                        });
                                break;
                            case R.id.nav_login:
                                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                                startActivityForResult(signInIntent, RC_SIGN_IN);
                                break;
                        }
                        return true;
                    }
                }
        );
    }

    private void changeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            ((TextView) headerView.findViewById(R.id.account)).setText(task.getResult().getDisplayName());
            login.setVisible(false);
            server_list.setVisible(true);
            logout.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

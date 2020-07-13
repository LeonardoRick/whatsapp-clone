package com.example.whatsapp_clone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.whatsapp_clone.R;
import com.example.whatsapp_clone.fragment.ChatsListFragment;
import com.example.whatsapp_clone.fragment.ContactsListFragment;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseConfig.getAuth();

    private SmartTabLayout smartTabLayout;
    private ViewPager viewPager;

    private MaterialSearchView searchView;

    private static final int CHATS_LIST_FRAGMENT_INDEX = 0;
    private static final int CONTACTS_LIST_FRAGMENT_INDEX = 1;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsApp Clone");
        setSupportActionBar(toolbar);

        configTabs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Config seatch button
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSignOut:
                signOutUser();
                finish();
                break;
            case R.id.menuConfig:
                startActivity(new Intent(this, ConfigActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void configTabs() {
        smartTabLayout = findViewById(R.id.viewPagerTab);
        viewPager = findViewById(R.id.viewPager);

        final FragmentPagerItemAdapter pagerAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), // return a fragment manager
                FragmentPagerItems.with(getApplicationContext())
                    .add("Conversas", ChatsListFragment.class)
                    .add("Contatos", ContactsListFragment.class)
                .create()
        );

        smartTabLayout.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
            }
        });

        viewPager.setAdapter(pagerAdapter);
        smartTabLayout.setViewPager(viewPager);

        configSearchView(pagerAdapter);
    }

    public void configSearchView(final FragmentPagerItemAdapter pageAdapter) {
        searchView = findViewById(R.id.materialSearchViewMain);

        // Listener to searchView (Open and close of bar)
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() { }

            @Override
            public void onSearchViewClosed() {
                // Access Fragment info throw activity
                ChatsListFragment fragment = (ChatsListFragment) pageAdapter.getPage(CHATS_LIST_FRAGMENT_INDEX);
                fragment.updateAdapterWithStartList();

            }
        });


        // Listener to search text box
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Access Fragment info throw activity
                ChatsListFragment fragment = (ChatsListFragment) pageAdapter.getPage(CHATS_LIST_FRAGMENT_INDEX);

                if(newText != null && !newText.isEmpty()) {
                    fragment.searchChats(newText.toLowerCase());
                }
                return true;
            }
        });
    }

    private void signOutUser() {
        try {
            auth.signOut();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
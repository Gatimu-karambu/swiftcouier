package com.swift.swiftcourier;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class Home extends AppCompatActivity implements ViewProfile.OnFragmentInteractionListener{
    private CardView createOrder,viewOrder, cardView_2, cardView_3;
    private BottomNavigationView bottomNavigationView;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        createOrder = findViewById(R.id.createOrder);
        viewOrder = findViewById(R.id.viewOrder);
        cardView_2 = findViewById(R.id.card_view2);
        cardView_3 = findViewById(R.id.card_view3);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setBackgroundTint(getResources().getColorStateList(R.color.x));
        bottomAppBar.setElevation(12f);
        bottomNavigationView.setBackground(null);
        Window window = getWindow();

        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        fab = findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#252a30")));
        fab.setElevation(12f);
        createOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateOderActivity();
            }
        });

        viewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openviewOrdersActivity();
            }
        });

        cardView_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.profilee) {
                    loadFragment(new ViewProfile());
                    return true;
                } else if (itemId == R.id.place) {
                    loadFragment(new ViewProfile());
                    return true;
                }
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    // Navigate back to HomeActivity screen

                }
            }
        });
    }

    private void openCreateOderActivity(){
        Intent intent = new Intent(Home.this, CreateOrder.class);
        startActivity(intent);
    }

    private void openviewOrdersActivity(){
        Intent intent = new Intent(Home.this, ViewOrders.class);
        startActivity(intent);
    }

    private void loadFragment(Fragment fragment) {
        hideAndDisableViews();


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null); // Optional: Add to back stack
        transaction.commit();
    }

    public void hideAndDisableViews() {
        createOrder.setVisibility(View.GONE);
        viewOrder.setVisibility(View.GONE);
        cardView_3.setVisibility(View.GONE);
        cardView_2.setVisibility(View.GONE);
        createOrder.setEnabled(false);
        viewOrder.setEnabled(false);
        cardView_3.setEnabled(false);
        cardView_2.setEnabled(false);

    }

    public void ShowAndEnablebleViews() {
        createOrder.setVisibility(View.VISIBLE);
        viewOrder.setVisibility(View.VISIBLE);
        cardView_3.setVisibility(View.VISIBLE);
        cardView_2.setVisibility(View.VISIBLE);

        createOrder.setEnabled(true);
        viewOrder.setEnabled(true);
        cardView_3.setEnabled(true);
        cardView_2.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check if there are fragments in the back stack
        if (fragmentManager.getBackStackEntryCount() > 0) {
            // Pop all fragments from the back stack
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            // If no fragments are in the back stack, perform the default back press action
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentPaused() {

    }
}
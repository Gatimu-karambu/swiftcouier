package com.swift.swiftcourier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home extends AppCompatActivity {
    private Button createOrder,viewOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        createOrder = findViewById(R.id.createOrder);
        viewOrder = findViewById(R.id.viewOrder);

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

    }

    private void openCreateOderActivity(){
        Intent intent = new Intent(Home.this, CreateOrder.class);
        startActivity(intent);
    }

    private void openviewOrdersActivity(){
        Intent intent = new Intent(Home.this, ViewOrders.class);
        startActivity(intent);
    }
}
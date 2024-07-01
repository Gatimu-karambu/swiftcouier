package com.swift.swiftcourier;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateOrderActivity extends AppCompatActivity {
    private EditText orderDescription;
    private Button createOrderButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        dbHelper = new DatabaseHelper(this);

        orderDescription = findViewById(R.id.orderDescription);
        createOrderButton = findViewById(R.id.createOrderButton);

        createOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrder();
            }
        });
    }

  private void createOrder() {
        String description = orderDescription.getText().toString().trim();
        // Assuming userId is obtained from login session
        int userId = 1; // Replace with actual user ID after implementing user session management

        if (description.isEmpty()) {
            Toast.makeText(CreateOrderActivity.this, "Please enter order description", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ORDER_USER_ID, userId);
        values.put(DatabaseHelper.COLUMN_ORDER_DESCRIPTION, description);
        values.put(DatabaseHelper.COLUMN_ORDER_STATUS, "created");

        long orderId = db.insert(DatabaseHelper.TABLE_ORDERS, null, values);
        if (orderId != -1) {
            Toast.makeText(CreateOrderActivity.this, "Order Created Successfully", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity
        } else {
            Toast.makeText(CreateOrderActivity.this, "Failed to create order", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.swift.swiftcourier;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OrderManagement extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView orderDescription;
    private TextView orderStatus;
    private EditText editTextOrderId;
    private EditText editTextDescription;
    private EditText editTextStatus;
    private Button updateOrderButton;
    private Button deleteOrderButton;
    private Button saveOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        dbHelper = new DatabaseHelper(this);

        orderDescription = findViewById(R.id.orderDescription);
        orderStatus = findViewById(R.id.orderStatus);
        editTextOrderId = findViewById(R.id.editTextOrderId);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextStatus = findViewById(R.id.editTextStatus);
        updateOrderButton = findViewById(R.id.updateOrderButton);
        deleteOrderButton = findViewById(R.id.deleteOrderButton);
        saveOrderButton = findViewById(R.id.saveOrderButton);

        updateOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditFields();
            }
        });

        deleteOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOrder();
            }
        });

        saveOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrder();
            }
        });
    }

    private void showEditFields() {
        editTextOrderId.setVisibility(View.VISIBLE);
        editTextDescription.setVisibility(View.VISIBLE);
        editTextStatus.setVisibility(View.VISIBLE);
        saveOrderButton.setVisibility(View.VISIBLE);
    }

    private void updateOrder() {
        try {
            long orderId = Long.parseLong(editTextOrderId.getText().toString());
            String description = editTextDescription.getText().toString();
            String status = editTextStatus.getText().toString();

            boolean result = dbHelper.updateOrder(orderId, description, status);

            if (result) {
                Toast.makeText(this, "Order updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Order update failed", Toast.LENGTH_SHORT).show();
            }

            hideEditFields();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteOrder() {
        try {
            long orderId = Long.parseLong(editTextOrderId.getText().toString());

            boolean result = dbHelper.deleteOrder(orderId);

            if (result) {
                Toast.makeText(this, "Order deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Order deletion failed", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideEditFields() {
        editTextOrderId.setVisibility(View.GONE);
        editTextDescription.setVisibility(View.GONE);
        editTextStatus.setVisibility(View.GONE);
        saveOrderButton.setVisibility(View.GONE);
    }
}
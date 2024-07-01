package com.swift.swiftcourier;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class OrderListActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrdersAdapter ordersAdapter;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        loadOrders();
    }

    private void loadOrders() {
        Cursor cursor = db.query("orders", null, null, null, null, null, null);
        ordersAdapter = new OrdersAdapter(cursor);
        ordersRecyclerView.setAdapter(ordersAdapter);
    }

    private class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

        private Cursor cursor;

        OrdersAdapter(Cursor cursor) {
            this.cursor = cursor;
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_order_list, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            if (cursor.moveToPosition(position)) {
                String orderDetails = cursor.getString(cursor.getColumnIndex("details"));
                final int orderId = cursor.getInt(cursor.getColumnIndex("_id"));

                holder.orderDetails.setText(orderDetails);

                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(orderId);
                    }
                });

                holder.updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OrderListActivity.this, OrderListActivity.class);
                        intent.putExtra("ORDER_ID", orderId);
                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {

            TextView orderDetails;
            Button deleteButton;
            Button updateButton;

            OrderViewHolder(View itemView) {
                super(itemView);
                orderDetails = itemView.findViewById(R.id.orderDetails);
                deleteButton = itemView.findViewById(R.id.deleteButton);
                updateButton = itemView.findViewById(R.id.updateButton);
            }
        }
    }

    private void deleteOrder(int orderId) {
        db.delete("orders", "_id = ?", new String[]{String.valueOf(orderId)});
        Toast.makeText(this, "Order deleted", Toast.LENGTH_SHORT).show();
        loadOrders(); // Refresh the list
    }
}

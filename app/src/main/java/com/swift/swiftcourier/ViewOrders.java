package com.swift.swiftcourier;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewOrders extends AppCompatActivity {
    private static final String TAG = "ViewOrders";
    private ListView listViewOrders;
    private List<JSONObject> orderList;
    private OrdersAdapter ordersAdapter;
    private RequestQueue requestQueue;
    private String savedOrderId;
    private OrderDBHelper dbHelper;
    private static final String URL = "https://0cb2e181574cd319a3d4c0d246233991.serveo.net/get_orders"; // Replace with your PC's IP
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);



        listViewOrders = findViewById(R.id.listViewOrders);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        orderList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(this, orderList);
        listViewOrders.setAdapter(ordersAdapter);

        requestQueue = Volley.newRequestQueue(this);
        SharedPreferences sharedPreferences = getSharedPreferences("OrderPrefs", MODE_PRIVATE);
        savedOrderId = sharedPreferences.getString("saved_order_id", null);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchOrders();
            }
        });

        dbHelper = new OrderDBHelper(this);

        fetchOrders();
    }

    private void fetchOrders() {

        swipeRefreshLayout.setRefreshing(true);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Orders fetched successfully. Response: " + response.toString());
                        parseOrders(response);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching orders", error);
                        Toast.makeText(ViewOrders.this, "Error fetching orders", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String credentials = "panel:panel234";
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }
        };

        requestQueue.add(jsonArrayRequest);
    }

    private void parseOrders(JSONArray jsonArray) {
        orderList.clear();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject order = jsonArray.getJSONObject(i);
                String orderId = order.getString("order_id");
                if (dbHelper.isOrderIdPresent(orderId)) {
                    orderList.add(order);
                }
            }
            ordersAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
            Toast.makeText(this, "Error parsing order data", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.swift.swiftcourier;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Map;

public class ViewOrders extends AppCompatActivity {
    private static final String TAG = "ViewOrders";
    private ListView listViewOrders;
    private ArrayList<String> orderList;
    private ArrayAdapter<String> adapter;
    private RequestQueue requestQueue;
    private static final String URL = "http://192.168.189.118:5000/get_orders"; // Replace with your PC's IP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        listViewOrders = findViewById(R.id.listViewOrders);
        orderList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderList);
        listViewOrders.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        fetchOrders();
    }

    private void fetchOrders() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Orders fetched successfully. Response: " + response.toString());
                        parseOrders(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching orders", error);
                        Toast.makeText(ViewOrders.this, "Error fetching orders", Toast.LENGTH_SHORT).show();
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
                String orderString = "Order ID: " + order.getString("order_id") +
                        "\nFrom: " + order.getString("order_from") +
                        "\nTo: " + order.getString("order_to") +
                        "\nStatus: " + order.getString("status");
                orderList.add(orderString);
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
            Toast.makeText(this, "Error parsing order data", Toast.LENGTH_SHORT).show();
        }
    }
}
package com.swift.swiftcourier;


import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateOrder extends AppCompatActivity {
    private EditText etOrderFrom, etOrderTo,etReceiverPhone;
    private Button btnCreateOrder;
    private RequestQueue requestQueue;
    private static final String URL = "http://192.168.189.118:5000/add_order"; // Assumes you're using Android emulator

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        etOrderFrom = findViewById(R.id.etOrderFrom);
        etOrderTo = findViewById(R.id.etOrderTo);
        etReceiverPhone = findViewById(R.id.rp);
        btnCreateOrder = findViewById(R.id.btnCreateOrder);
        requestQueue = Volley.newRequestQueue(this);

        btnCreateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrder();
                finish();
            }
        });
    }

    private static final String TAG = "CreateOrder";

    private void createOrder() {
        String orderFrom = etOrderFrom.getText().toString().trim();
        String orderTo = etOrderTo.getText().toString().trim();
        String receiverPhone = etReceiverPhone.getText().toString().trim(); // New field
        String orderId = generateOrderId();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("order_from", orderFrom);
            jsonBody.put("order_to", orderTo);
            jsonBody.put("receiver_phone", receiverPhone); // New field
            jsonBody.put("order_id", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Sending request to URL: " + URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Order created successfully. Response: " + response.toString());
                        Toast.makeText(CreateOrder.this, "Order created successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error creating order", error);
                        String errorMessage = "Error creating order";
                        if (error.networkResponse != null) {
                            errorMessage += " - Status Code: " + error.networkResponse.statusCode;
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                Log.e(TAG, "Error response body: " + responseBody);
                                errorMessage += ", Response: " + responseBody;
                            } catch (UnsupportedEncodingException e) {
                                Log.e(TAG, "Error reading error response body", e);
                            }
                        } else if (error.getCause() != null) {
                            errorMessage += " - Cause: " + error.getCause().getMessage();
                        }
                        Toast.makeText(CreateOrder.this, errorMessage, Toast.LENGTH_LONG).show();
                    }


                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                // Add Basic Auth header
                String credentials = "panel:panel234";
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", auth);

                return headers;
            }

        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
        Log.d(TAG, "Request added to queue");


    }

    private String generateOrderId() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}


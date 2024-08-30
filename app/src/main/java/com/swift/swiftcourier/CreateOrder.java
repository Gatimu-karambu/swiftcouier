package com.swift.swiftcourier;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private EditText etOrderFrom, etOrderTo, etReceiverPhone;
    private Button btnCreateOrder;
    private RequestQueue requestQueue;
    private static final String URL = "https://0cb2e181574cd319a3d4c0d246233991.serveo.net/add_order"; // Assumes you're using Android emulator
    private OrderDBHelper dbHelper;
    private ProgressBar progressBar;

    // Define a Map to store distances between counties
    private Map<String, Map<String, Integer>> countyDistances = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        etOrderFrom = findViewById(R.id.etOrderFrom);
        etOrderTo = findViewById(R.id.etOrderTo);
        etReceiverPhone = findViewById(R.id.rp);
        btnCreateOrder = findViewById(R.id.btnCreateOrder);
        progressBar = findViewById(R.id.progressBar);
        requestQueue = Volley.newRequestQueue(this);


        // Populate the countyDistances Map with distances between all Kenyan counties
        populateCountyDistances();

        btnCreateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateAndDisplayPrice();
            }
        });
        dbHelper = new OrderDBHelper(this);
    }

    private static final String TAG = "CreateOrder";

    private void populateCountyDistances() {
        String[] counties = {"Nairobi", "Kisumu", "Mombasa", "Nakuru", "Eldoret", "Nyeri", "Meru", "Kisii", "Kitui", "Machakos", "Kakamega", "Uasin Gishu", "Bungoma", "Kilifi", "Kwale", "Garissa", "Mandera", "Wajir", "Marsabit", "Isiolo", "Lamu", "Tana River", "Taita Taveta", "Baringo", "Bomet", "Busia", "Elgeyo-Marakwet", "Homa Bay", "Kajiado", "Kericho", "Kiambu", "Kirinyaga", "Laikipia", "Migori", "Muranga", "Nandi", "Narok", "Nyamira", "Samburu", "Siaya", "Trans Nzoia", "Turkana", "Vihiga", "West Pokot"};

        for (String county1 : counties) {
            Map<String, Integer> distancesForCounty1 = new HashMap<>();
            for (String county2 : counties) {
                int distance = calculateDistance(county1, county2);
                distancesForCounty1.put(county2, distance);
            }
            countyDistances.put(county1, distancesForCounty1);
        }
    }

    private int calculateDistance(String county1, String county2) {
        // Implement your logic to calculate the distance between two counties
        // This is just a simple example, you can use a more sophisticated approach
        int distance = Math.abs(county1.hashCode() - county2.hashCode()) % 500 + 50;
        return distance;
    }

    private void calculateAndDisplayPrice() {
        String orderFrom = etOrderFrom.getText().toString().trim();
        String orderTo = etOrderTo.getText().toString().trim();
        String receiverPhone = etReceiverPhone.getText().toString().trim();

        if (orderFrom.isEmpty() || orderTo.isEmpty() || receiverPhone.isEmpty()) {
            Toast.makeText(CreateOrder.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int price = calculatePrice(orderFrom, orderTo);

        if (price == -1) {
            Toast.makeText(CreateOrder.this, "Route not found. Please check your input.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_order, null);

        // Set the price message in the dialog
        TextView tvDialogMessage = dialogView.findViewById(R.id.tvDialogMessage);
        tvDialogMessage.setText("The shipment price is KES " + price + ". Do you want to confirm the order?");

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateOrder.this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Handle the confirm button click
        dialogView.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the confirmation message
                EditText etConfirmationMessage = dialogView.findViewById(R.id.etConfirmationMessage);
                String confirmationMessage = etConfirmationMessage.getText().toString().trim();

                // Check if the confirmation message is empty
                if (confirmationMessage.isEmpty()) {
                    etConfirmationMessage.setError("Confirmation message is required");
                    etConfirmationMessage.requestFocus();
                    return;
                }

                // Show the progress bar
                progressBar.setVisibility(View.VISIBLE);

                // Simulate a 3-second loading
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Create the order with the price and confirmation message
                        createOrder(price, confirmationMessage);

                        // Hide the progress bar
                        progressBar.setVisibility(View.GONE);

                        dialog.dismiss();
                    }
                }, 3000);
            }
        });

        // Handle the cancel button click
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private int calculatePrice(String orderFrom, String orderTo) {
        // Check if the route exists in the countyDistances Map
        if (countyDistances.containsKey(orderFrom) && countyDistances.get(orderFrom).containsKey(orderTo)) {
            int distance = countyDistances.get(orderFrom).get(orderTo);
            return distance * 2; // Price is 2 KES per km
        } else if (countyDistances.containsKey(orderTo) && countyDistances.get(orderTo).containsKey(orderFrom)) {
            int distance = countyDistances.get(orderTo).get(orderFrom);
            return distance * 2; // Price is 2 KES per km
        } else {
            return -1; // Route not found
        }
    }

    private void createOrder(int price, String confirmationMessage) {
        String orderFrom = etOrderFrom.getText().toString().trim();
        String orderTo = etOrderTo.getText().toString().trim();
        String receiverPhone = etReceiverPhone.getText().toString().trim(); // New field

        if (orderFrom.isEmpty() || orderTo.isEmpty() || receiverPhone.isEmpty()) {
            Toast.makeText(CreateOrder.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderId = generateOrderId();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("order_from", orderFrom);
            jsonBody.put("order_to", orderTo);
            jsonBody.put("receiver_phone", receiverPhone); // New field
            jsonBody.put("order_id", orderId);
            jsonBody.put("price", price); // Add price to the JSON body
            jsonBody.put("confirmation_message", confirmationMessage); // Add confirmation message to the JSON body
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

                        // Save the order_id to SQLite database
                        dbHelper.addOrderId(orderId);

                        finish();
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

    private void saveOrderId(String orderId) {
        SharedPreferences sharedPreferences = getSharedPreferences("OrderPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saved_order_id", orderId);
        editor.apply();
    }
}
package com.swift.swiftcourier;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter messageAdapter;
    private List<Message> messageList;
    private EditText editTextMessage;
    private Button buttonSend;
    private UserDbHelper dbHelper;
    //private String currentUserId = "user"; // Replace with actual user ID
    private String currentUserId;


    private static final int POLLING_INTERVAL = 2000; // 2 seconds
    private boolean isPolling = false;
    private Runnable pollingTask;
    private Handler handler;
    private long lastFetchTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        dbHelper = new UserDbHelper(this);


        loadExistingProfile();


        recyclerView = findViewById(R.id.recycler_view);
        editTextMessage = findViewById(R.id.edit_text_message);
        buttonSend = findViewById(R.id.button_send);

        messageList = new ArrayList<>();
        messageAdapter = new ChatAdapter(messageList, currentUserId);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchMessages();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextMessage.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    sendMessage(messageText);
                }
            }
        });

        handler = new Handler();
        pollingTask = new Runnable() {
            @Override
            public void run() {
                fetchMessages();
                if (isPolling) {
                    handler.postDelayed(this, POLLING_INTERVAL);
                }
            }
        };
    }
    private void fetchMessages() {
        String url = "https://4b725af098b9d02ba023c04b3afd6e33.serveo.net/get_messages?user=" + currentUserId + "&since=" + lastFetchTime;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                        // Your existing code for handling the response
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject messageObject = response.getJSONObject(i);
                                    String sender = messageObject.getString("sender");
                                    String receiver = messageObject.getString("receiver");
                                    String messageText = messageObject.getString("message");
                                    String timestamp = messageObject.getString("timestamp");
                                    long timestampMillis = messageObject.getLong("timestamp_millis");

                                    Message newMessage = new Message(sender, receiver, messageText, timestamp);
                                    messageAdapter.addMessage(newMessage);
                                    lastFetchTime = Math.max(lastFetchTime, timestampMillis);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            recyclerView.scrollToPosition(messageList.size() - 1);
                        }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (statusCode == 502) {
                                // Handle 502 Bad Gateway error
                                Toast.makeText(ChatActivity.this, "Server is currently unavailable. Please try again later.", Toast.LENGTH_LONG).show();
                            } else {
                                // Handle other errors
                                Toast.makeText(ChatActivity.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle network connectivity issues
                            Toast.makeText(ChatActivity.this, "No internet connection. Please check your network settings.", Toast.LENGTH_SHORT).show();
                        }
                        error.printStackTrace();
                    }
                }
        );

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }


    private void sendMessage(String messageText) {
        String url = "https://4b725af098b9d02ba023c04b3afd6e33.serveo.net/send_message";

        JSONObject messageData = new JSONObject();
        try {
            messageData.put("sender", currentUserId);
            messageData.put("receiver", "admin");
            messageData.put("message", messageText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                messageData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        editTextMessage.setText("");
                        fetchMessages();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


    @Override
    protected void onResume() {
        super.onResume();
        startPolling();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPolling();
    }

    private void startPolling() {
        isPolling = true;
        handler.post(pollingTask);
    }

    private void stopPolling() {
        isPolling = false;
        handler.removeCallbacks(pollingTask);
    }


    private void loadExistingProfile() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {

                UserDbHelper.COLUMN_PHONE

        };

        Cursor cursor = db.query(
                UserDbHelper.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            currentUserId = (cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_PHONE)));


        }
        cursor.close();
    }
}



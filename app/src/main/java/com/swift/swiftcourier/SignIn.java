package com.swift.swiftcourier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignIn extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_FIRST_TIME = "firstTime";

    LottieAnimationView lottieAnimationView;
    private View lottieOverlay;
    private TextInputEditText usernameEditText, passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        TextInputLayout passwordInputLayout = findViewById(R.id.passssword);
        passwordInputLayout.setEndIconTintList(ColorStateList.valueOf(Color.BLACK));

        lottieAnimationView=findViewById(R.id.lottieAnimationView);
        lottieOverlay = findViewById(R.id.lottieOverlay);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);


        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Validate login
                if (validateLogin(username, password)) {
                    // Navigate to MainActivity
                    lottieOverlay.setVisibility(View.VISIBLE);

                    // Start playing the animation
                    lottieAnimationView.playAnimation();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean(KEY_FIRST_TIME, false);
                            editor.apply();
                            lottieOverlay.setVisibility(View.GONE);

                            // Stop the animation
                            lottieAnimationView.cancelAnimation();
                            /*Intent intent = new Intent(SignIn.this, MainActivity.class);
                            startActivity(intent);
                            finish();*/
                        }
                    },5000); // Close the login activity
                } else {
                    // Display an error message
                    Toast.makeText(SignIn.this, "Invalid phone number or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateLogin(String username, String password) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_PASSWORD};
        String selection = DatabaseHelper.COLUMN_USERNAME + " = ? AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean isValid = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return isValid;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
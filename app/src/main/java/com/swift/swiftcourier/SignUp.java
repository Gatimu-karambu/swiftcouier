package com.swift.swiftcourier;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUp extends AppCompatActivity {
    private TextInputEditText emailEditText, passwordEditText, confirmPasswordEditText;
    private TextInputLayout passwordInputLayout, confirmPasswordInputLayout;
    LottieAnimationView lottieAnimationView;
    private View lottieOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        lottieOverlay = findViewById(R.id.lottieOverlay);

        Button newUser = findViewById(R.id.new_user);
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });

        passwordInputLayout = findViewById(R.id.password);
        confirmPasswordInputLayout = findViewById(R.id.confirm_password);
        passwordInputLayout.setEndIconTintList(ColorStateList.valueOf(Color.BLACK));
        confirmPasswordInputLayout.setEndIconTintList(ColorStateList.valueOf(Color.BLACK));

        emailEditText = findViewById(R.id.username_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edittext);

        // Add a TextWatcher to passwordEditText to provide real-time feedback on password strength
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                passwordInputLayout.setHelperText(getPasswordStrength(password));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!username.contains("@gmail.com") && !username.contains("@yahoo.com")) {
                    Toast.makeText(SignUp.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                } else if (!isPasswordStrong(password)) {
                    Toast.makeText(SignUp.this, "Password is not strong enough", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseHelper dbHelper = new DatabaseHelper(SignUp.this);

                    if (usernameExists(dbHelper, username)) {
                        Toast.makeText(SignUp.this, "Username is already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        ContentValues values = new ContentValues();
                        values.put(DatabaseHelper.COLUMN_USERNAME, username);
                        values.put(DatabaseHelper.COLUMN_PASSWORD, password);

                        db.insert(DatabaseHelper.TABLE_USERS, null, values);
                        db.close();

                        lottieOverlay.setVisibility(View.VISIBLE);
                        lottieAnimationView.playAnimation();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                lottieOverlay.setVisibility(View.GONE);
                                lottieAnimationView.cancelAnimation();
                                Intent intent = new Intent(SignUp.this, userprofile.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 5000);
                    }
                }
            }
        });
    }

    private boolean usernameExists(DatabaseHelper dbHelper, String username) {
        // Your logic to check if the username exists in the database
        return false; // Placeholder
    }

    private String getPasswordStrength(String password) {
        int score = 0;

        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) score++;

        switch (score) {
            case 5: return "Very Strong";
            case 4: return "Strong";
            case 3: return "Medium";
            case 2: return "Weak";
            default: return "Very Weak";
        }
    }

    private boolean isPasswordStrong(String password) {
        return getPasswordStrength(password).equals("Strong") || getPasswordStrength(password).equals("Very Strong");
    }
}

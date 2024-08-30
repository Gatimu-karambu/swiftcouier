package com.swift.swiftcourier;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class userprofile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private EditText fullNameEditText, phoneEditText;
    private CircleImageView profilePhotoImageView;
    private Spinner genderSpinner, countySpinner, subCountySpinner, wardSpinner;
    private CheckBox emailCheckBox, smsCheckBox, pushNotificationCheckBox;
    private Button submitButton;
    private ImageView uploadImageButton;

    private UserDbHelper dbHelper;
    private String imagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        initializeViews();
        setupSpinners();
        dbHelper = new UserDbHelper(this);
        loadExistingProfile();

        uploadImageButton.setOnClickListener(v -> showImageSourceDialog());
        submitButton.setOnClickListener(v -> saveUserProfile());
    }

    private void initializeViews() {
        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        profilePhotoImageView = findViewById(R.id.pet_image_view);
        genderSpinner = findViewById(R.id.genderSpinner);
        countySpinner = findViewById(R.id.countySpinner);
        subCountySpinner = findViewById(R.id.subCountySpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        emailCheckBox = findViewById(R.id.emailCheckBox);
        smsCheckBox = findViewById(R.id.smsCheckBox);
        pushNotificationCheckBox = findViewById(R.id.pushNotificationCheckBox);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        submitButton = findViewById(R.id.deleteAccountButton);
    }

    private void setupSpinners() {
        // Populate the gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Populate the county spinner
        ArrayAdapter<CharSequence> countyAdapter = ArrayAdapter.createFromResource(this, R.array.county_array, android.R.layout.simple_spinner_item);
        countyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countySpinner.setAdapter(countyAdapter);

        countySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCounty = countySpinner.getSelectedItem().toString();
                int subCountyArrayId = getSubCountyArrayId(selectedCounty);

                if (subCountyArrayId != 0) {
                    ArrayAdapter<CharSequence> subCountyAdapter = ArrayAdapter.createFromResource(userprofile.this, subCountyArrayId, android.R.layout.simple_spinner_item);
                    subCountyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subCountySpinner.setAdapter(subCountyAdapter);
                    subCountySpinner.setEnabled(true);
                } else {
                    subCountySpinner.setAdapter(null);
                    subCountySpinner.setEnabled(false);
                    wardSpinner.setAdapter(null);
                    wardSpinner.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                subCountySpinner.setEnabled(false);
                wardSpinner.setEnabled(false);
            }
        });

        subCountySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSubCounty = subCountySpinner.getSelectedItem().toString();
                int wardArrayId = getWardArrayId(selectedSubCounty);

                if (wardArrayId != 0) {
                    ArrayAdapter<CharSequence> wardAdapter = ArrayAdapter.createFromResource(userprofile.this, wardArrayId, android.R.layout.simple_spinner_item);
                    wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    wardSpinner.setAdapter(wardAdapter);
                    wardSpinner.setEnabled(true);
                } else {
                    wardSpinner.setAdapter(null);
                    wardSpinner.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                wardSpinner.setEnabled(false);
            }
        });
    }

    private void showImageSourceDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                if (checkCameraPermission()) {
                    openCamera();
                } else {
                    requestCameraPermission();
                }
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera(); // Permission was granted, open the camera
            } else {
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                Uri selectedImage = data.getData();
                imagePath = getRealPathFromURI(selectedImage);
                profilePhotoImageView.setImageURI(selectedImage);
            } else if (requestCode == CAMERA_REQUEST && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imagePath = saveImageToInternalStorage(photo);
                profilePhotoImageView.setImageBitmap(photo);
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        android.content.CursorLoader loader = new android.content.CursorLoader(this, contentUri, proj, null, null, null);
        android.database.Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private String saveImageToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, "profile_" + System.currentTimeMillis() + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    private void saveUserProfile() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(UserDbHelper.COLUMN_NAME, fullNameEditText.getText().toString());
        values.put(UserDbHelper.COLUMN_PHONE, phoneEditText.getText().toString());

        // Add null checks for spinners
        if (genderSpinner.getSelectedItem() != null) {
            values.put(UserDbHelper.COLUMN_GENDER, genderSpinner.getSelectedItem().toString());
        }

        if (countySpinner.getSelectedItem() != null) {
            values.put(UserDbHelper.COLUMN_COUNTY, countySpinner.getSelectedItem().toString());
        }

        if (subCountySpinner.getSelectedItem() != null) {
            values.put(UserDbHelper.COLUMN_SUB_COUNTY, subCountySpinner.getSelectedItem().toString());
        }

        if (wardSpinner.getSelectedItem() != null) {
            values.put(UserDbHelper.COLUMN_WARD, wardSpinner.getSelectedItem().toString());
        }

        values.put(UserDbHelper.COLUMN_EMAIL_NOTIF, emailCheckBox.isChecked() ? 1 : 0);
        values.put(UserDbHelper.COLUMN_SMS_NOTIF, smsCheckBox.isChecked() ? 1 : 0);
        values.put(UserDbHelper.COLUMN_PUSH_NOTIF, pushNotificationCheckBox.isChecked() ? 1 : 0);

        if (imagePath != null) {
            values.put(UserDbHelper.COLUMN_IMAGE_PATH, imagePath);
        }

        // Check if a profile already exists
        Cursor cursor = db.query(UserDbHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            // Update existing profile
            int rowsAffected = db.update(UserDbHelper.TABLE_NAME, values, null, null);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Insert new profile
            long newRowId = db.insert(UserDbHelper.TABLE_NAME, null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();

        Intent intent = new Intent(userprofile.this, Home.class);
        startActivity(intent);
        finish();
    }

    private int getSubCountyArrayId(String county) {
        switch (county) {
            case "Nairobi":
                return R.array.nairobi_sub_county_array;
            case "Mombasa":
                return R.array.mombasa_sub_county_array;
            case "Kisumu":
                return R.array.kisumu_sub_county_array;
            // Add more cases for other counties
            default:
                return 0;
        }
    }

    private int getWardArrayId(String subCounty) {
        switch (subCounty) {
            case "Westlands":
                return R.array.westlands_ward_array;
            case "Lang'ata":
                return R.array.langata_ward_array;
            case "Dagoretti":
                return R.array.dagoretti_ward_array;
            case "Nyali":
                return R.array.nyali_ward_array;
            case "Kisauni":
                return R.array.kisauni_ward_array;
            case "Kisumu East":
                return R.array.kisumu_east_ward_array;
            case "Kisumu West":
                return R.array.kisumu_west_ward_array;
            // Add more cases for other sub-counties
            default:
                return 0;
        }
    }

    private void loadExistingProfile() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                UserDbHelper.COLUMN_NAME,
                UserDbHelper.COLUMN_PHONE,
                UserDbHelper.COLUMN_GENDER,
                UserDbHelper.COLUMN_COUNTY,
                UserDbHelper.COLUMN_SUB_COUNTY,
                UserDbHelper.COLUMN_WARD,
                UserDbHelper.COLUMN_EMAIL_NOTIF,
                UserDbHelper.COLUMN_SMS_NOTIF,
                UserDbHelper.COLUMN_PUSH_NOTIF,
                UserDbHelper.COLUMN_IMAGE_PATH
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
            fullNameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_NAME)));
            phoneEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_PHONE)));

            // Set spinner selections
            setSpinnerSelection(genderSpinner, cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_GENDER)));
            setSpinnerSelection(countySpinner, cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_COUNTY)));
            setSpinnerSelection(subCountySpinner, cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_SUB_COUNTY)));
            setSpinnerSelection(wardSpinner, cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_WARD)));

            emailCheckBox.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_EMAIL_NOTIF)) == 1);
            smsCheckBox.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_SMS_NOTIF)) == 1);
            pushNotificationCheckBox.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_PUSH_NOTIF)) == 1);

            imagePath = cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_IMAGE_PATH));
            if (imagePath != null && !imagePath.isEmpty()) {
                profilePhotoImageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            }
        }
        cursor.close();
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null && value != null) {
            int position = adapter.getPosition(value);
            spinner.setSelection(position);
        }
    }
}

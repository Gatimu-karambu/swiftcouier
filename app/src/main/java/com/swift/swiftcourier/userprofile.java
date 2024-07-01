package com.swift.swiftcourier;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class userprofile extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, phoneEditText, passwordEditText;
    private ImageView profilePhotoImageView;
    private Spinner genderSpinner, countySpinner, subCountySpinner, wardSpinner;
    private CheckBox emailCheckBox, smsCheckBox, pushNotificationCheckBox;
    private Button  submitButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        genderSpinner = findViewById(R.id.genderSpinner);
        countySpinner = findViewById(R.id.countySpinner);
        subCountySpinner = findViewById(R.id.subCountySpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        emailCheckBox = findViewById(R.id.emailCheckBox);
        smsCheckBox = findViewById(R.id.smsCheckBox);
        pushNotificationCheckBox = findViewById(R.id.pushNotificationCheckBox);
        submitButton = findViewById(R.id.deleteAccountButton);

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

        // Handle profile photo upload




        // Handle delete account
        submitButton.setOnClickListener(v -> {
            // Code to delete account
            Toast.makeText(userprofile.this, "Submitted", Toast.LENGTH_SHORT).show();
        });
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
}

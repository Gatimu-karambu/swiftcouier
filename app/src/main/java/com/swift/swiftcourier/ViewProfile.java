package com.swift.swiftcourier;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfile extends Fragment {

    private CircleImageView profileImageView;
    private TextView fullNameTextView, phoneTextView, genderTextView, countyTextView, subCountyTextView, wardTextView, notificationPreferencesTextView;
    private UserDbHelper dbHelper;
    private Button updateProfile;
    View rootView;

    Home activity;
    private OnFragmentInteractionListener mListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_view_profile, container, false);
        rootView.setClickable(true);
        rootView.setFocusable(true);
        initViews();
        dbHelper = new UserDbHelper(requireContext());
        loadProfileData();
        activity = (Home) getActivity();
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), userprofile.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void initViews() {
        updateProfile = rootView.findViewById(R.id.updateProfile);
        profileImageView = rootView.findViewById(R.id.profileImageView);
        fullNameTextView = rootView.findViewById(R.id.fullNameTextView);
        phoneTextView = rootView.findViewById(R.id.phoneTextView);
        genderTextView = rootView.findViewById(R.id.genderTextView);
        countyTextView = rootView.findViewById(R.id.countyTextView);
        subCountyTextView = rootView.findViewById(R.id.subCountyTextView);
        wardTextView = rootView.findViewById(R.id.wardTextView);
        notificationPreferencesTextView = rootView.findViewById(R.id.notificationPreferencesTextView);
    }





    private void loadProfileData() {
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
            fullNameTextView.setText("Name: " + cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_NAME)));
            phoneTextView.setText("Phone: " + cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_PHONE)));
            genderTextView.setText("Gender: " + cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_GENDER)));
            countyTextView.setText("County: " + cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_COUNTY)));
            subCountyTextView.setText("Sub-County: " + cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_SUB_COUNTY)));
            wardTextView.setText("Ward: " + cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_WARD)));

            StringBuilder notificationPreferences = new StringBuilder("Notifications: ");
            if (cursor.getInt(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_EMAIL_NOTIF)) == 1) {
                notificationPreferences.append("Email ");
            }
            if (cursor.getInt(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_SMS_NOTIF)) == 1) {
                notificationPreferences.append("SMS ");
            }
            if (cursor.getInt(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_PUSH_NOTIF)) == 1) {
                notificationPreferences.append("Push ");
            }
            notificationPreferencesTextView.setText(notificationPreferences.toString());

            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_IMAGE_PATH));
            if (imagePath != null && !imagePath.isEmpty()) {
                profileImageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            }
        }
        cursor.close();
    }

    @Override
    public void onDestroy() {
        activity.ShowAndEnablebleViews();
        super.onDestroy();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListener != null) {
            mListener.onFragmentPaused();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentPaused();
    }
}
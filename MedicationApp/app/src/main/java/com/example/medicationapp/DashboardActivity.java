package com.example.medicationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import static java.lang.Boolean.TRUE;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{

    private SessionHelper sessionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setTitle("Dashboard");

        TextView helloMessage = (TextView) findViewById(R.id.tvHello);

        Button activeDay = (Button) findViewById(R.id.btnActiveDay);
        activeDay.setOnClickListener(this);

        Button monitorProgress = (Button) findViewById(R.id.btnMonitorProgress);
        monitorProgress.setOnClickListener(this);

        Button logout = (Button) findViewById(R.id.btnLogout);
        logout.setOnClickListener(this);

		//Session helper to get first name
        sessionHelper = new SessionHelper(getApplicationContext());

        HashMap<String, String> user = sessionHelper.getUserDetails();
        String firstName = user.get(sessionHelper.FIRST_NAME);

        String helloText = "Hello "+firstName;

        SpannableString ss = new SpannableString(helloText);

        ForegroundColorSpan fscGreen = new ForegroundColorSpan(Color.rgb(131, 173, 36)); //Set name to green
        ss.setSpan(fscGreen, 5, helloText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        helloMessage.setText(ss);

		//Inform user they need an internet connection if non exists
        if (AirplaneModeOn(getApplicationContext()) == TRUE) {
            Toast.makeText(getApplicationContext(), "An internet connection is required to view medication, try turning off airplane mode", Toast.LENGTH_SHORT).show();
        }else if(!isNetwork(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "An internet connection is required to view medication", Toast.LENGTH_SHORT).show();
        }

    }

    public static String getPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnActiveDay:
                Intent intentActiveDay = new Intent(DashboardActivity.this, ActiveDayActivity.class);
                startActivity(intentActiveDay);
                break;

            case R.id.btnMonitorProgress:

                Intent intentMonitorProgress = new Intent(DashboardActivity.this, MonitorProgessActivity.class);
                startActivity(intentMonitorProgress);
                break;

            case R.id.btnLogout:
                sessionHelper.logout();
                Intent intentMainActivity = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(intentMainActivity);
                break;

            default:
                break;
        }
    }

    //For checking internet connection
    public boolean isNetwork(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private static boolean AirplaneModeOn(Context context) {
        // Return true if airplane more is on
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}

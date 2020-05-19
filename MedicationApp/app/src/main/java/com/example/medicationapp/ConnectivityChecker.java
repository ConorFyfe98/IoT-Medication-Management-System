package com.example.medicationapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

import static java.lang.Boolean.TRUE;

public class ConnectivityChecker {

    public void checkConnectivity(Context context){

        if (AirplaneModeOn(context) == TRUE) {
            Toast.makeText(context, "An internet connection is required to view medication, try turning off airplane mode", Toast.LENGTH_SHORT).show();
        }else if(!isNetwork(context)) {
            Toast.makeText(context, "An internet connection is required to view medication", Toast.LENGTH_SHORT).show();
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

}

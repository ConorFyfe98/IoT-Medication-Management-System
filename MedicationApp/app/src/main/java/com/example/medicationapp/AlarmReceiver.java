package com.example.medicationapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver {
    public Context context;
    private int medID;
    private String dateTime, pillOneAmount, pillTwoAmount, pillThreeAmount, status, title, message;

    public void onReceive(Context context, Intent intent) {
        setContext(context);

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            medID = bundle.getInt("MedID");
            dateTime = bundle.getString("dateTime");
            pillOneAmount = bundle.getString("pillOneAmount");
            pillTwoAmount = bundle.getString("pillTwoAmount");
            pillThreeAmount = bundle.getString("pillThreeAmount");
            status = bundle.getString("status");
        }

        //If alarm passed pending status then user must be informed
        if(status.contains("Pending")){
             title = "Medication "+medID+" Due: " +dateTime;
             message = "Pill One: "+pillOneAmount+" Pill Two: "+pillTwoAmount+" Pill Three: "+pillThreeAmount;
        }else{ //If alarm passed other status medication has been missed
            title = "Medication "+medID+ " Missed";
            message = "Medication due at "+dateTime+" was not confirmed";
            try {
                updateDatabase(medID, status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
		//Send notification with title and message
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(title, message);
        notificationHelper.getManager().notify(1, nb.build());
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

	//Update database via API
    private void updateDatabase(int medicationID, String status) throws JSONException {

        final String json = "{\"status\":\""+status+"\",\"medicationID\":\"" + medicationID + "\"}";

        StringRequest request = new StringRequest(Request.Method.POST, "https://mayar.abertay.ac.uk/~1604475/MedWeb/API/updateStatus.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),
                        "An error : " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Data", json);
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(request);
    }


}

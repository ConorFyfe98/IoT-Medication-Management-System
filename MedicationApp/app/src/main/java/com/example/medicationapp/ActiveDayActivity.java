package com.example.medicationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.medicationapp.SampleUtil;
import com.example.medicationapp.SampleUtil.KeyStorePasswordPair;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.util.UUID;

import static com.example.medicationapp.DashboardActivity.getPref;
import static java.lang.Boolean.TRUE;

public class ActiveDayActivity extends AppCompatActivity implements View.OnClickListener {

    // Initiate variables
    private String clientEndpoint, clientId, certificateFile, privateKeyFile;
    private ListView list;
    private ArrayList<String> activeMedication = new ArrayList<String>();
    private ArrayList<String> activeMedicationDateTime = new ArrayList<String>();
    private Button confirmButton;
    private Spinner spinner;
    private TextView activeDayInfo;
    private SessionHelper sessionHelper;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_day);
        setTitle("Active Day");

        sessionHelper = new SessionHelper(getApplicationContext());

        list = (ListView) findViewById(R.id.listMedData);
        confirmButton = (Button) findViewById(R.id.btnConfirm);
        spinner = findViewById(R.id.dropDown);
        activeDayInfo = (TextView) findViewById(R.id.activeDayInfo);

        confirmButton.setOnClickListener(this);

		//Check if airplane mode is on
        if (AirplaneModeOn(getApplicationContext()) == TRUE) {
            Toast.makeText(getApplicationContext(), "An internet connection is required to view medication, try turning off airplane mode", Toast.LENGTH_SHORT).show();
            activeDayInfo.setText("An internet connection is required to view medication, try turning off airplane mode");
        }
        
		//Check if user has an internet connection
		if(!isNetwork(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "An internet connection is required to view medication", Toast.LENGTH_SHORT).show();
            activeDayInfo.setText("An internet connection is required to view medication");
        }

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String MedID = ((TextView) view.findViewById(R.id.tvMedicationID)).getText().toString();
                String dateTime = ((TextView) view.findViewById(R.id.tvDateTime)).getText().toString();
                String pillOneAmount = ((TextView) view.findViewById(R.id.tvPillOne)).getText().toString();
                String pillTwoAmount = ((TextView) view.findViewById(R.id.tvPillTwo)).getText().toString();
                String pillThreeAmount = ((TextView) view.findViewById(R.id.tvPillThree)).getText().toString();
                String status = ((TextView) view.findViewById(R.id.tvStatus)).getText().toString();

				//Retrieve only medication data from list
                MedID = MedID.substring(15);
                dateTime = dateTime.substring(11);
                pillOneAmount = pillOneAmount.substring(10);
                pillTwoAmount = pillTwoAmount.substring(10);
                pillThreeAmount = pillThreeAmount.substring(12);


                //If status is pending then display pop up dialog
                if (status.contains("Pending")) {

                    // Options for dialog
                    String[] options = {"Yes", "Cancel"};
                    
					// Build dialog, set title and items as options
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActiveDayActivity.this);
                    final String finalMedID = MedID;
                    builder.setTitle("Confirm medication " + finalMedID + " as active:");
                    final String finalDateTime = dateTime;
                    final String finalPillOneAmount = pillOneAmount;
                    final String finalPillTwoAmount = pillTwoAmount;
                    final String finalPillThreeAmount = pillThreeAmount;
                    final String finalStatus = status;
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedOption) {
                            // listen for selected item, check selected item and perform appropriate action
                            if (selectedOption == 0) {
                                //Call function to update medication to active, use mqtt and start alarm
                                try {
                                    startAlarm(finalMedID, finalDateTime, finalPillOneAmount, finalPillTwoAmount, finalPillThreeAmount, finalStatus);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else if (selectedOption == 1) {
                                //User selected cancel so do nothing
                            } else {
                                Toast.makeText(ActiveDayActivity.this, "Sorry an error occurred.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    builder.show();


                } else { // Display medication can not be set as active
                    Toast.makeText(getApplicationContext(),
                            "Medication " + MedID + " can not be set active", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

		//If network exists call function to set medication in offline database
        if (isNetwork(getApplicationContext())) {
            try {
                getOfflineMedication();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            confirmButton.setEnabled(false);
            confirmButton.setBackgroundColor(Color.GRAY);
            spinner.setEnabled(false);
        }

        activeDayData();
    }

	 //Convert String to date format
    private Date convertSelectedDateTime(String dateTime) {
        Date convertedDateTime = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            convertedDateTime = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDateTime;
    }


    private void startAlarm(String medicationID, String dateTime, String pillOneAmount, String pillTwoAmount, String pillThreeAmount, String status) throws JSONException {
        //Convert String to Date
		Date pillDateTime = convertSelectedDateTime(dateTime);

        Calendar c = Calendar.getInstance();
        c.setTime(pillDateTime);

        //Create alarm manager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //Class to call and pass extra data
        int medID = Integer.parseInt(medicationID);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("MedID", medID);
        intent.putExtra("dateTime", dateTime);
        intent.putExtra("pillOneAmount", pillOneAmount);
        intent.putExtra("pillTwoAmount", pillTwoAmount);
        intent.putExtra("pillThreeAmount", pillThreeAmount);
        intent.putExtra("status", status);

		//Set medication notification
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), medID, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        Log.i("Alarm", "ALARM set");

		//If internet update database and publish mqtt message, if not add request to offline database
        if (isNetwork(getApplicationContext())) {
            updateDatabase(medicationID, "Active");
            publishMqtt();
        } else if (AirplaneModeOn(getApplicationContext()) == TRUE) {
            Toast.makeText(getApplicationContext(), "No internet connection: Airplane mode is on, medication may be dispensed from device", Toast.LENGTH_SHORT).show();
            addToOfflineDb(medID, dateTime, pillOneAmount, pillTwoAmount, pillThreeAmount);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection: Medication may be dispensed from device", Toast.LENGTH_SHORT).show();
            addToOfflineDb(medID, dateTime, pillOneAmount, pillTwoAmount, pillThreeAmount);
        }
		
		//Set missed medication notification
        c.add(Calendar.MINUTE, 1);
        int missedNotificationID = -medID;
        intent.putExtra("status", "Missed");
        PendingIntent missedPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), missedNotificationID, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), missedPendingIntent);

        activeDayData();

        Toast.makeText(getApplicationContext(),
                "You will receive a notification when it is time to take medication " + medicationID, Toast.LENGTH_SHORT).show();
    }

	//Update database via API
    private void updateDatabase(String medicationID, String status) throws JSONException {

        final String json = "{\"status\":\"" + status + "\",\"medicationID\":\"" + medicationID + "\"}";

        StringRequest request = new StringRequest(Request.Method.POST, "https://mayar.abertay.ac.uk/~1604475/MedWeb/API/updateStatus.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
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
        Volley.newRequestQueue(ActiveDayActivity.this).add(request);
    }


	//Async task to send mqtt message
    private void activeDayData() {
        activeDayDataAsyncTask retrieveTask = new activeDayDataAsyncTask();
        retrieveTask.execute();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

			
            case R.id.btnConfirm:
				//Get medication ID from spinner
                String medicationID = spinner.getSelectedItem().toString();

                int position = spinner.getSelectedItemPosition();
				
				//convert medication string datetime to date
                Date dateTime = convertSelectedDateTime(activeMedicationDateTime.get(position));
				//get current time
                Date currentDateTime = Calendar.getInstance().getTime();
				
				//Add 1 (would be 15 minutes not in prototype) minute to medication date time to get missed time
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateTime);
                cal.add(Calendar.MINUTE, 1);
                Date missedDateTime = cal.getTime();

				//Confirm medication if between medication time and missed medication time
                if (currentDateTime.before(dateTime)) {
                    Toast.makeText(getApplicationContext(),
                            "Medication " + medicationID + " can not be confirmed until " + dateTime, Toast.LENGTH_SHORT).show();
                } else if (currentDateTime.after(missedDateTime)) {
                    Toast.makeText(getApplicationContext(),
                            "Medication " + medicationID + " should have been confirmed between" + dateTime + " till " + missedDateTime, Toast.LENGTH_SHORT).show();
                    activeDayData();
                } else {

                    try {
                        updateDatabase(medicationID, "Taken");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Missed alarm manager ID is - or medicationID
					int missedNotificationID = Integer.parseInt(medicationID);
                    missedNotificationID = -missedNotificationID;
                    //Cancel missed medication alarm and update database
					cancelMissedAlarm(missedNotificationID);
                    activeDayData();
                    
					Toast.makeText(getApplicationContext(),
                            "Medication " + medicationID + " confirmed", Toast.LENGTH_SHORT).show();
                }


                break;

            default:
                break;
        }
    }

	//Cancel missed medication alarm
    private void cancelMissedAlarm(int missedNotificationID) {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent missedIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), missedNotificationID, missedIntent, 0);
        alarmManager.cancel(pendingIntent);
    }

	//Check network connectivity
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

	//Add active day medication requests to offline database
    private void addToOfflineDb(int medID, String dateTime, String pillOneAmount, String pillTwoAmount, String pillThreeAmount) {
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        ArrayList<MedicationData> medDataList = new ArrayList<MedicationData>();

        HashMap<String, String> user = sessionHelper.getUserDetails();
        String userID = user.get(sessionHelper.USERID);

        MedicationData medication = new MedicationData(String.valueOf(medID), userID, dateTime, pillOneAmount, pillTwoAmount, pillThreeAmount, "Pending");

        db.addActiveRequest(medication);
    }

	//Attempt to set medication as active, if time has passed display error
    private void getOfflineMedication() throws JSONException {
        databaseHelper = new DatabaseHelper(getApplicationContext());
        ArrayList<MedicationData> medication = databaseHelper.getActiveRequest();

        Date currentDateTime = Calendar.getInstance().getTime();

        for (int i = 0; i < medication.size(); i++) {

            Date dateTime = convertSelectedDateTime(medication.get(i).dateTime);

            if (dateTime.after(currentDateTime)) {
                publishMqtt();
                Toast.makeText(getApplicationContext(),
                        "Medication " + medication.get(i).medicationID + " set active", Toast.LENGTH_SHORT).show();
                updateDatabase(medication.get(i).medicationID, "Active");
                databaseHelper.removeByID(Integer.valueOf(medication.get(i).medicationID));
                activeDayData();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Medication " + medication.get(i).medicationID + " could not be scheduled as the date/time is in the past", Toast.LENGTH_SHORT).show();
                databaseHelper.removeByID(Integer.valueOf(medication.get(i).medicationID));
            }
        }
    }

	//Request medication from todays from database via API
    private class activeDayDataAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            StringRequest request = new StringRequest(Request.Method.POST, "https://mayar.abertay.ac.uk/~1604475/MedWeb/API/activeDay.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String medicationID, pillOneAmount, pillTwoAmount, pillThreeAmount, dateTime, status;

                            ArrayList<MedicationData> medDataList = new ArrayList<MedicationData>();
							
							//if no medication display message
                            try {
                                JSONArray medData = new JSONArray(response);
                                if (medData.toString().contains("error")) {

                                    Toast.makeText(getApplicationContext(),
                                            "No medication scheduled for today", Toast.LENGTH_SHORT).show();

                                    confirmButton.setEnabled(false);
                                    confirmButton.setBackgroundColor(Color.GRAY);
                                    spinner.setEnabled(false);

                                } else {
                                    activeMedication.clear();
                                    activeMedicationDateTime.clear();
                                    int medDataSize = medData.length();

                                    HashMap<String, String> user = sessionHelper.getUserDetails();
                                    String userID = user.get(sessionHelper.USERID);
									
									//add medication data to a list
                                    for (int i = 0; i < medDataSize; i++) {
                                        JSONObject row = medData.getJSONObject(i);
                                        medicationID = row.getString("medicationID");
                                        dateTime = row.getString("dateTime");
                                        pillOneAmount = row.getString("pillOneAmount");
                                        pillTwoAmount = row.getString("pillTwoAmount");
                                        pillThreeAmount = row.getString("pillThreeAmount");
                                        status = row.getString("status");

                                        MedicationData medication = new MedicationData(medicationID, userID, dateTime, pillOneAmount, pillTwoAmount, pillThreeAmount, status);

                                        medDataList.add(medication);
										
										//Create list of active medication ID and time
                                        if (status.contains("Active")) {
                                            activeMedication.add(medicationID);
                                            activeMedicationDateTime.add(dateTime);
                                        }
                                    }
									//Display medication data in listview
                                    MedicationDataAdapter adapter = new MedicationDataAdapter(getApplicationContext(), medDataList);
                                    list.setAdapter(adapter);


                                    if (activeMedication.isEmpty()) {
                                        //Disable button and change colour
                                        confirmButton.setEnabled(false);
                                        confirmButton.setBackgroundColor(Color.GRAY);

                                        //Set list to empty and disable spinner
                                        activeMedication.add("");
                                        ArrayAdapter<String> listAdapter;
                                        listAdapter = new ArrayAdapter<String>(ActiveDayActivity.this, R.layout.support_simple_spinner_dropdown_item, activeMedication);
                                        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinner.setAdapter(listAdapter);

                                        spinner.setEnabled(false);
                                    } else {
                                        //Change button colour, enable button and spinner
                                        confirmButton.setEnabled(true);
                                        spinner.setEnabled(true);
                                        confirmButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                                        //Populate spinner with medication IDs of active medication
                                        ArrayAdapter<String> listAdapter;
                                        listAdapter = new ArrayAdapter<String>(ActiveDayActivity.this, R.layout.support_simple_spinner_dropdown_item, activeMedication);
                                        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinner.setAdapter(listAdapter);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    HashMap<String, String> user = sessionHelper.getUserDetails();
                    String userID = user.get(sessionHelper.USERID);

                    Map<String, String> params = new HashMap<>();
                    params.put("userID", userID);
                    return params;
                }
            };
            Volley.newRequestQueue(ActiveDayActivity.this).add(request);
            return null;
        }
    }

	//Publish active message to Raspberry Pi via AWS IoT Core
    private void publishMqtt() {
        publishMqttAsyncTask publishTask = new publishMqttAsyncTask();
        publishTask.execute();
    }


    private class publishMqttAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            clientEndpoint = "asxxquscsq3dr.iot.us-east-1.amazonaws.com";
            clientId = "thingclient1";                           

            certificateFile = getFilesDir() + "/iot-cert.pem.crt";
            privateKeyFile = getFilesDir() + "/private-key.pem.key";

            Log.i("Keys", certificateFile);
            Log.i("Keys", privateKeyFile);


            KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
            AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);


            try {
                client.connect();
                Log.i("MQTT", "CONNECTED");
                String topic = "MedicationSystem";
                String payload = "Active Day";

                client.publish(topic, AWSIotQos.QOS0, payload);

            } catch (AWSIotException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}



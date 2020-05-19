package com.example.medicationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.medicationapp.DashboardActivity.getPref;
import static java.lang.Boolean.TRUE;

public class MonitorProgessActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String statusFilter= "All";
    private ListView list;

    private SessionHelper sessionHelper;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        setTitle("Monitor Progress");

        list = (ListView)findViewById(R.id.listMedData);

        Spinner spinner = findViewById(R.id.dropDown);
        ArrayAdapter<CharSequence> statusAdapter  = ArrayAdapter.createFromResource(this, R.array.status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(statusAdapter);
        spinner.setOnItemSelectedListener(this);

		//Inform user they need an intenet connection if non exists
        if (AirplaneModeOn(getApplicationContext()) == TRUE) {
            Toast.makeText(getApplicationContext(), "An internet connection is required to view medication, try turning off airplane mode", Toast.LENGTH_SHORT).show();
        }else if(!isNetwork(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "An internet connection is required to view medication", Toast.LENGTH_SHORT).show();
        }else{
            retrieveData();
        }
    }

	//Retrieve medication from database via API
    private void retrieveData(){
        StringRequest request = new StringRequest(Request.Method.POST, "https://mayar.abertay.ac.uk/~1604475/MedWeb/API/display.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String medicationID, pillOneAmount, pillTwoAmount, pillThreeAmount, dateTime, status;

                        ArrayList<MedicationData> medDataList = new ArrayList<MedicationData>();

                        HashMap<String, String> user = sessionHelper.getUserDetails();
                        String userID = user.get(sessionHelper.USERID);

						//Display medication data in listview
                        try {
                            JSONArray medData = new JSONArray(response);
                            int medDataSize = medData.length();
                            for(int i =0; i <medDataSize; i++){
                                JSONObject row = medData.getJSONObject(i);
                                medicationID = row.getString("medicationID");
                                dateTime = row.getString("dateTime");
                                pillOneAmount = row.getString("pillOneAmount");
                                pillTwoAmount = row.getString("pillTwoAmount");
                                pillThreeAmount = row.getString("pillThreeAmount");
                                status = row.getString("status");

                                MedicationData medication = new MedicationData(medicationID, userID, dateTime, pillOneAmount, pillTwoAmount, pillThreeAmount, status);
                                medDataList.add(medication);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        MedicationDataAdapter adapter = new MedicationDataAdapter(getApplicationContext(), medDataList);
                        list.setAdapter(adapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "An error occurred: "+error,Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
				
				//Get userID and authorisation from session helper
                sessionHelper = new SessionHelper(getApplicationContext());

                HashMap<String, String> user = sessionHelper.getUserDetails();
                String userID = user.get(sessionHelper.USERID);
                String authorisation = user.get(sessionHelper.AUTHORISATION);

				//Pass parameters
                Map<String,String> params = new HashMap<>();
                params.put("app", "true");
                params.put("numberRows", "All");
                params.put("orderBy", "Latest");
                params.put("medStatus", statusFilter);
                params.put("requestFunction", authorisation);
                params.put("userID", userID);

                return params;
            }
        };
        Volley.newRequestQueue(MonitorProgessActivity.this).add(request);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Refresh medication data when filter option selected
		String option = parent.getItemAtPosition(position).toString();
        statusFilter = option;
        retrieveData();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

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

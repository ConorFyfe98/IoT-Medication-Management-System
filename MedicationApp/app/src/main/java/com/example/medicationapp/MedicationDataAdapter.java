package com.example.medicationapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class MedicationDataAdapter extends ArrayAdapter<MedicationData> {
    public MedicationDataAdapter (Context context, ArrayList<MedicationData> medDataList){
        super(context, 0, medDataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the sms item for this position.
        MedicationData medDataList = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.meddata_layout, parent, false);
        }
        // Initialise views.
        TextView medicationID = (TextView) convertView.findViewById(R.id.tvMedicationID);
        TextView dateTime = (TextView) convertView.findViewById(R.id.tvDateTime);
        TextView pillOneAmount = (TextView) convertView.findViewById(R.id.tvPillOne);
        TextView pillTwoAmount = (TextView) convertView.findViewById(R.id.tvPillTwo);
        TextView pillThreeAmount = (TextView) convertView.findViewById(R.id.tvPillThree);
        TextView status = (TextView) convertView.findViewById(R.id.tvStatus);

        // set values of text views
        medicationID.setText("Medication ID: "+medDataList.medicationID);
        dateTime.setText("Date/Time: "+medDataList.dateTime);
        pillOneAmount.setText("Pill one: "+medDataList.pillOneAmount);
        pillTwoAmount.setText("Pill two: "+medDataList.pillTwoAmount);
        pillThreeAmount.setText("Pill three: "+medDataList.pillThreeAmount);
        status.setText("Status: "+medDataList.status);

        // Return view to display on users screen
        return convertView;
    }
}

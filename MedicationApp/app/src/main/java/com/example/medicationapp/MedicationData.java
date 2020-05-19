package com.example.medicationapp;

public class MedicationData {

    public String medicationID="";
    public String userID ="";
    public String dateTime ="";
    public String pillOneAmount="";
    public String pillTwoAmount="";
    public String pillThreeAmount="";
    public String status ="";

    MedicationData(String mid, String uid, String dt, String pOne, String pTwo, String pThree, String s){
        medicationID = mid;
        userID = uid;
        dateTime = dt;
        pillOneAmount = pOne;
        pillTwoAmount = pTwo;
        pillThreeAmount = pThree;
        status = s;
    }
}

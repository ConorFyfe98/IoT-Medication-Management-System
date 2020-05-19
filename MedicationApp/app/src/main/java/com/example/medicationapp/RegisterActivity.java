package com.example.medicationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstName, lastName, email, password, confirmPassword;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstName = (EditText)findViewById(R.id.etFirstName);
        lastName = (EditText)findViewById(R.id.etLastName);
        email = (EditText)findViewById(R.id.etEmail);
        password = (EditText)findViewById(R.id.etPassword);
        confirmPassword = (EditText)findViewById(R.id.etConfirmPassword);
        signUp = (Button)findViewById(R.id.btnSignUp);

        TextView textView = findViewById(R.id.tvSignIn);

        String text = "Already a member? Sign-in here";

        SpannableString ss = new SpannableString(text);

        ClickableSpan clickSpan = new ClickableSpan() {

            //Open up register activity when span is clicked
            @Override
            public void onClick(@NonNull View widget) {
                Intent openMain = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(openMain);
            }

            //Set colour of clickable span
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.rgb(72, 135, 28));
            }
        };
        //Set clickable span on "Sign-in here"
        ss.setSpan(clickSpan, 18, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }


    private void register(){
        StringRequest request = new StringRequest(Request.Method.POST, "https://mayar.abertay.ac.uk/~1604475/MedWeb/API/register.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObject = new JSONObject(response);
                            String status = jObject.getString("status");

                            if(status.contains("0")){ // Retrieve user data, initialise shared preferences, open main activity
                                Toast.makeText(getApplicationContext(),
                                        "Registration Successful",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                            }else if(status.contains("1")){
                                String message = jObject.getString("message");
                                Toast.makeText(getApplicationContext(),
                                        message,Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
				//Pass parameters to API
                Map<String,String> params = new HashMap<>();
                params.put("button", "pressed");
                params.put("app", "true");
                params.put("firstName", firstName.getText().toString());
                params.put("lastName", lastName.getText().toString());
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());
                params.put("confirmPassword", confirmPassword.getText().toString());
                return params;
            }
        };

        Volley.newRequestQueue(RegisterActivity.this).add(request);
    }
}

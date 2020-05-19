package com.example.medicationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.provider.Settings;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private TextView  textView;
    private Button login;
    private SessionHelper sessionHelper;
    private static final String certficiateFile = "iot-cert.pem.crt";
    private static final String privateKeyFile = "private-key.pem.key";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionHelper = new SessionHelper(getApplicationContext());
		
		//Check for internal storage permission, if not granted request it
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
		
		//If user already logged in open dashboard
        if (sessionHelper.isLogin()) {
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
        }

		//Inform user an internet connection is required if not connectivity
        if (AirplaneModeOn(getApplicationContext()) == TRUE) {
            Toast.makeText(getApplicationContext(), "An internet connection is required to sign in, try turning off airplane mode", Toast.LENGTH_SHORT).show();
        }else if(!isNetwork(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "An internet connection is required to sign in", Toast.LENGTH_SHORT).show();
        }

        email = (EditText) findViewById(R.id.etEmail);
        password = (EditText) findViewById(R.id.etPassword);
        login = (Button) findViewById(R.id.btnLogin);


        textView = findViewById(R.id.tvRegister);
        String text = "Not a member? Register here";
		
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickSpan = new ClickableSpan() {

            //Open up register activity when span is clicked
            @Override
            public void onClick(@NonNull View widget) {
                Intent openRegister = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(openRegister);
            }

            //Set colour of clickable span
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.rgb(72, 135, 28));
            }
        };

        //Set clickable span on "Register here"
        ss.setSpan(clickSpan, 14, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        saveRequiredFiles();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void saveRequiredFiles() {

        String privateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIEowIBAAKCAQEAwosfhiXJ9TTo53M7eexcFqoqycoPD2eGNzOk++pKZpLnG0O7" +
                "qQISE0pGLy/UJa7l2RIgXQIOPEJZ8mS3OGCCj7y2nz6GhYcJ/DvEaz40swiH9A84" +
                "WxHVjzyQe9Zd6g90TF0zzRZjUmi2fDM+Bv/dw9RMJpOLAJmIpGcW7nBrTcgzP4a9" +
                "NdbcInQulNei/Q6jfUwwR+ohE+uTKmxpXcp3leL59mTaPK6r+DzcRY4BeQ/Ho71C" +
                "Owp8c++KSgoLbUTVApjro36jSiQPaBJdDEPAz+6bi5cto29Z73zPvZsQ0OSRNbLd" +
                "dFtZyY2fC8EV1UGV10b9ciG3qpr6TMb3rryEHwIDAQABAoIBAGx3nS/ySdQJmAHT" +
                "4cls2ztXzVuM6TCxi8ejZ819J9Zg+bHd5fepS+Ktesw8YpBQH58T2C+DQeSfKf9X" +
                "4D/yr1js4vWOqvov/IVB3ZXdrQxVKVgOVGGSY/vkzLn/H4Ws8C7TmFo0rVP5RiWh" +
                "fGfyqPbSm1K22L2Q3Pqs78Apteo7SFe8iYnkypoMZT2adUmJbXtuFFSRgbg8hyJE" +
                "9A99BitBmZLfCQWNk7PtC+QXpkBV1lZnECpdej7bvK2UZJRW6QfNkzQIIorlIgMs" +
                "KYCf6d8VVF0ud2HlyCVV3mK5TDZvFUhnzQUwIloxV1Yk2ry0TDMbsz68t3xo6i1Z" +
                "tyrhLEECgYEA4fNAYs3DuR7Tj0h8LUax+TrwhZc/rx/6xQX6JFGDa3IsnKZQGu4W" +
                "NGokI9s/yRVSWJM44K+IMtTmJzrH/W+qh9rVYIy/SzPeVlOC0XEIFv8HQXOuiaGX" +
                "Tm3atyiRyMVAcOuYQ02dzgxFMJtz71mS3gfA83F6CVzZoSzIZ/4G46kCgYEA3GqX" +
                "OMK3YeiJAMCE6krmC8bqQUXhlJtBtxZD/FKGvAtx86/OmpA1s4tJ9cCCxuuSdEZ7" +
                "T68BAhn25PEnFJI30K5t6J86t1HTaAhTtY2MIYq8rNS3FxJQXbULE4FzG5nlc8bc" +
                "Ctt1qtRu13n/Ea2n/GPFjZ1Wge8H82G2K+RwhocCgYBaKBua0Qu1xA0acQvLbRf6" +
                "oH/0biNlF1eSYg4ri7LqnoYag2oXNMT7aRa+j+9lpvJSkXvye1fXIIgtQxElmPbs" +
                "em8FtWNm+G1Q+MWMGJPnyR72CNit5iJfkwngZ3m0KLDL3dXFlSTQYKkC+iRKbu1h" +
                "Vqqw+Kh/L+NOonXcuMm68QKBgQDF0nikfcXefrgvT5o7OTuciEuIh7J9EJb7PmWI" +
                "GX1JWjBMmJYiDJnV6YZYpQ36gETyTKuxiYyrGSiIs5LymIS1q0eDhx1ImIzXitmu" +
                "UTrag8zwhP1KHKq6X5lwSTdJJ2dS8MBekjbaOky84YY2lJf3IoiUoAibFzAYlLLO" +
                "U/lWEwKBgBAF1gM//tXS7XELdv2FDpoQ5oUFTdX1Dr6LQ4g0EkGWHNs0/BFLlPew" +
                "R5Zp0hrclvu+R3R1m4NCNvtAJRtsmzgB7skM30pzMNmFs2L28gPmhQNCy06pXy/v" +
                "uLAJG5iIQ5cPFjG/1/pYZAuPrliiQyaYWo9r4VZmrINXOGNVV67j\n" +
                "-----END RSA PRIVATE KEY-----";

        String certificate = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDWTCCAkGgAwIBAgIUef9ddlggGOLB+5Rjk8oC0MqpCMIwDQYJKoZIhvcNAQEL" +
                "BQAwTTFLMEkGA1UECwxCQW1hem9uIFdlYiBTZXJ2aWNlcyBPPUFtYXpvbi5jb20g" +
                "SW5jLiBMPVNlYXR0bGUgU1Q9V2FzaGluZ3RvbiBDPVVTMB4XDTIwMDIwNDA5MjUx" +
                "M1oXDTQ5MTIzMTIzNTk1OVowHjEcMBoGA1UEAwwTQVdTIElvVCBDZXJ0aWZpY2F0" +
                "ZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMKLH4YlyfU06OdzO3ns" +
                "XBaqKsnKDw9nhjczpPvqSmaS5xtDu6kCEhNKRi8v1CWu5dkSIF0CDjxCWfJktzhg" +
                "go+8tp8+hoWHCfw7xGs+NLMIh/QPOFsR1Y88kHvWXeoPdExdM80WY1JotnwzPgb/" +
                "3cPUTCaTiwCZiKRnFu5wa03IMz+GvTXW3CJ0LpTXov0Oo31MMEfqIRPrkypsaV3K" +
                "d5Xi+fZk2jyuq/g83EWOAXkPx6O9QjsKfHPvikoKC21E1QKY66N+o0okD2gSXQxD" +
                "wM/um4uXLaNvWe98z72bENDkkTWy3XRbWcmNnwvBFdVBlddG/XIht6qa+kzG9668" +
                "hB8CAwEAAaNgMF4wHwYDVR0jBBgwFoAU0YnqSdZoAEG7w3CVu/yMUBK07oQwHQYD" +
                "VR0OBBYEFI7FaQG9WZhMM+VPHdQSiV344vvQMAwGA1UdEwEB/wQCMAAwDgYDVR0P" +
                "AQH/BAQDAgeAMA0GCSqGSIb3DQEBCwUAA4IBAQB1a2zQXWMY642vJ0tavzhYhExT" +
                "F7VK6tnpODs1tU1fjlgsJfMeXOuqZE4JFbOEThLZDeJ72alDS7WAYiocLCVshJHM" +
                "dR9wrrMfcJ+VrwGRkj0DQc8PiGsQmDHUExnDo2gBMfOUlBloCeHp+YnkPpOcsvIU" +
                "L616VAYtT26QeU+ED9Sm0kW0KYruBNT7oXeUB13Ou15btttlM0anDntSMvKOtldc" +
                "be/khv1RFxXyVutLMheK9pNJt6Obs8kzBYsKJ7bobTl7J5Ij90H8QxEeGi9/VX09" +
                "7e8gMqNOk6w437O8sSLoxZtRojPGXAwDsKA6flA1b8ZFOvhedtEpSjvga23j\n" +
                "-----END CERTIFICATE-----";

        FileOutputStream fos = null;
        FileOutputStream cert = null;

        try {
            fos = openFileOutput(privateKeyFile, MODE_PRIVATE);
            cert = openFileOutput(certficiateFile, MODE_PRIVATE);
            fos.write(privateKey.getBytes());
            cert.write(certificate.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (cert != null) {
                try {
                    cert.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


	//Check for success message received from API
    private void login(){
        StringRequest request = new StringRequest(Request.Method.POST, "https://mayar.abertay.ac.uk/~1604475/MedWeb/API/login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObject = new JSONObject(response);
                            String status = jObject.getString("status");

                            if(status.contains("0")){ // Retrieve user data, initialise shared preferences, open dashboard
                                String jsonUserID = jObject.getString("userID");
                                String jsonFirstName = jObject.getString("firstName");
                                String jsonAuthorisation = jObject.getString("authorisation");

                                SessionHelper session =  new SessionHelper(getApplicationContext());
                                session.createSession(jsonUserID, jsonFirstName, jsonAuthorisation);

                                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                startActivity(intent);
                            }else if(status.contains("1")){ //Status 1 symbolises an error
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

                Map<String,String> params = new HashMap<>();
                params.put("button", "pressed");
                params.put("app", "true");
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }
        };

        Volley.newRequestQueue(MainActivity.this).add(request);
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

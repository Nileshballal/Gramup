package com.gramup.lead.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gramup.lead.R;
import com.gramup.lead.model.Utility;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.gramup.lead.activity.CorporateRegistrationActivity.USERINFO;
import static com.gramup.lead.activity.CorporateRegistrationActivity.trustEveryone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;


public class MainActivity extends AppCompatActivity {

    Button button_sign;
    AppCompatRadioButton radio_user,radio_corpo,radio_guest;
    TextView txt_signup;
    String signup="",Role="",Email="",Password="";
    private ProgressDialog progressDialog;
    private long totalSize;
    EditText edt_email,edt_pass;
    SharedPreferences userpreferences;
    String Token;
    Utility utility;
    private String FinalJson="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        utility=new Utility();

        userpreferences = getSharedPreferences(USERINFO,
                Context.MODE_PRIVATE);
        button_sign=findViewById(R.id.button_sign);
        radio_user=findViewById(R.id.radio_user);
        radio_corpo=findViewById(R.id.radio_corpo);
        txt_signup=findViewById(R.id.txt_signup);
        edt_email=findViewById(R.id.edt_email);
        edt_pass=findViewById(R.id.edt_pass);

        String login=userpreferences.getString("Login","");
        Role=userpreferences.getString("Role","");

        if (login.equals("true")) {
            startActivity(new Intent(MainActivity.this, LeadsMainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
      /*  if (Role.equals("4")){
        if (login.equals("true")) {

           *//* startActivity(new Intent(MainActivity.this,DashboardActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));*//*
            startActivity(new Intent(MainActivity.this, LeadsMainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
        }else {
            if (Role.equals("3")){
                if (login.equals("true")) {
                    startActivity(new Intent(MainActivity.this, LiveLeadsActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
                }
        }*/

        button_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Email=edt_email.getText().toString();
                Password=edt_pass.getText().toString();



                FinalJson = "";

                if (Role.equals("")){
                   Toast.makeText(MainActivity.this,"Select Role",Toast.LENGTH_LONG).show();
                } else if (Email.equals("")){
                    Toast.makeText(MainActivity.this,"Enter email",Toast.LENGTH_LONG).show();
                } else if (Password.equals("")){
                    Toast.makeText(MainActivity.this,"Enter password",Toast.LENGTH_LONG).show();
                }else {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        //jsonObject.put("client_id", "2");
                        //jsonObject.put("client_secret", "y68wRWa9bq5xzm4PaPHmEIRnsHzjZtzcyR6KaRtz");
                      //  jsonObject.put("grant_type", "password");
                        jsonObject.put("email", Email);
                        jsonObject.put("password", Password);
                        FinalJson = jsonObject.toString();
                        FinalJson = FinalJson.replaceAll("\\\\", "");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (utility.isNet(MainActivity.this)) {
                        UploadFileToServer uploadFileToServer = new UploadFileToServer();
                        uploadFileToServer.execute();
                    }


                }

            }
        });
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (signup.equalsIgnoreCase("User")){
                    startActivity(new Intent(MainActivity.this,UserRegistrationActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }else if (signup.equalsIgnoreCase("Corporate")){
                    startActivity(new Intent(MainActivity.this,CorporateRegistrationActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }else {
                  //  startActivity(new Intent(MainActivity.this,DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }



            }
        });

        radio_corpo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    signup=radio_corpo.getText().toString();
                    Role="3";
                }

            }
        });
        radio_user.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    signup=radio_user.getText().toString();
                    Role="4";
                }

            }
        });

    }
    public class UploadFileToServer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progreass_lay);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }

        @Override
        protected String doInBackground(String... params) {



            try {

                trustEveryone();
                String url_string ="https://gram-up.com/gramup/api/login";

                URL mUrl = new URL(url_string);
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("POST");
                httpConnection.addRequestProperty("Content-Type", "application/json");
                httpConnection.addRequestProperty("Client-Platform", "android");
                httpConnection.setConnectTimeout(100000);
                httpConnection.setReadTimeout(100000);

                httpConnection.connect();

                DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream ());
                wr.writeBytes(FinalJson);
                wr.flush();
                wr.close();

                int responseCode = httpConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            try {
                if (s.contains("token_type")) {
                    JSONObject root = new JSONObject(s);
                    String token_type = root.getString("token_type");
                    String access_token = root.getString("access_token");
                    String user_code = root.getString("user_code");
                    SharedPreferences.Editor editor = userpreferences.edit();
                    editor.putString("Token", access_token);
                    editor.putString("Type", token_type);
                    editor.putString("Login", "true");
                    editor.putString("Role",Role);
                    editor.putString("code",user_code);
                    editor.commit();
                    Toast.makeText(MainActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(MainActivity.this, LeadsMainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();

                   /* startActivity(new Intent(MainActivity.this,DashboardActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));*/

                   /* if (Role.equals("4")) {
                        startActivity(new Intent(MainActivity.this, LeadsMainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }else {
                        startActivity(new Intent(MainActivity.this, LiveLeadsActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }*/
                }else {

                }

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }

}

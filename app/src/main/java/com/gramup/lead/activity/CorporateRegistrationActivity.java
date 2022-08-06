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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gramup.lead.Multipart.MultipartEntity;
import com.gramup.lead.Multipart.StringBody;
import com.gramup.lead.R;
import com.gramup.lead.model.State;
import com.gramup.lead.model.Utility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CorporateRegistrationActivity extends AppCompatActivity {
    SharedPreferences userpreferences;
    String Token;
    Utility utility;
    private ProgressDialog progressDialog;
    private long totalSize;

    EditText edt_name,edt_company_name,edt_designation,edt_exp,edt_mobile,edt_email,edt_address,edt_country,edt_pass;
    AutoCompleteTextView edt_state,edt_city,edt_district;
    Button button_sign_up;
    private String response="";
    ArrayList<State> stateArrayList;
    ArrayList<State>cityArrayList;
    ArrayList<State>districtArrayList;
    private String  StateId="",CityId="",DistrictId="";
    private String Name="",Email="",Password="",Company="",Designation="",Mobile="",
            Experience="",Address="",Referal="te-032207";;
    public static String USERINFO="USER";
    private ProgressDialog progressDialog_1;
    private String id;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.corporate_lay);
        getSupportActionBar().setTitle("Vendor Registration");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        progressDialog=new ProgressDialog(CorporateRegistrationActivity.this);
        userpreferences = getSharedPreferences(USERINFO,
                Context.MODE_PRIVATE);
        utility=new Utility();

        init();
        setlistner();

        stateArrayList=new ArrayList<>();
        cityArrayList=new ArrayList<>();
        districtArrayList=new ArrayList<>();

        if (utility.isNet(CorporateRegistrationActivity.this)){
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progreass_lay);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            GetStateList getStateList=new GetStateList();
            getStateList.execute();
        }
        if (utility.isNet(CorporateRegistrationActivity.this)){
            GetCityList getStateList=new GetCityList();
            getStateList.execute();
        }
        if (utility.isNet(CorporateRegistrationActivity.this)){
            GetDistrictList getStateList=new GetDistrictList();
            getStateList.execute();
        }



    }

    private void setlistner() {

        edt_state.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((AutoCompleteTextView)view).showDropDown();
                return false;
            }
        });
        edt_state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                StateId= String.valueOf(stateArrayList.get(position).getId());
            }
        });
        edt_city.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((AutoCompleteTextView)view).showDropDown();
                return false;
            }
        });
        edt_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CityId= String.valueOf(cityArrayList.get(position).getId());
            }
        });
        edt_district.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((AutoCompleteTextView)view).showDropDown();
                return false;
            }
        });
        edt_district.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DistrictId= String.valueOf(districtArrayList.get(position).getId());
            }
        });
        button_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Name=edt_name.getText().toString();
                Password=edt_pass.getText().toString();
                Email=edt_email.getText().toString();
                Experience=edt_exp.getText().toString();
                Designation=edt_designation.getText().toString();
                Mobile=edt_mobile.getText().toString();
                Company=edt_company_name.getText().toString();
                Address=edt_address.getText().toString();

                if (utility.isNet(CorporateRegistrationActivity.this)){
                    UploadFileToServer uploadFileToServer=new UploadFileToServer();
                    uploadFileToServer.execute();
                }
            }
        });
    }

    private void init() {


        edt_state=findViewById(R.id.edt_state);
        edt_address=findViewById(R.id.edt_address);
        edt_city=findViewById(R.id.edt_city);
        edt_company_name=findViewById(R.id.edt_company_name);
        edt_country=findViewById(R.id.edt_country);
        edt_designation=findViewById(R.id.edt_designation);
        edt_district=findViewById(R.id.edt_district);
        edt_email=findViewById(R.id.edt_email);
        edt_exp=findViewById(R.id.edt_exp);
        edt_mobile=findViewById(R.id.edt_mobile);
        edt_name=findViewById(R.id.edt_name);
        edt_pass=findViewById(R.id.edt_pass);
        button_sign_up=findViewById(R.id.button_sign_up);
    }


    class GetStateList extends AsyncTask<String, Void, String> {
        String url, res;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String integer) {
            super.onPostExecute(integer);
            progressDialog.dismiss();
            if (!integer.equalsIgnoreCase("")) {


                try {
                    stateArrayList.clear();
                    JSONArray jResults = new JSONArray(integer);
                    for (int i = 0; i < jResults.length(); i++) {
                        JSONObject jsonObject = jResults.getJSONObject(i);
                        State state = new State();
                        state.setId(jsonObject.getInt("id"));//UserLoginId
                        state.setName(jsonObject.getString("name"));
                        stateArrayList.add(state);
                    }
                    ArrayAdapter<State> dataAdapter = new ArrayAdapter<State>(CorporateRegistrationActivity.this, android.R.layout.simple_spinner_item, stateArrayList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    edt_state.setAdapter(dataAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                if (utility.isNet(CorporateRegistrationActivity.this)) {
                    Toast.makeText(CorporateRegistrationActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CorporateRegistrationActivity.this, "Record not found", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected String doInBackground(String ... params) {


            try {

                url = getResources().getString(R.string.baseurl) + "/states";

                URL mUrl = new URL(url);
                trustEveryone();
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.addRequestProperty("Content-Type", "application/json");
                httpConnection.addRequestProperty("Client-Platform", "android");
/*                Token = userpreferences.getString("userid", "");
                httpConnection.setRequestProperty("Cookie", Token);*/
                httpConnection.setConnectTimeout(100000);
                httpConnection.setReadTimeout(100000);
                //SSLSocketFactory sslSocketFactory;
                //sslSocketFactory = new TLSSocketFactory();
                // HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
                httpConnection.connect();

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
            return "";
        }
    }
    class GetCityList extends AsyncTask<String, Void, String> {
        String url, res;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String integer) {
            super.onPostExecute(integer);
            if (!integer.equalsIgnoreCase("")) {


                try {
                    cityArrayList.clear();
                    JSONArray jResults = new JSONArray(integer);
                    for (int i = 0; i < jResults.length(); i++) {
                        JSONObject jsonObject = jResults.getJSONObject(i);
                        State state = new State();
                        state.setId(jsonObject.getInt("id"));//UserLoginId
                        state.setName(jsonObject.getString("name"));
                        cityArrayList.add(state);
                    }
                    ArrayAdapter<State> dataAdapter = new ArrayAdapter<State>(CorporateRegistrationActivity.this, android.R.layout.simple_spinner_item, cityArrayList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    edt_city.setAdapter(dataAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                if (utility.isNet(CorporateRegistrationActivity.this)) {
                    Toast.makeText(CorporateRegistrationActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CorporateRegistrationActivity.this, "Record not found", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected String doInBackground(String ... params) {


            try {

                url = getResources().getString(R.string.baseurl) + "/cities";

                URL mUrl = new URL(url);
                trustEveryone();
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.addRequestProperty("Content-Type", "application/json");
                httpConnection.addRequestProperty("Client-Platform", "android");
/*                Token = userpreferences.getString("userid", "");
                httpConnection.setRequestProperty("Cookie", Token);*/
                httpConnection.setConnectTimeout(100000);
                httpConnection.setReadTimeout(100000);
                //SSLSocketFactory sslSocketFactory;
                //sslSocketFactory = new TLSSocketFactory();
                // HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
                httpConnection.connect();

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
            return "";
        }
    }

    class GetDistrictList extends AsyncTask<String, Void, String> {
        String url, res;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String integer) {
            super.onPostExecute(integer);
            if (!integer.equalsIgnoreCase("")) {


                try {
                    districtArrayList.clear();
                    JSONArray jResults = new JSONArray(integer);
                    for (int i = 0; i < jResults.length(); i++) {
                        JSONObject jsonObject = jResults.getJSONObject(i);
                        State state = new State();
                        state.setId(jsonObject.getInt("id"));//UserLoginId
                        state.setName(jsonObject.getString("name"));
                        districtArrayList.add(state);
                    }
                    ArrayAdapter<State> dataAdapter = new ArrayAdapter<State>(CorporateRegistrationActivity.this, android.R.layout.simple_spinner_item, districtArrayList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    edt_district.setAdapter(dataAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                if (utility.isNet(CorporateRegistrationActivity.this)) {
                    Toast.makeText(CorporateRegistrationActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CorporateRegistrationActivity.this, "Record not found", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected String doInBackground(String ... params) {


            try {

                url = getResources().getString(R.string.baseurl) + "/districts";

                URL mUrl = new URL(url);
                trustEveryone();
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.addRequestProperty("Content-Type", "application/json");
                httpConnection.addRequestProperty("Client-Platform", "android");
/*                Token = userpreferences.getString("userid", "");
                httpConnection.setRequestProperty("Cookie", Token);*/
                httpConnection.setConnectTimeout(100000);
                httpConnection.setReadTimeout(100000);
                //SSLSocketFactory sslSocketFactory;
                //sslSocketFactory = new TLSSocketFactory();
                // HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
                httpConnection.connect();

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
            return "";
        }
    }


    public static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }});
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(CorporateRegistrationActivity.this);
            progressDialog.setCancelable(true);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progreass_lay);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible

            // updating progress bar value

            // updating percentage value
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            trustEveryone();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getResources().getString(R.string.baseurl)
                    +"/register");

            try {
                MultipartEntity multipartEntity=new MultipartEntity();


                // Extra parameters if you want to pass to server ### Use here our text inputs

                multipartEntity.addPart("name", new StringBody(Name));
                multipartEntity.addPart("email", new StringBody(Email));
                multipartEntity.addPart("password", new StringBody(Password));
                multipartEntity.addPart("role",new StringBody("3"));
                multipartEntity.addPart("company_name",new StringBody(Company));
                multipartEntity.addPart("designation",new StringBody(Designation));
                multipartEntity.addPart("mobile",new StringBody(Mobile));
                multipartEntity.addPart("total_experience",new StringBody("5"));
                multipartEntity.addPart("experience_in",new StringBody(Experience));
                multipartEntity.addPart("address",new StringBody(Address));
                multipartEntity.addPart("countries_id",new StringBody("1"));
                multipartEntity.addPart("states_id",new StringBody(StateId));
                multipartEntity.addPart("cities_id",new StringBody(CityId));
                multipartEntity.addPart("districts_id",new StringBody(DistrictId));
                multipartEntity.addPart("referral_code",new StringBody(Referal));



                totalSize = multipartEntity.getContentLength();
                httppost.setEntity(multipartEntity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            // showing the server response in an alert dialog

            super.onPostExecute(result);
            progressDialog.dismiss();

            try {
                if (result.contains("token_type")) {
                    JSONObject root = new JSONObject(result);
                    String token_type = root.getString("token_type");
                    String access_token = root.getString("access_token");
                    String user_code = root.getString("user_code");
                    SharedPreferences.Editor editor = userpreferences.edit();
                    editor.putString("Token", access_token);
                    editor.putString("Type", token_type);
                    editor.putString("Login", "true");
                    editor.putString("Role", "3");
                    editor.putString("code",user_code);
                    editor.commit();
                    Toast.makeText(CorporateRegistrationActivity.this, "Corporate user registered successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CorporateRegistrationActivity.this, LeadsMainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }else {
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray ja = jsonObject.getJSONArray("original");
                    Toast.makeText(CorporateRegistrationActivity.this,ja.toString(),Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(CorporateRegistrationActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

    }
    private void verifyemail()  {
        progressDialog_1 = new ProgressDialog(CorporateRegistrationActivity.this);
        progressDialog_1.setCancelable(true);
        progressDialog_1.show();
        progressDialog_1.setContentView(R.layout.progreass_lay);
        progressDialog_1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        JSONObject jsonObject1 =new JSONObject();
        try {
            jsonObject1.put("email",Email);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,jsonObject1.toString() );
        Request request = new Request.Builder()
                .url("https://api.mojoauth.com/users/emailotp?language=")
                .method("POST", body)
                //   .addHeader("X-API-Key", "test-47796d03-bd92-42f7-a5b7-a28c67d0b404")
                .addHeader("X-API-Key", "e41962f1-e3b3-4e1f-a992-191d5b8f9ee8")
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String r=response.body().string();
            if (r.contains("state_id")){
                JSONObject jsonObject=new JSONObject(r);
                id=jsonObject.getString("state_id");
                getotpdialog();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getotpdialog() {
        progressDialog_1.dismiss();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.otp_lay, null);
        dialogBuilder.setView(dialogView);

        final EditText edt_otp = (EditText) dialogView.findViewById(R.id.edt_otp);
        Button button_resend = (Button) dialogView.findViewById(R.id.button_resend);
        Button button_submit = (Button) dialogView.findViewById(R.id.button_submit);

        button_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyemail();
            }
        });
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_otp.getText().toString().length()!=6){
                    Toast.makeText(CorporateRegistrationActivity.this,"Please enter OTP",Toast.LENGTH_SHORT).show();
                }else {
                    String OTP=edt_otp.getText().toString();
                    submitotp(OTP);
                }
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void submitotp(String otp) {

        progressDialog_1 = new ProgressDialog(CorporateRegistrationActivity.this);
        progressDialog_1.setCancelable(true);
        progressDialog_1.show();
        progressDialog_1.setContentView(R.layout.progreass_lay);
        progressDialog_1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        JSONObject jsonObject =new JSONObject();
        try {
            jsonObject.put("otp", otp);
            jsonObject.put("state_id", id);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder()
                .url("https://api.mojoauth.com/users/emailotp/verify")
                .method("POST", body)
                .addHeader("X-API-Key", "e41962f1-e3b3-4e1f-a992-191d5b8f9ee8")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String r=response.body().string();
            if (r.contains("access_token")) {
                progressDialog_1.dismiss();
                if (utility.isNet(CorporateRegistrationActivity.this)) {
                    UploadFileToServer uploadFileToServer = new UploadFileToServer();
                    uploadFileToServer.execute();
                }
            }
            System.out.print(r);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
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
import android.widget.EditText;
import android.widget.Toast;

import com.gramup.lead.Multipart.MultipartEntity;
import com.gramup.lead.Multipart.StringBody;
import com.gramup.lead.R;
import com.gramup.lead.model.Utility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import static com.gramup.lead.activity.CorporateRegistrationActivity.USERINFO;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class EnquiryActivity extends AppCompatActivity {

    SharedPreferences userpreferences;
    String Token;
    Utility utility;
    private ProgressDialog progressDialog;
    private long totalSize;

    Button button_sign_up;
    private String response="";

    EditText edt_vendor_name,edt_vendor_address,edt_vendor_contact,edt_vendor_email,edt_note,edt_price;
    String Name,Email,Address,Mobile,Note,Price;
    private String requisitions_id="";
    private String code="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enquiry_lay);
        getSupportActionBar().setTitle("Bidding");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        progressDialog = new ProgressDialog(EnquiryActivity.this);
        userpreferences = getSharedPreferences(USERINFO,
                Context.MODE_PRIVATE);
        utility = new Utility();
        Token=userpreferences.getString("Token","");
        code=userpreferences.getString("code","");


        requisitions_id=getIntent().getStringExtra("requisitions_id");

        init();
        setlistner();
    }

    private void setlistner() {


        button_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Name=edt_vendor_name.getText().toString();
                Address=edt_vendor_address.getText().toString();
                Email=edt_vendor_email.getText().toString();
                Mobile=edt_vendor_contact.getText().toString();
                Note=edt_note.getText().toString();
                Price=edt_price.getText().toString();

                if (utility.isNet(EnquiryActivity.this)){
                    UploadFileToServer uploadFileToServer=new UploadFileToServer();
                    uploadFileToServer.execute();
                }
            }
        });
    }

    private void init() {


        edt_vendor_name=findViewById(R.id.edt_vendor_name);
        edt_vendor_address=findViewById(R.id.edt_vendor_address);
        edt_vendor_contact=findViewById(R.id.edt_vendor_contact);
        edt_vendor_email=findViewById(R.id.edt_vendor_email);
        edt_note=findViewById(R.id.edt_note);
        edt_price=findViewById(R.id.edt_price);
        button_sign_up=findViewById(R.id.button_sign_up);
    }

    private static void trustEveryone() {
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
            progressDialog = new ProgressDialog(EnquiryActivity.this);
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
                    +"/enquiry");

            try {
                MultipartEntity multipartEntity=new MultipartEntity();


                // Extra parameters if you want to pass to server ### Use here our text inputs

                multipartEntity.addPart("requisition_id", new StringBody(requisitions_id));
                multipartEntity.addPart("user_code", new StringBody(code));
                multipartEntity.addPart("vendor_name", new StringBody(Name));
                multipartEntity.addPart("vendor_address", new StringBody(Address));
                multipartEntity.addPart("vendor_contact",new StringBody(Mobile));
                multipartEntity.addPart("vendor_email",new StringBody(Email));
                multipartEntity.addPart("note",new StringBody(Note));
                multipartEntity.addPart("price",new StringBody(Price));



                totalSize = multipartEntity.getContentLength();
                httppost.setEntity(multipartEntity);
                httppost.addHeader("Authorization", "Bearer"+" "+Token);

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
                if (result.contains("reference_enquiry_no")) {
                    Toast.makeText(EnquiryActivity.this, "Enquiry created successfully", Toast.LENGTH_SHORT).show();
                     startActivity(new Intent(EnquiryActivity.this,LeadsMainActivity.class)
                     .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                     finish();
                }else {
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray ja = jsonObject.getJSONArray("original");
                    Toast.makeText(EnquiryActivity.this,ja.toString(),Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(EnquiryActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

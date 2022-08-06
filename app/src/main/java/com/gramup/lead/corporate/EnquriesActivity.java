package com.gramup.lead.corporate;


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
import android.widget.TextView;
import android.widget.Toast;

import com.gramup.lead.Multipart.MultipartEntity;
import com.gramup.lead.Multipart.StringBody;
import com.gramup.lead.R;
import com.gramup.lead.model.Bidding;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.gramup.lead.activity.CorporateRegistrationActivity.trustEveryone;
import static com.gramup.lead.fragment.LeadsFragmentA.USERINFO;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EnquriesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String Token;
    Utility utility;
    SharedPreferences userpreferences;
    private ProgressDialog progressDialog;
    CorporateEnquiriesLeadAdapter myLeadAdapter;
    ArrayList<Bidding>leadsArrayList;
    TextView txt_record;
    private String requisition_id="";
    private String ID="",Status="";
    public long totalSize=0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_lay);
        getSupportActionBar().setTitle("Bidding List");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        userpreferences = getSharedPreferences(USERINFO,
                Context.MODE_PRIVATE);
        utility=new Utility();
        recyclerView = (RecyclerView) findViewById(R.id.recycleViewContainer);
        recyclerView.setHasFixedSize(true);
        leadsArrayList=new ArrayList<>();
        layoutManager = new LinearLayoutManager(EnquriesActivity.this);
        txt_record = (TextView) findViewById(R.id.txt_record);
        recyclerView.setLayoutManager(layoutManager);
        myLeadAdapter = new CorporateEnquiriesLeadAdapter(EnquriesActivity.this, leadsArrayList);
        recyclerView.setAdapter(myLeadAdapter);
        progressDialog=new ProgressDialog(EnquriesActivity.this);
        Token=userpreferences.getString("Token","");

        requisition_id=getIntent().getStringExtra("requisition_id");

        if (utility.isNet(EnquriesActivity.this)){
            GetLeadsShowData getLeadsShowData=new GetLeadsShowData();
            getLeadsShowData.execute();
        }








    }

    public void setstatus(String id, String s) {

        ID=id;
        Status=s;
        if (utility.isNet(EnquriesActivity.this)){
            progressDialog = new ProgressDialog(EnquriesActivity.this);
            progressDialog.setCancelable(true);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progreass_lay);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            UploadFileToServer  uploadFileToServer=new UploadFileToServer();
            uploadFileToServer.execute();
        }


    }

    public class GetLeadsShowData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progreass_lay);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }

        @Override
        protected String doInBackground(String... params) {



            try {


                String url_string="";

                url_string =getResources().getString(R.string.baseurl)+"/enquiries/"+requisition_id;



                URL mUrl = new URL(url_string);
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Authorization", "Bearer"+" "+Token);
                httpConnection.setConnectTimeout(100000);
                httpConnection.setReadTimeout(100000);
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
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            try {
                JSONArray root = new JSONArray(s);
                leadsArrayList.clear();
                for (int i=0;i<root.length();i++){
                    Bidding leads=new Bidding();

                    JSONObject jsonObject=root.getJSONObject(i);
                    leads.setId(jsonObject.getString("id"));
                    String reference_enquiry_no=jsonObject.getString("reference_enquiry_no");
                    leads.setReference_enquiry_no(reference_enquiry_no);
                    String requisitions_id=jsonObject.getString("requisitions_id");
                    leads.setRequisitions_id(requisitions_id);
                    String vendor_name=jsonObject.getString("vendor_name");
                    leads.setVendor_name(vendor_name);
                    String vendor_address=jsonObject.getString("vendor_address");
                    leads.setVendor_address(vendor_address);
                    String vendor_contact=jsonObject.getString("vendor_contact");
                    leads.setVendor_contact(vendor_contact);
                    leads.setVendor_email(jsonObject.getString("vendor_email"));
                    leads.setPrice(jsonObject.getString("price"));
                    leads.setNote(jsonObject.getString("note"));
                    leads.setCreated_at(jsonObject.getString("created_at"));
                    leads.setStatus(jsonObject.getString("status"));
                    JSONObject json=jsonObject.getJSONObject("user");
                    String User=json.getString("id");
                    leads.setUser(User);
                    leadsArrayList.add(leads);
                }



                if (leadsArrayList.size()>0){
                    myLeadAdapter.updateList(leadsArrayList);
                }else {
                    txt_record.setVisibility(View.VISIBLE);
                }

                //  mAdapter.notifyDataSetChanged();



            } catch (Exception e) {
                Toast.makeText(EnquriesActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();



                //   progressDialog.dismiss();
            }
        }
    }
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();


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
                    +"/update-enquiry-status/"+ID);

            try {
                MultipartEntity multipartEntity=new MultipartEntity();


                // Extra parameters if you want to pass to server ### Use here our text inputs

                multipartEntity.addPart("status", new StringBody(Status));




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
                    Toast.makeText(EnquriesActivity.this, "Status updated successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EnquriesActivity.this, LiveLeadsActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }

            } catch (Exception e) {
                Toast.makeText(EnquriesActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
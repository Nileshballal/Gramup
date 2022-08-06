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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gramup.lead.R;
import com.gramup.lead.activity.MainActivity;
import com.gramup.lead.model.Leads;
import com.gramup.lead.model.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.gramup.lead.fragment.LeadsFragmentA.USERINFO;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LiveLeadsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String Token;
    Utility utility;
    SharedPreferences userpreferences;
    private ProgressDialog progressDialog;
    CorporateLiveLeadAdapter myLeadAdapter;
    ArrayList<Leads>leadsArrayList;
    TextView txt_record;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_lay);

        getSupportActionBar().setTitle("Leads List");
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
        layoutManager = new LinearLayoutManager(LiveLeadsActivity.this);
        txt_record = (TextView) findViewById(R.id.txt_record);
        recyclerView.setLayoutManager(layoutManager);
        myLeadAdapter = new CorporateLiveLeadAdapter(LiveLeadsActivity.this, leadsArrayList);
        recyclerView.setAdapter(myLeadAdapter);
        progressDialog=new ProgressDialog(LiveLeadsActivity.this);
        Token=userpreferences.getString("Token","");

        if (utility.isNet(LiveLeadsActivity.this)){
            GetLeadsShowData getLeadsShowData=new GetLeadsShowData();
            getLeadsShowData.execute();
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

                url_string =getResources().getString(R.string.baseurl)+"/all-requisitions";



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
                    Leads leads=new Leads();

                    JSONObject jsonObject=root.getJSONObject(i);
                    String id=jsonObject.getString("id");
                    leads.setId(id);
                    String items_master_id=jsonObject.getString("items_master_id");
                    leads.setItems_master_id(items_master_id);
                    String item_description=jsonObject.getString("item_description");
                    leads.setItem_description(item_description);
                    String item_best_price=jsonObject.getString("item_best_price");
                    leads.setItem_best_price(item_best_price);
                    String  remarks=jsonObject.getString("remarks");
                    leads.setRemarks(remarks);
                    String required_on=jsonObject.getString("required_on");
                    leads.setRequired_on(required_on);
                    leads.setQuantity(jsonObject.getString("qty"));
                    leads.setReference_requisition_no(jsonObject.getString("reference_requisition_no"));

                        JSONObject json = jsonObject.getJSONObject("user");
                        if (json!=null) {
                            String User = json.getString("id");
                            leads.setUser(User);
                        }

                    leadsArrayList.add(leads);

                }



                if (leadsArrayList.size()>0){
                    myLeadAdapter.updateList(leadsArrayList);
                }else {
                    txt_record.setVisibility(View.VISIBLE);
                }

                //  mAdapter.notifyDataSetChanged();



            } catch (Exception e) {
                SharedPreferences.Editor editor = userpreferences.edit();
                editor.remove("Token");
                editor.remove("Type");
                editor.remove("Login");
                editor.remove("Role");
                editor.remove("code");
                editor.commit();
                startActivity(new Intent(LiveLeadsActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                progressDialog.dismiss();



                //   progressDialog.dismiss();
            }
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

        }

        if (item.getItemId() == R.id.logout) {

            SharedPreferences.Editor editor = userpreferences.edit();
            editor.remove("Token");
            editor.remove("Type");
            editor.remove("Login");
            editor.remove("Role");
            editor.remove("code");
            editor.commit();
            startActivity(new Intent(LiveLeadsActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
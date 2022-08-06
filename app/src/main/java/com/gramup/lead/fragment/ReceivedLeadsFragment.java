package com.gramup.lead.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gramup.lead.R;
import com.gramup.lead.activity.MainActivity;
import com.gramup.lead.adapter.BiddingAdapter;
import com.gramup.lead.model.Bidding;
import com.gramup.lead.model.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.gramup.lead.activity.CorporateRegistrationActivity.USERINFO;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReceivedLeadsFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String Token;
    Utility utility;
    SharedPreferences userpreferences;
    private ProgressDialog progressDialog;
    BiddingAdapter myLeadAdapter;
    ArrayList<Bidding>leadsArrayList;
    TextView txt_record;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.list_lay, container, false);
        progressDialog = new ProgressDialog(getActivity());
        userpreferences = getActivity().getSharedPreferences(USERINFO,
                Context.MODE_PRIVATE);
        utility=new Utility();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleViewContainer);
        txt_record = (TextView) rootView.findViewById(R.id.txt_record);
        recyclerView.setHasFixedSize(true);
        leadsArrayList=new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        myLeadAdapter = new BiddingAdapter(getActivity(), leadsArrayList);
        recyclerView.setAdapter(myLeadAdapter);
        progressDialog=new ProgressDialog(getActivity());
        Token=userpreferences.getString("Token","");

        if (utility.isNet(getActivity())){
            GetLeadsShowData getLeadsShowData=new GetLeadsShowData();
            getLeadsShowData.execute();
        }

        return rootView;
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

                url_string =getResources().getString(R.string.baseurl)+"/enquiries";



                URL mUrl = new URL(url_string);
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("GET");
                Token=userpreferences.getString("Token","");
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
                    String reference_enquiry_no=jsonObject.getString("reference_enquiry_no");
                    leads.setReference_enquiry_no(reference_enquiry_no);
                    String requisitions_id=jsonObject.getString("requisition_id");
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
              /*  SharedPreferences.Editor editor = userpreferences.edit();
                editor.remove("Token");
                editor.remove("Type");
                editor.remove("Login");
                editor.remove("Role");
                editor.remove("code");
                editor.commit();
                startActivity(new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                getActivity().finish();*/
                progressDialog.dismiss();



                //   progressDialog.dismiss();
            }
        }
    }

}
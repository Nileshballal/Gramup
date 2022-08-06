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
import com.gramup.lead.adapter.CommissionAdapter;
import com.gramup.lead.model.Commission;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CommissionFragment extends Fragment {



    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String Token;
    Utility utility;
    SharedPreferences userpreferences;
    private ProgressDialog progressDialog;
    CommissionAdapter commissionAdapter;
    ArrayList<Commission> commissionArrayList;
    TextView txt_record;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.list_lay, container, false);
        userpreferences = getActivity().getSharedPreferences(USERINFO,
                Context.MODE_PRIVATE);
        utility=new Utility();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleViewContainer);
        recyclerView.setHasFixedSize(true);
        commissionArrayList=new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        txt_record = (TextView) rootView.findViewById(R.id.txt_record);
        recyclerView.setLayoutManager(layoutManager);
        commissionAdapter = new CommissionAdapter(getActivity(), commissionArrayList);
        recyclerView.setAdapter(commissionAdapter);
        progressDialog=new ProgressDialog(getActivity());
        Token=userpreferences.getString("Token","");

        if (utility.isNet(getActivity())){
            GetCommissionShowData getCommissionShowData=new GetCommissionShowData();
            getCommissionShowData.execute();
        }



        return rootView;




    }


    public class GetCommissionShowData extends AsyncTask<String, Void, String> {

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
                commissionArrayList.clear();
                for (int i=0;i<root.length();i++){
                    Commission leads=new Commission();

                    JSONObject jsonObject=root.getJSONObject(i);
                    String status=jsonObject.getString("status");
                    if (status.equalsIgnoreCase("2")) {

                        String price = jsonObject.getString("price");
                        leads.setPrice(price);
                        String note = jsonObject.getString("note");
                        leads.setNote(note);
                        String bidder_commission = jsonObject.getString("bidder_commission");
                        leads.setBidder_commission(bidder_commission);
                        String requisition_commission = jsonObject.getString("requisition_commission");
                        leads.setRequisition_commission(requisition_commission);
                        String updated_at = jsonObject.getString("updated_at");
                        leads.setUpdated_at(updated_at);
                        leads.setReference_enquiry_no(jsonObject.getString("reference_enquiry_no"));
                        commissionArrayList.add(leads);
                    }
                }



                if (commissionArrayList.size()>0){
                    commissionAdapter.updateList(commissionArrayList);
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
                startActivity(new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                getActivity().finish();
                progressDialog.dismiss();



                //   progressDialog.dismiss();
            }
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}
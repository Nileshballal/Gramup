package com.gramup.lead.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gramup.lead.R;
import com.gramup.lead.model.Leads;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyLeadAdapter extends RecyclerView.Adapter<MyLeadAdapter.ViewHolder> {

    private Context context;
    private List<Leads> leadsList;
    public static final String USERINFO = "UserInfo";
    String DonorUserid;
    SharedPreferences userpreferences;
    private ViewHolder viewHolder;

    public MyLeadAdapter(Context context, ArrayList<Leads>leadsList) {
        this.context = context;
        this.leadsList = leadsList;
        userpreferences = context.getSharedPreferences(USERINFO,
                Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.myleads_item_lay, parent, false);
        viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setTag(leadsList.get(position));
        viewHolder=holder;
         final Leads leads = leadsList.get(position);
         holder.txt_budget.setText("Budget : " + leads.getItem_best_price());
         holder.txt_desc.setText(leads.getRemarks());
         holder.txt_item_desc.setText(leads.getItem_description());
         holder.txt_qty.setText("Qty : " + leads.getQuantity());

            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            DateFormat outputFormat = new SimpleDateFormat("dd MMM");
            String Endate = leads.getRequired_on();

            Date date = null;
            try {
                date = inputFormat.parse(Endate);
                String outputDateStr = outputFormat.format(date);
                holder.txt_date.setText("Valid : "+ outputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }





    }

    public void updateList(List<Leads> categoryList) {
        this.leadsList=categoryList;
        notifyDataSetChanged();


    }
    @Override
    public int getItemCount() {

        return leadsList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txt_budget,txt_date,txt_qty,txt_desc,txt_item_desc;
        View view_1;



        public ViewHolder(View itemView) {
            super(itemView);

            txt_budget = (TextView) itemView.findViewById(R.id.txt_budget);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            txt_qty = (TextView) itemView.findViewById(R.id.txt_qty);
            txt_desc = (TextView) itemView.findViewById(R.id.txt_desc);
            txt_item_desc = (TextView) itemView.findViewById(R.id.txt_item_desc);






        }
    }

}
package com.gramup.lead.corporate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gramup.lead.R;
import com.gramup.lead.model.Leads;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CorporateLiveLeadAdapter extends RecyclerView.Adapter<CorporateLiveLeadAdapter.ViewHolder> {

    private Context context;
    private List<Leads> leadsList;
    public static final String USERINFO = "UserInfo";
    String DonorUserid;
    SharedPreferences userpreferences;
    private ViewHolder viewHolder;

    public CorporateLiveLeadAdapter(Context context, ArrayList<Leads>leadsList) {
        this.context = context;
        this.leadsList = leadsList;
        userpreferences = context.getSharedPreferences(USERINFO,
                Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.liveleads_item_lay, parent, false);
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
         holder.txt_enq.setText("Enquiry No : " + leads.getReference_requisition_no());
        holder.txt_usercode.setText("PSUSR"+leads.getUser());
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


            holder.card_digital.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context,
                            EnquriesActivity.class).
                            putExtra("requisition_id",
                                    leadsList.get(position).getId()).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            });



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

        public TextView txt_budget,txt_date,txt_qty,txt_desc,txt_item_desc,txt_enq,txt_usercode;
        CardView card_digital;
        View view_1;



        public ViewHolder(View itemView) {
            super(itemView);

            txt_budget = (TextView) itemView.findViewById(R.id.txt_budget);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            txt_qty = (TextView) itemView.findViewById(R.id.txt_qty);
            txt_desc = (TextView) itemView.findViewById(R.id.txt_desc);
            txt_item_desc = (TextView) itemView.findViewById(R.id.txt_item_desc);
            txt_enq = (TextView) itemView.findViewById(R.id.txt_enq);
            txt_usercode = (TextView) itemView.findViewById(R.id.txt_usercode);
            card_digital = (CardView) itemView.findViewById(R.id.card_digital);






        }
    }

}
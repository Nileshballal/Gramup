package com.gramup.lead.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gramup.lead.R;
import com.gramup.lead.model.Bidding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BiddingAdapter extends RecyclerView.Adapter<BiddingAdapter.ViewHolder> {

    private Context context;
    private List<Bidding> leadsList;
    public static final String USERINFO = "UserInfo";
    String DonorUserid;
    SharedPreferences userpreferences;
    private ViewHolder viewHolder;

    public BiddingAdapter(Context context, ArrayList<Bidding>leadsList) {
        this.context = context;
        this.leadsList = leadsList;
        userpreferences = context.getSharedPreferences(USERINFO,
                Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bidding_lay, parent, false);
        viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setTag(leadsList.get(position));
        viewHolder=holder;
        final Bidding bidding = leadsList.get(position);
        holder.txt_budget.setText("Budget : " + bidding.getPrice());
        holder.txt_v_name.setText(bidding.getVendor_name());
        holder.txt_enq.setText("Enquiry No : " + bidding.getReference_enquiry_no());
        holder.txt_address.setText("Address : " + bidding.getVendor_address());
        holder.txt_email.setText("Email : " + bidding.getVendor_email());
        holder.txt_mobile.setText("Mobile : " + bidding.getVendor_contact());
        holder.txt_usercode.setText("PSUSR"+bidding.getUser());
        String Note=bidding.getNote();
        if (Note.equals("")){
        }else {
            holder.txt_note.setVisibility(View.VISIBLE);
            holder.txt_note.setText("Remark : " + Note);
        }

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        String Endate = bidding.getCreated_at();
        String dt = Endate;
        String[] namesList = dt.split("T");
        String d = namesList [0];
        Date datee = null;
        try {
            datee = inputFormat.parse(d);
            String outputDateStr = outputFormat.format(datee);
            holder.txt_date.setText("Valid : "+ outputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.txt_accept.setVisibility(View.GONE);
        holder.txt_reject.setVisibility(View.GONE);
        holder.txt_status.setVisibility(View.GONE);

    }

    public void updateList(List<Bidding> categoryList) {
        this.leadsList=categoryList;
        notifyDataSetChanged();


    }
    @Override
    public int getItemCount() {

        return leadsList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txt_budget,txt_mobile,txt_email,txt_address,txt_v_name,
                txt_enq,txt_date,txt_note,txt_usercode,txt_reject,txt_accept,txt_status;
        CardView card_digital;
        View view_1;



        public ViewHolder(View itemView) {
            super(itemView);

            txt_budget = (TextView) itemView.findViewById(R.id.txt_budget);
            txt_mobile = (TextView) itemView.findViewById(R.id.txt_mobile);
            txt_email = (TextView) itemView.findViewById(R.id.txt_email);
            txt_address = (TextView) itemView.findViewById(R.id.txt_address);
            txt_v_name = (TextView) itemView.findViewById(R.id.txt_v_name);
            txt_enq = (TextView) itemView.findViewById(R.id.txt_enq);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            txt_note = (TextView) itemView.findViewById(R.id.txt_note);
            txt_usercode = (TextView) itemView.findViewById(R.id.txt_usercode);
            txt_reject = (TextView) itemView.findViewById(R.id.txt_reject);
            txt_accept = (TextView) itemView.findViewById(R.id.txt_accept);
            txt_status = (TextView) itemView.findViewById(R.id.txt_status);
            card_digital = (CardView) itemView.findViewById(R.id.card_digital);






        }
    }

}
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
import com.gramup.lead.model.Commission;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CommissionAdapter extends RecyclerView.Adapter<CommissionAdapter.ViewHolder> {

    private Context context;
    private List<Commission> commissionList;
    public static final String USERINFO = "UserInfo";
    String DonorUserid;
    SharedPreferences userpreferences;
    private ViewHolder viewHolder;

    public CommissionAdapter(Context context, ArrayList<Commission>commissionList) {
        this.context = context;
        this.commissionList = commissionList;
        userpreferences = context.getSharedPreferences(USERINFO,
                Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.commision_item_lay, parent, false);
        viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setTag(commissionList.get(position));
        viewHolder=holder;
        final Commission commission = commissionList.get(position);
        holder.txt_enq.setText("Enquiry Commission : " + commission.getRequisition_commission());
        holder.txt_ref_no.setText("Enquiry No : " + commission.getReference_enquiry_no());
        holder.txt_bidder.setText("Bidder Commission : " +commission.getBidder_commission() );
        holder.txt_budget.setText("Budget : " +commission.getPrice() );
        String Note=commission.getNote();
        if (Note.equals("")){
        }else {
            holder.txt_note.setVisibility(View.VISIBLE);
            holder.txt_note.setText("Remark : " + Note);
        }

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        String Endate = commission.getUpdated_at();
        String dt = Endate;
        String[] namesList = dt.split("T");
        String d = namesList [0];
        Date datee = null;
        try {
            datee = inputFormat.parse(d);
            String outputDateStr = outputFormat.format(datee);
            holder.txt_date.setText("Completion Date : "+ outputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void updateList(List<Commission> categoryList) {
        this.commissionList=categoryList;
        notifyDataSetChanged();


    }
    @Override
    public int getItemCount() {

        return commissionList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txt_date,txt_enq,txt_bidder,txt_ref_no,txt_budget,txt_note;
        CardView card_digital;
        View view_1;



        public ViewHolder(View itemView) {
            super(itemView);

            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            txt_enq = (TextView) itemView.findViewById(R.id.txt_enq);
            txt_bidder = (TextView) itemView.findViewById(R.id.txt_bidder);
            txt_ref_no = (TextView) itemView.findViewById(R.id.txt_ref_no);
            txt_budget = (TextView) itemView.findViewById(R.id.txt_budget);
            txt_note = (TextView) itemView.findViewById(R.id.txt_note);
            card_digital = (CardView) itemView.findViewById(R.id.card_digital);






        }
    }

}
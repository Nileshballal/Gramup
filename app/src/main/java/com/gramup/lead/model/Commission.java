package com.gramup.lead.model;

import java.io.Serializable;

public class Commission implements Serializable {

    String reference_enquiry_no,price,status,bidder_commission,requisition_commission,updated_at,note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getReference_enquiry_no() {
        return reference_enquiry_no;
    }

    public void setReference_enquiry_no(String reference_enquiry_no) {
        this.reference_enquiry_no = reference_enquiry_no;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBidder_commission() {
        return bidder_commission;
    }

    public void setBidder_commission(String bidder_commission) {
        this.bidder_commission = bidder_commission;
    }

    public String getRequisition_commission() {
        return requisition_commission;
    }

    public void setRequisition_commission(String requisition_commission) {
        this.requisition_commission = requisition_commission;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}

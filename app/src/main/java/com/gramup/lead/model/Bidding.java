package com.gramup.lead.model;

import java.io.Serializable;

public class Bidding implements Serializable {

    String reference_enquiry_no,requisitions_id,vendor_name,vendor_address,vendor_contact,vendor_email,price;
    String created_at,note,User,Id,status;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getReference_enquiry_no() {
        return reference_enquiry_no;
    }

    public void setReference_enquiry_no(String reference_enquiry_no) {
        this.reference_enquiry_no = reference_enquiry_no;
    }

    public String getRequisitions_id() {
        return requisitions_id;
    }

    public void setRequisitions_id(String requisitions_id) {
        this.requisitions_id = requisitions_id;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getVendor_address() {
        return vendor_address;
    }

    public void setVendor_address(String vendor_address) {
        this.vendor_address = vendor_address;
    }

    public String getVendor_contact() {
        return vendor_contact;
    }

    public void setVendor_contact(String vendor_contact) {
        this.vendor_contact = vendor_contact;
    }

    public String getVendor_email() {
        return vendor_email;
    }

    public void setVendor_email(String vendor_email) {
        this.vendor_email = vendor_email;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}

package com.gramup.lead.model;

import java.io.Serializable;

public class Leads implements Serializable {

    String id,items_master_id,item_description,item_best_price,user_code,remarks,required_on,Quantity,reference_requisition_no,user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReference_requisition_no() {
        return reference_requisition_no;
    }

    public void setReference_requisition_no(String reference_requisition_no) {
        this.reference_requisition_no = reference_requisition_no;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItems_master_id() {
        return items_master_id;
    }

    public void setItems_master_id(String items_master_id) {
        this.items_master_id = items_master_id;
    }

    public String getItem_description() {
        return item_description;
    }

    public void setItem_description(String item_description) {
        this.item_description = item_description;
    }

    public String getItem_best_price() {
        return item_best_price;
    }

    public void setItem_best_price(String item_best_price) {
        this.item_best_price = item_best_price;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRequired_on() {
        return required_on;
    }

    public void setRequired_on(String required_on) {
        this.required_on = required_on;
    }
}

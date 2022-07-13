package com.sesolutions.responses.contest;

import com.google.gson.annotations.SerializedName;

public class Transaction {
    private String transaction_id;//3,
    private int id;//":46,
    private String title;//test package",
    @SerializedName("package")
    private String packaze;//test package",
    private String gateway;//Paypal",
    private String status;//Active",
    private String amount;//$100.00 monthly",
    private String date;//September 17, 2018 12:52 PM +04"

    public String getTransaction_id() {
        return transaction_id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPackaze() {
        return packaze;
    }

    public String getGateway() {
        return gateway;
    }

    public String getStatus() {
        return status;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }
}

package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;

public class PaginationHelper {
    @SerializedName("loggedin_user_id")
    private int loggedinUserId;
    @SerializedName("total_page")
    private int totalPage;
    private int total;
    @SerializedName("current_page")
    private int currentPage;
    @SerializedName("next_page")
    private int nextPage;


    public int getLoggedInUserId() {
        return loggedinUserId;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getTotal() {
        return total;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getNextPage() {
        return nextPage;
    }
}

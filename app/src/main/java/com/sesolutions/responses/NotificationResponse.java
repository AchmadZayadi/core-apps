package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by root on 7/11/17.
 */

public class NotificationResponse {

    @SerializedName("result")
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result {
        @SerializedName("loggedin_user_id")
        private int loggedinUserId;
        @SerializedName("total_page")
        private int totalPage;
        private int total;
        @SerializedName("current_page")
        private int currentPage;
        @SerializedName("next_page")
        private int nextPage;
        @SerializedName("notification")
        private List<Notifications> notification;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getNextPage() {
            return nextPage;
        }

        public void setNextPage(int nextPage) {
            this.nextPage = nextPage;
        }

        public int getLoggedinUserId() {
            return loggedinUserId;
        }

        public void setLoggedinUserId(int loggedinUserId) {
            this.loggedinUserId = loggedinUserId;
        }

        public List<Notifications> getNotification() {
            return notification;
        }

        public void setNotification(List<Notifications> notification) {
            this.notification = notification;
        }
    }
}

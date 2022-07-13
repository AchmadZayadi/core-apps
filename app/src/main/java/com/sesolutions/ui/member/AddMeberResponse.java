package com.sesolutions.ui.member;

import android.app.Notification;

import com.sesolutions.responses.Notifications;

public class AddMeberResponse {


    /**
     * notification : {"user_id":47,"title":"Indian Moms","follow":{"action":"follow","text":"Follow"},"block":{"action":"unblock","text":"Unblock"},"mutualFriends":"0 mutual friends","user_image":"http://sandbox.socialnetworking.solutions/apptesting/public/user/6d/03/c4e4ec5b2f094687678681d5921a7aa8.jpg","membership":{"label":"Add Friend","icon":"http://sandbox.socialnetworking.solutions/apptesting/application/modules/User/externals/images/friends/add.png","action":"add"}}
     * loggedin_user_id : 24
     */

    private ResultBean result;
    /**
     * result : {"notification":{"user_id":47,"title":"Indian Moms","follow":{"action":"follow","text":"Follow"},"block":{"action":"unblock","text":"Unblock"},"mutualFriends":"0 mutual friends","user_image":"http://sandbox.socialnetworking.solutions/apptesting/public/user/6d/03/c4e4ec5b2f094687678681d5921a7aa8.jpg","membership":{"label":"Add Friend","icon":"http://sandbox.socialnetworking.solutions/apptesting/application/modules/User/externals/images/friends/add.png","action":"add"}},"loggedin_user_id":24}
     * session_id : radk8sqp6gqifkh3lsolicftgh
     */

    private String session_id;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public static class ResultBean {
        /**
         * user_id : 47
         * title : Indian Moms
         * follow : {"action":"follow","text":"Follow"}
         * block : {"action":"unblock","text":"Unblock"}
         * mutualFriends : 0 mutual friends
         * user_image : http://sandbox.socialnetworking.solutions/apptesting/public/user/6d/03/c4e4ec5b2f094687678681d5921a7aa8.jpg
         * membership : {"label":"Add Friend","icon":"http://sandbox.socialnetworking.solutions/apptesting/application/modules/User/externals/images/friends/add.png","action":"add"}
         */

        private Notifications notification;
        private int loggedin_user_id;

        public Notifications getNotification() {
            return notification;
        }

        public void setNotification(Notifications notification) {
            this.notification = notification;
        }

        public int getLoggedin_user_id() {
            return loggedin_user_id;
        }

        public void setLoggedin_user_id(int loggedin_user_id) {
            this.loggedin_user_id = loggedin_user_id;
        }


    }
}

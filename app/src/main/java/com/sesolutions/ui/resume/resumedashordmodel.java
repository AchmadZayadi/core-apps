package com.sesolutions.ui.resume;

import java.util.List;

public class resumedashordmodel {


    /**
     * result : {"message":"","dashboardoptions":[{"name":"personal_information","action":"contact-information","label":"Personal Information"},{"name":"experience","action":"workexperience","label":"Work Experiences"},{"name":"education","action":"education","label":"Educations"},{"name":"project","action":"project","label":"Projects"},{"name":"certificate","action":"certificate","label":"Certificates"},{"name":"skills","action":"skills","label":"Skills, Interests, Strengths & Hobbies"},{"name":"references","action":"reference","label":"References"},{"name":"curricular","action":"achievements","label":"Achievements & Co-Curricular"},{"name":"objectives","action":"objectives","label":"Career Objectives, Date & Place & Declaration"}],"loggedin_user_id":10246}
     * session_id : 7qtebf8sogib2m86sst8sqh2di
     */

    private ResultBean result;
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
         * message :
         * dashboardoptions : [{"name":"personal_information","action":"contact-information","label":"Personal Information"},{"name":"experience","action":"workexperience","label":"Work Experiences"},{"name":"education","action":"education","label":"Educations"},{"name":"project","action":"project","label":"Projects"},{"name":"certificate","action":"certificate","label":"Certificates"},{"name":"skills","action":"skills","label":"Skills, Interests, Strengths & Hobbies"},{"name":"references","action":"reference","label":"References"},{"name":"curricular","action":"achievements","label":"Achievements & Co-Curricular"},{"name":"objectives","action":"objectives","label":"Career Objectives, Date & Place & Declaration"}]
         * loggedin_user_id : 10246
         */

        private String message;
        private int loggedin_user_id;
        private List<DashboardoptionsBean> dashboardoptions;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getLoggedin_user_id() {
            return loggedin_user_id;
        }

        public void setLoggedin_user_id(int loggedin_user_id) {
            this.loggedin_user_id = loggedin_user_id;
        }

        public List<DashboardoptionsBean> getDashboardoptions() {
            return dashboardoptions;
        }

        public void setDashboardoptions(List<DashboardoptionsBean> dashboardoptions) {
            this.dashboardoptions = dashboardoptions;
        }

        public static class DashboardoptionsBean {
            /**
             * name : personal_information
             * action : contact-information
             * label : Personal Information
             */

            private String name;
            private String action;
            private String label;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }
        }
    }
}

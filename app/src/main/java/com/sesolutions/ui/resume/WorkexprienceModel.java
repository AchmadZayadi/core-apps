package com.sesolutions.ui.resume;

import java.util.List;

public class WorkexprienceModel {


    /**
     * result : {"experiences":[{"experience_id":15,"resume_id":"32","title":"Software Engneer","company":"Info Soultion pvt. ltd.","location":"jaipur","fromyear":"2016","frommonth":"February","toyear":"0","tomonth":"February","currentlywork":1,"description":"ok","owner_type":"user","owner_id":10161,"creation_date":"2021-04-20 14:03:55","modified_date":"2021-04-20 14:03:55"}],"menus":[{"name":"add","label":"Add Experience"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}],"loggedin_user_id":10161,"total_page":1,"current_page":1,"next_page":2,"total":1}
     * session_id : luviced4pcsp3nephegmgv8fe2
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
         * experiences : [{"experience_id":15,"resume_id":"32","title":"Software Engneer","company":"Info Soultion pvt. ltd.","location":"jaipur","fromyear":"2016","frommonth":"February","toyear":"0","tomonth":"February","currentlywork":1,"description":"ok","owner_type":"user","owner_id":10161,"creation_date":"2021-04-20 14:03:55","modified_date":"2021-04-20 14:03:55"}]
         * menus : [{"name":"add","label":"Add Experience"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]
         * loggedin_user_id : 10161
         * total_page : 1
         * current_page : 1
         * next_page : 2
         * total : 1
         */

        private int loggedin_user_id;
        private int total_page;
        private int current_page;
        private int next_page;
        private int total;
        private List<ExperiencesBean> experiences;
        private List<MenusBean> menus;

        public int getLoggedin_user_id() {
            return loggedin_user_id;
        }

        public void setLoggedin_user_id(int loggedin_user_id) {
            this.loggedin_user_id = loggedin_user_id;
        }

        public int getTotal_page() {
            return total_page;
        }

        public void setTotal_page(int total_page) {
            this.total_page = total_page;
        }

        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public int getNext_page() {
            return next_page;
        }

        public void setNext_page(int next_page) {
            this.next_page = next_page;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<ExperiencesBean> getExperiences() {
            return experiences;
        }

        public void setExperiences(List<ExperiencesBean> experiences) {
            this.experiences = experiences;
        }

        public List<MenusBean> getMenus() {
            return menus;
        }

        public void setMenus(List<MenusBean> menus) {
            this.menus = menus;
        }

        public static class ExperiencesBean {
            /**
             * experience_id : 15
             * resume_id : 32
             * title : Software Engneer
             * company : Info Soultion pvt. ltd.
             * location : jaipur
             * fromyear : 2016
             * frommonth : February
             * toyear : 0
             * tomonth : February
             * currentlywork : 1
             * description : ok
             * owner_type : user
             * owner_id : 10161
             * creation_date : 2021-04-20 14:03:55
             * modified_date : 2021-04-20 14:03:55
             */

            private int experience_id;
            private String resume_id;
            private String title;
            private String company;
            private String location;
            private String fromyear;
            private String frommonth;
            private String toyear;
            private String tomonth;
            private int currentlywork;
            private String description;
            private String owner_type;
            private int owner_id;
            private String creation_date;
            private String modified_date;

            public int getExperience_id() {
                return experience_id;
            }

            public void setExperience_id(int experience_id) {
                this.experience_id = experience_id;
            }

            public String getResume_id() {
                return resume_id;
            }

            public void setResume_id(String resume_id) {
                this.resume_id = resume_id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getCompany() {
                return company;
            }

            public void setCompany(String company) {
                this.company = company;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getFromyear() {
                return fromyear;
            }

            public void setFromyear(String fromyear) {
                this.fromyear = fromyear;
            }

            public String getFrommonth() {
                return frommonth;
            }

            public void setFrommonth(String frommonth) {
                this.frommonth = frommonth;
            }

            public String getToyear() {
                return toyear;
            }

            public void setToyear(String toyear) {
                this.toyear = toyear;
            }

            public String getTomonth() {
                return tomonth;
            }

            public void setTomonth(String tomonth) {
                this.tomonth = tomonth;
            }

            public int getCurrentlywork() {
                return currentlywork;
            }

            public void setCurrentlywork(int currentlywork) {
                this.currentlywork = currentlywork;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getOwner_type() {
                return owner_type;
            }

            public void setOwner_type(String owner_type) {
                this.owner_type = owner_type;
            }

            public int getOwner_id() {
                return owner_id;
            }

            public void setOwner_id(int owner_id) {
                this.owner_id = owner_id;
            }

            public String getCreation_date() {
                return creation_date;
            }

            public void setCreation_date(String creation_date) {
                this.creation_date = creation_date;
            }

            public String getModified_date() {
                return modified_date;
            }

            public void setModified_date(String modified_date) {
                this.modified_date = modified_date;
            }
        }

        public static class MenusBean {
            /**
             * name : add
             * label : Add Experience
             */

            private String name;
            private String label;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
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

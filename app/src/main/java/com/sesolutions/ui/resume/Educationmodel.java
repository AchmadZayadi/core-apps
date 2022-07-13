package com.sesolutions.ui.resume;

import java.util.List;

public class Educationmodel {
    /**
     * result : {"educations":[{"education_id":18,"resume_id":"32","school":"Vivekanand senior secondary school","degree":"B.tech","field_of_study":"Mats","grade":"A","activities":"sports","fromyear":"2014","toyear":"2019","description":"okk","owner_type":"user","owner_id":10161,"creation_date":"2021-04-20 18:33:42","modified_date":"2021-04-20 18:33:42"}],"menus":[{"name":"add","label":"Add Experience"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}],"loggedin_user_id":10161,"total_page":1,"current_page":1,"next_page":2,"total":1}
     * session_id : bm6h8kul3p44p2nai5pvdaviv0
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
         * educations : [{"education_id":18,"resume_id":"32","school":"Vivekanand senior secondary school","degree":"B.tech","field_of_study":"Mats","grade":"A","activities":"sports","fromyear":"2014","toyear":"2019","description":"okk","owner_type":"user","owner_id":10161,"creation_date":"2021-04-20 18:33:42","modified_date":"2021-04-20 18:33:42"}]
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
        private List<EducationsBean> educations;
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

        public List<EducationsBean> getEducations() {
            return educations;
        }

        public void setEducations(List<EducationsBean> educations) {
            this.educations = educations;
        }

        public List<MenusBean> getMenus() {
            return menus;
        }

        public void setMenus(List<MenusBean> menus) {
            this.menus = menus;
        }

        public static class EducationsBean {
            /**
             * education_id : 18
             * resume_id : 32
             * school : Vivekanand senior secondary school
             * degree : B.tech
             * field_of_study : Mats
             * grade : A
             * activities : sports
             * fromyear : 2014
             * toyear : 2019
             * description : okk
             * owner_type : user
             * owner_id : 10161
             * creation_date : 2021-04-20 18:33:42
             * modified_date : 2021-04-20 18:33:42
             */

            private int education_id;
            private String resume_id;
            private String school;
            private String degree;
            private String field_of_study;
            private String grade;
            private String activities;
            private String fromyear;
            private String toyear;
            private String description;
            private String owner_type;
            private int owner_id;
            private String creation_date;
            private String modified_date;

            public int getEducation_id() {
                return education_id;
            }

            public void setEducation_id(int education_id) {
                this.education_id = education_id;
            }

            public String getResume_id() {
                return resume_id;
            }

            public void setResume_id(String resume_id) {
                this.resume_id = resume_id;
            }

            public String getSchool() {
                return school;
            }

            public void setSchool(String school) {
                this.school = school;
            }

            public String getDegree() {
                return degree;
            }

            public void setDegree(String degree) {
                this.degree = degree;
            }

            public String getField_of_study() {
                return field_of_study;
            }

            public void setField_of_study(String field_of_study) {
                this.field_of_study = field_of_study;
            }

            public String getGrade() {
                return grade;
            }

            public void setGrade(String grade) {
                this.grade = grade;
            }

            public String getActivities() {
                return activities;
            }

            public void setActivities(String activities) {
                this.activities = activities;
            }

            public String getFromyear() {
                return fromyear;
            }

            public void setFromyear(String fromyear) {
                this.fromyear = fromyear;
            }

            public String getToyear() {
                return toyear;
            }

            public void setToyear(String toyear) {
                this.toyear = toyear;
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

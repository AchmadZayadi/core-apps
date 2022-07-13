package com.sesolutions.ui.resume;

import java.util.List;

public class ResumeRefereneceModel {


    /**
     * result : {"references":[{"reference_id":7,"resume_id":"32","name":"Mark dew","designation":"Devloper","org_name":"Adani Group","email_id":"mark@gmail.com","mobile_number":"9782165008","owner_type":"user","owner_id":10161,"creation_date":"2021-04-21 15:37:51","modified_date":"2021-04-21 15:37:51","menus":[{"name":"add","label":"Add Reference"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}],"loggedin_user_id":10161,"total_page":1,"current_page":1,"next_page":2,"total":1}
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
         * references : [{"reference_id":7,"resume_id":"32","name":"Mark dew","designation":"Devloper","org_name":"Adani Group","email_id":"mark@gmail.com","mobile_number":"9782165008","owner_type":"user","owner_id":10161,"creation_date":"2021-04-21 15:37:51","modified_date":"2021-04-21 15:37:51","menus":[{"name":"add","label":"Add Reference"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}]
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
        private List<ReferencesBean> references;

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

        public List<ReferencesBean> getReferences() {
            return references;
        }

        public void setReferences(List<ReferencesBean> references) {
            this.references = references;
        }

        public static class ReferencesBean {
            /**
             * reference_id : 7
             * resume_id : 32
             * name : Mark dew
             * designation : Devloper
             * org_name : Adani Group
             * email_id : mark@gmail.com
             * mobile_number : 9782165008
             * owner_type : user
             * owner_id : 10161
             * creation_date : 2021-04-21 15:37:51
             * modified_date : 2021-04-21 15:37:51
             * menus : [{"name":"add","label":"Add Reference"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]
             */

            private int reference_id;
            private String resume_id;
            private String name;
            private String designation;
            private String org_name;
            private String email_id;
            private String mobile_number;
            private String owner_type;
            private int owner_id;
            private String creation_date;
            private String modified_date;
            private List<MenusBean> menus;

            public int getReference_id() {
                return reference_id;
            }

            public void setReference_id(int reference_id) {
                this.reference_id = reference_id;
            }

            public String getResume_id() {
                return resume_id;
            }

            public void setResume_id(String resume_id) {
                this.resume_id = resume_id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDesignation() {
                return designation;
            }

            public void setDesignation(String designation) {
                this.designation = designation;
            }

            public String getOrg_name() {
                return org_name;
            }

            public void setOrg_name(String org_name) {
                this.org_name = org_name;
            }

            public String getEmail_id() {
                return email_id;
            }

            public void setEmail_id(String email_id) {
                this.email_id = email_id;
            }

            public String getMobile_number() {
                return mobile_number;
            }

            public void setMobile_number(String mobile_number) {
                this.mobile_number = mobile_number;
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

            public List<MenusBean> getMenus() {
                return menus;
            }

            public void setMenus(List<MenusBean> menus) {
                this.menus = menus;
            }

            public static class MenusBean {
                /**
                 * name : add
                 * label : Add Reference
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
}

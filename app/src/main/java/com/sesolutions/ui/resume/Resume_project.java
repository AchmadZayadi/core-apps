package com.sesolutions.ui.resume;

import java.util.List;

public class Resume_project {


    /**
     * result : {"projects":[{"project_id":24,"resume_id":42,"title":"gvggg","fromyear":"2018","frommonth":"March","toyear":"2021","tomonth":"January","currentlywork":0,"project_url":"","description":"ggh","owner_type":"user","owner_id":10161,"creation_date":"2021-04-23 14:31:17","modified_date":"2021-04-23 14:31:20","photo_id":10970,"menus":[{"name":"add","label":"Add Project","image_url":"https://media.vavci.com/public/eresume_project/05/2b/78890ecbcbdbf58755ee2267e8c16fbb.pdf"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"},{"name":"download","label":"Download","image_url":"https://media.vavci.com/public/eresume_project/05/2b/78890ecbcbdbf58755ee2267e8c16fbb.pdf"}]},{"project_id":23,"resume_id":42,"title":"vavco","fromyear":"2017","frommonth":"March","toyear":"2020","tomonth":"July","currentlywork":0,"project_url":"","description":"baja","owner_type":"user","owner_id":10161,"creation_date":"2021-04-23 14:15:53","modified_date":"2021-04-23 14:15:53","photo_id":0,"menus":[{"name":"add","label":"Add Project"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]},{"project_id":20,"resume_id":42,"title":"Vavci","fromyear":"2018","frommonth":"February","toyear":"2021","tomonth":"July","currentlywork":0,"project_url":"https://google.com","description":"data","owner_type":"user","owner_id":10161,"creation_date":"2021-04-22 15:42:48","modified_date":"2021-04-22 15:46:13","photo_id":0,"menus":[{"name":"add","label":"Add Project"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}],"loggedin_user_id":10161,"total_page":1,"current_page":1,"next_page":2,"total":3}
     * session_id : gvtf9g22j1etjmd0oosoq9r2uf
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
         * projects : [{"project_id":24,"resume_id":42,"title":"gvggg","fromyear":"2018","frommonth":"March","toyear":"2021","tomonth":"January","currentlywork":0,"project_url":"","description":"ggh","owner_type":"user","owner_id":10161,"creation_date":"2021-04-23 14:31:17","modified_date":"2021-04-23 14:31:20","photo_id":10970,"menus":[{"name":"add","label":"Add Project"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"},{"name":"download","label":"Download","image_url":"https://media.vavci.com/public/eresume_project/05/2b/78890ecbcbdbf58755ee2267e8c16fbb.pdf"}]},{"project_id":23,"resume_id":42,"title":"vavco","fromyear":"2017","frommonth":"March","toyear":"2020","tomonth":"July","currentlywork":0,"project_url":"","description":"baja","owner_type":"user","owner_id":10161,"creation_date":"2021-04-23 14:15:53","modified_date":"2021-04-23 14:15:53","photo_id":0,"menus":[{"name":"add","label":"Add Project"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]},{"project_id":20,"resume_id":42,"title":"Vavci","fromyear":"2018","frommonth":"February","toyear":"2021","tomonth":"July","currentlywork":0,"project_url":"https://google.com","description":"data","owner_type":"user","owner_id":10161,"creation_date":"2021-04-22 15:42:48","modified_date":"2021-04-22 15:46:13","photo_id":0,"menus":[{"name":"add","label":"Add Project"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}]
         * loggedin_user_id : 10161
         * total_page : 1
         * current_page : 1
         * next_page : 2
         * total : 3
         */

        private int loggedin_user_id;
        private int total_page;
        private int current_page;
        private int next_page;
        private int total;
        private List<ProjectsBean> projects;

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

        public List<ProjectsBean> getProjects() {
            return projects;
        }

        public void setProjects(List<ProjectsBean> projects) {
            this.projects = projects;
        }

        public static class ProjectsBean {
            /**
             * project_id : 24
             * resume_id : 42
             * title : gvggg
             * fromyear : 2018
             * frommonth : March
             * toyear : 2021
             * tomonth : January
             * currentlywork : 0
             * project_url :
             * description : ggh
             * owner_type : user
             * owner_id : 10161
             * creation_date : 2021-04-23 14:31:17
             * modified_date : 2021-04-23 14:31:20
             * photo_id : 10970
             * menus : [{"name":"add","label":"Add Project"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"},{"name":"download","label":"Download","image_url":"https://media.vavci.com/public/eresume_project/05/2b/78890ecbcbdbf58755ee2267e8c16fbb.pdf"}]
             */

            private int project_id;
            private int resume_id;
            private String title;
            private String fromyear;
            private String frommonth;
            private String toyear;
            private String tomonth;
            private int currentlywork;
            private String project_url;
            private String description;
            private String owner_type;
            private int owner_id;
            private String creation_date;
            private String modified_date;
            private int photo_id;
            private List<MenusBean> menus;

            public int getProject_id() {
                return project_id;
            }

            public void setProject_id(int project_id) {
                this.project_id = project_id;
            }

            public int getResume_id() {
                return resume_id;
            }

            public void setResume_id(int resume_id) {
                this.resume_id = resume_id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
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

            public String getProject_url() {
                return project_url;
            }

            public void setProject_url(String project_url) {
                this.project_url = project_url;
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

            public int getPhoto_id() {
                return photo_id;
            }

            public void setPhoto_id(int photo_id) {
                this.photo_id = photo_id;
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
                 * label : Add Project
                 * image_url : https://media.vavci.com/public/eresume_project/05/2b/78890ecbcbdbf58755ee2267e8c16fbb.pdf
                 */

                private String name;
                private String label;
                private String image_url;

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

                public String getImage_url() {
                    return image_url;
                }

                public void setImage_url(String image_url) {
                    this.image_url = image_url;
                }
            }
        }
    }
}

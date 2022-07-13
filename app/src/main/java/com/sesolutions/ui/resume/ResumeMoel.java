package com.sesolutions.ui.resume;

import java.util.List;

public class ResumeMoel {


    /**
     * result : {"resumes":[{"resume_id":1,"title":"Software Engineer","owner_id":10001,"template_id":18,"menus":[{"name":"preview","resume_id":1,"label":"Preview"},{"name":"download","resume_id":1,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":1,"label":"Edit"},{"name":"editinformation","resume_id":1,"label":"Edit Resume Information"},{"name":"delete","resume_id":1,"label":"Delete"}]},{"resume_id":2,"title":"Web Designer","owner_id":10001,"template_id":1,"menus":[{"name":"preview","resume_id":2,"label":"Preview"},{"name":"download","resume_id":2,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":2,"label":"Edit"},{"name":"editinformation","resume_id":2,"label":"Edit Resume Information"},{"name":"delete","resume_id":2,"label":"Delete"}]},{"resume_id":3,"title":"Nitesh Test Resume","owner_id":10001,"template_id":1,"menus":[{"name":"preview","resume_id":3,"label":"Preview"},{"name":"download","resume_id":3,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":3,"label":"Edit"},{"name":"editinformation","resume_id":3,"label":"Edit Resume Information"},{"name":"delete","resume_id":3,"label":"Delete"}]},{"resume_id":15,"title":"Test with Ranjit JI","owner_id":10001,"template_id":1,"menus":[{"name":"preview","resume_id":15,"label":"Preview"},{"name":"download","resume_id":15,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":15,"label":"Edit"},{"name":"editinformation","resume_id":15,"label":"Edit Resume Information"},{"name":"delete","resume_id":15,"label":"Delete"}]},{"resume_id":18,"title":"Project Manager","owner_id":10001,"template_id":1,"menus":[{"name":"preview","resume_id":18,"label":"Preview"},{"name":"download","resume_id":18,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":18,"label":"Edit"},{"name":"editinformation","resume_id":18,"label":"Edit Resume Information"},{"name":"delete","resume_id":18,"label":"Delete"}]},{"resume_id":26,"title":"Electrical Engineer","owner_id":10001,"template_id":1,"menus":[{"name":"preview","resume_id":26,"label":"Preview"},{"name":"download","resume_id":26,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":26,"label":"Edit"},{"name":"editinformation","resume_id":26,"label":"Edit Resume Information"},{"name":"delete","resume_id":26,"label":"Delete"}]},{"resume_id":28,"title":"Raj","owner_id":10001,"template_id":1,"menus":[{"name":"preview","resume_id":28,"label":"Preview"},{"name":"download","resume_id":28,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":28,"label":"Edit"},{"name":"editinformation","resume_id":28,"label":"Edit Resume Information"},{"name":"delete","resume_id":28,"label":"Delete"}]},{"resume_id":29,"title":"Testing","owner_id":10001,"template_id":1,"menus":[{"name":"preview","resume_id":29,"label":"Preview"},{"name":"download","resume_id":29,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":29,"label":"Edit"},{"name":"editinformation","resume_id":29,"label":"Edit Resume Information"},{"name":"delete","resume_id":29,"label":"Delete"}]}],"loggedin_user_id":10001,"total_page":1,"current_page":1,"next_page":2,"total":8}
     * session_id : ahaqrd4mnb5nklli1mvabg57fm
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
         * resumes : [{"resume_id":1,"title":"Software Engineer","owner_id":10001,"template_id":18,"menus":[{"name":"preview","resume_id":1,"label":"Preview"},{"name":"download","resume_id":1,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":1,"label":"Edit"},{"name":"editinformation","resume_id":1,"label":"Edit Resume Information"},{"name":"delete","resume_id":1,"label":"Delete"}]},{"resume_id":2,"title":"Web Designer","owner_id":10001,"template_id":1,"menus":[{"name":"preview","resume_id":2,"label":"Preview"},{"name":"download","resume_id":2,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":2,"label":"Edit"},{"name":"editinformation","resume_id":2,"label":"Edit Resume Information"},{"name":"delete","resume_id":2,"label":"Delete"}]},{"resume_id":3,"title":"Nitesh Test Resume","owner_id":10001,"template_id":1,"menus":[{"name":"preview","resume_id":3,"label":"Preview"},{"name":"download","resume_id":3,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":3,"label":"Edit"},{"name":"editinformation","resume_id":3,"label":"Edit Resume Information"},{"name":"delete","resume_id":3,"label":"Delete"}]},{"resume_id":15,"title":"Test with Ranjit JI","owner_id":10001,"template_id":1,"menus":[{"name":"preview","resume_id":15,"label":"Preview"},{"name":"download","resume_id":15,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":15,"label":"Edit"},{"name":"editinformation","resume_id":15,"label":"Edit Resume Information"},{"name":"delete","resume_id":15,"label":"Delete"}]},{"resume_id":18,"title":"Project Manager","owner_id":10001,"template_id":1,"menus":[{"name":"preview","resume_id":18,"label":"Preview"},{"name":"download","resume_id":18,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":18,"label":"Edit"},{"name":"editinformation","resume_id":18,"label":"Edit Resume Information"},{"name":"delete","resume_id":18,"label":"Delete"}]},{"resume_id":26,"title":"Electrical Engineer","owner_id":10001,"template_id":1,"menus":[{"name":"preview","resume_id":26,"label":"Preview"},{"name":"download","resume_id":26,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":26,"label":"Edit"},{"name":"editinformation","resume_id":26,"label":"Edit Resume Information"},{"name":"delete","resume_id":26,"label":"Delete"}]},{"resume_id":28,"title":"Raj","owner_id":10001,"template_id":1,"menus":[{"name":"preview","resume_id":28,"label":"Preview"},{"name":"download","resume_id":28,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":28,"label":"Edit"},{"name":"editinformation","resume_id":28,"label":"Edit Resume Information"},{"name":"delete","resume_id":28,"label":"Delete"}]},{"resume_id":29,"title":"Testing","owner_id":10001,"template_id":1,"menus":[{"name":"preview","resume_id":29,"label":"Preview"},{"name":"download","resume_id":29,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":29,"label":"Edit"},{"name":"editinformation","resume_id":29,"label":"Edit Resume Information"},{"name":"delete","resume_id":29,"label":"Delete"}]}]
         * loggedin_user_id : 10001
         * total_page : 1
         * current_page : 1
         * next_page : 2
         * total : 8
         */

        private int loggedin_user_id;
        private int total_page;
        private int canCreate;
        private int current_page;
        private int next_page;
        private int total;
        private List<ResumesBean> resumes;

        public int getLoggedin_user_id() {
            return loggedin_user_id;
        }

        public void setLoggedin_user_id(int loggedin_user_id) {
            this.loggedin_user_id = loggedin_user_id;
        }

        public int getCanCreate() {
            return canCreate;
        }

        public void setCanCreate(int canCreate) {
            this.canCreate = canCreate;
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

        public List<ResumesBean> getResumes() {
            return resumes;
        }

        public void setResumes(List<ResumesBean> resumes) {
            this.resumes = resumes;
        }

        public static class ResumesBean {
            /**
             * resume_id : 1
             * title : Software Engineer
             * owner_id : 10001
             * template_id : 18
             * menus : [{"name":"preview","resume_id":1,"label":"Preview"},{"name":"download","resume_id":1,"label":"Download Resume"},{"name":"create","label":"Create Resume"},{"name":"edit","resume_id":1,"label":"Edit"},{"name":"editinformation","resume_id":1,"label":"Edit Resume Information"},{"name":"delete","resume_id":1,"label":"Delete"}]
             */

            private int resume_id;
            private String title;
            private int owner_id;
            private int template_id;
            private List<MenusBean> menus;

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

            public int getOwner_id() {
                return owner_id;
            }

            public void setOwner_id(int owner_id) {
                this.owner_id = owner_id;
            }

            public int getTemplate_id() {
                return template_id;
            }

            public void setTemplate_id(int template_id) {
                this.template_id = template_id;
            }

            public List<MenusBean> getMenus() {
                return menus;
            }

            public void setMenus(List<MenusBean> menus) {
                this.menus = menus;
            }

            public static class MenusBean {
                /**
                 * name : preview
                 * resume_id : 1
                 * label : Preview
                 */

                private String name;
                private int resume_id;
                private String label;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getResume_id() {
                    return resume_id;
                }

                public void setResume_id(int resume_id) {
                    this.resume_id = resume_id;
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

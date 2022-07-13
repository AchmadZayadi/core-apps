package com.sesolutions.ui.resume;

import java.util.List;

public class AchivementParentModel {


    /**
     * result : {"achievements":[{"achievement_id":9,"resume_id":42,"achievementname":"manage","user_id":10161,"menus":[{"name":"add","label":"Add Achievement"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]},{"achievement_id":10,"resume_id":42,"achievementname":"achive 2","user_id":10161,"menus":[{"name":"add","label":"Add Achievement"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}],"curriculars":[{"curricular_id":7,"resume_id":42,"curricularname":"act1","user_id":10161,"menus":[{"name":"add","label":"Add Curricular Activities"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]},{"curricular_id":8,"resume_id":42,"curricularname":"act 2","user_id":10161,"menus":[{"name":"add","label":"Add Curricular Activities"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}],"loggedin_user_id":10161,"total_page":1,"current_page":1,"next_page":2,"total":2}
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
         * achievements : [{"achievement_id":9,"resume_id":42,"achievementname":"manage","user_id":10161,"menus":[{"name":"add","label":"Add Achievement"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]},{"achievement_id":10,"resume_id":42,"achievementname":"achive 2","user_id":10161,"menus":[{"name":"add","label":"Add Achievement"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}]
         * curriculars : [{"curricular_id":7,"resume_id":42,"curricularname":"act1","user_id":10161,"menus":[{"name":"add","label":"Add Curricular Activities"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]},{"curricular_id":8,"resume_id":42,"curricularname":"act 2","user_id":10161,"menus":[{"name":"add","label":"Add Curricular Activities"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}]
         * loggedin_user_id : 10161
         * total_page : 1
         * current_page : 1
         * next_page : 2
         * total : 2
         */

        private int loggedin_user_id;
        private int total_page;
        private int current_page;
        private int next_page;
        private int total;
        private List<AchievementsBean> achievements;
        private List<CurricularsBean> curriculars;

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

        public List<AchievementsBean> getAchievements() {
            return achievements;
        }

        public void setAchievements(List<AchievementsBean> achievements) {
            this.achievements = achievements;
        }

        public List<CurricularsBean> getCurriculars() {
            return curriculars;
        }

        public void setCurriculars(List<CurricularsBean> curriculars) {
            this.curriculars = curriculars;
        }

        public static class AchievementsBean {
            /**
             * achievement_id : 9
             * resume_id : 42
             * achievementname : manage
             * user_id : 10161
             * menus : [{"name":"add","label":"Add Achievement"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]
             */

            private int achievement_id;
            private int resume_id;
            private String achievementname;
            private int user_id;
            private List<MenusBean> menus;

            public int getAchievement_id() {
                return achievement_id;
            }

            public void setAchievement_id(int achievement_id) {
                this.achievement_id = achievement_id;
            }

            public int getResume_id() {
                return resume_id;
            }

            public void setResume_id(int resume_id) {
                this.resume_id = resume_id;
            }

            public String getAchievementname() {
                return achievementname;
            }

            public void setAchievementname(String achievementname) {
                this.achievementname = achievementname;
            }

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
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
                 * label : Add Achievement
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

        public static class CurricularsBean {
            /**
             * curricular_id : 7
             * resume_id : 42
             * curricularname : act1
             * user_id : 10161
             * menus : [{"name":"add","label":"Add Curricular Activities"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]
             */

            private int curricular_id;
            private int resume_id;
            private String curricularname;
            private int user_id;
            private List<MenusBeanX> menus;

            public int getCurricular_id() {
                return curricular_id;
            }

            public void setCurricular_id(int curricular_id) {
                this.curricular_id = curricular_id;
            }

            public int getResume_id() {
                return resume_id;
            }

            public void setResume_id(int resume_id) {
                this.resume_id = resume_id;
            }

            public String getCurricularname() {
                return curricularname;
            }

            public void setCurricularname(String curricularname) {
                this.curricularname = curricularname;
            }

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public List<MenusBeanX> getMenus() {
                return menus;
            }

            public void setMenus(List<MenusBeanX> menus) {
                this.menus = menus;
            }

            public static class MenusBeanX {
                /**
                 * name : add
                 * label : Add Curricular Activities
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

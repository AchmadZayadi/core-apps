package com.sesolutions.ui.resume;

import java.util.List;

public class SkillParentModel {


    /**
     * result : {"skills":[{"skill_id":33,"resume_id":42,"skillname":"php","user_id":10161,"rating":4,"menus":[{"name":"add","label":"Add Skills"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]},{"skill_id":34,"resume_id":42,"skillname":"java","user_id":10161,"rating":4,"menus":[{"name":"add","label":"Add Skills"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}],"interests":[{"interest_id":11,"resume_id":42,"interestname":"interests","user_id":10161,"menus":[{"name":"add","label":"Add Interests"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]},{"interest_id":12,"resume_id":42,"interestname":"sports","user_id":10161,"menus":[{"name":"add","label":"Add Interests"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}],"strengths":[{"strength_id":13,"resume_id":42,"strengthname":"okok","user_id":10161,"menus":[{"name":"add","label":"Add Strengths"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]},{"strength_id":14,"resume_id":42,"strengthname":"fine","user_id":10161,"menus":[{"name":"add","label":"Add Strengths"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}],"hobbies":[{"hobbie_id":10,"resume_id":42,"hobbiename":"Testing adsasd ","user_id":10161,"menus":[{"name":"add","label":"Add Hobbies"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}],"loggedin_user_id":10161}
     * session_id : l7drc01e8l28kf9fjhj08of09n
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
         * skills : [{"skill_id":33,"resume_id":42,"skillname":"php","user_id":10161,"rating":4,"menus":[{"name":"add","label":"Add Skills"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]},{"skill_id":34,"resume_id":42,"skillname":"java","user_id":10161,"rating":4,"menus":[{"name":"add","label":"Add Skills"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}]
         * interests : [{"interest_id":11,"resume_id":42,"interestname":"interests","user_id":10161,"menus":[{"name":"add","label":"Add Interests"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]},{"interest_id":12,"resume_id":42,"interestname":"sports","user_id":10161,"menus":[{"name":"add","label":"Add Interests"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}]
         * strengths : [{"strength_id":13,"resume_id":42,"strengthname":"okok","user_id":10161,"menus":[{"name":"add","label":"Add Strengths"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]},{"strength_id":14,"resume_id":42,"strengthname":"fine","user_id":10161,"menus":[{"name":"add","label":"Add Strengths"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}]
         * hobbies : [{"hobbie_id":10,"resume_id":42,"hobbiename":"Testing adsasd ","user_id":10161,"menus":[{"name":"add","label":"Add Hobbies"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}]
         * loggedin_user_id : 10161
         */

        private int loggedin_user_id;
        private List<SkillsBean> skills;
        private List<InterestsBean> interests;
        private List<StrengthsBean> strengths;
        private List<HobbiesBean> hobbies;

        public int getLoggedin_user_id() {
            return loggedin_user_id;
        }

        public void setLoggedin_user_id(int loggedin_user_id) {
            this.loggedin_user_id = loggedin_user_id;
        }

        public List<SkillsBean> getSkills() {
            return skills;
        }

        public void setSkills(List<SkillsBean> skills) {
            this.skills = skills;
        }

        public List<InterestsBean> getInterests() {
            return interests;
        }

        public void setInterests(List<InterestsBean> interests) {
            this.interests = interests;
        }

        public List<StrengthsBean> getStrengths() {
            return strengths;
        }

        public void setStrengths(List<StrengthsBean> strengths) {
            this.strengths = strengths;
        }

        public List<HobbiesBean> getHobbies() {
            return hobbies;
        }

        public void setHobbies(List<HobbiesBean> hobbies) {
            this.hobbies = hobbies;
        }

        public static class SkillsBean {
            /**
             * skill_id : 33
             * resume_id : 42
             * skillname : php
             * user_id : 10161
             * rating : 4
             * menus : [{"name":"add","label":"Add Skills"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]
             */

            private int skill_id;
            private int resume_id;
            private String skillname;
            private int user_id;
            private int rating;
            private List<MenusBean> menus;

            public int getSkill_id() {
                return skill_id;
            }

            public void setSkill_id(int skill_id) {
                this.skill_id = skill_id;
            }

            public int getResume_id() {
                return resume_id;
            }

            public void setResume_id(int resume_id) {
                this.resume_id = resume_id;
            }

            public String getSkillname() {
                return skillname;
            }

            public void setSkillname(String skillname) {
                this.skillname = skillname;
            }

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public int getRating() {
                return rating;
            }

            public void setRating(int rating) {
                this.rating = rating;
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
                 * label : Add Skills
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

        public static class InterestsBean {
            /**
             * interest_id : 11
             * resume_id : 42
             * interestname : interests
             * user_id : 10161
             * menus : [{"name":"add","label":"Add Interests"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]
             */

            private int interest_id;
            private int resume_id;
            private String interestname;
            private int user_id;
            private List<MenusBeanX> menus;

            public int getInterest_id() {
                return interest_id;
            }

            public void setInterest_id(int interest_id) {
                this.interest_id = interest_id;
            }

            public int getResume_id() {
                return resume_id;
            }

            public void setResume_id(int resume_id) {
                this.resume_id = resume_id;
            }

            public String getInterestname() {
                return interestname;
            }

            public void setInterestname(String interestname) {
                this.interestname = interestname;
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
                 * label : Add Interests
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

        public static class StrengthsBean {
            /**
             * strength_id : 13
             * resume_id : 42
             * strengthname : okok
             * user_id : 10161
             * menus : [{"name":"add","label":"Add Strengths"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]
             */

            private int strength_id;
            private int resume_id;
            private String strengthname;
            private int user_id;
            private List<MenusBeanXX> menus;

            public int getStrength_id() {
                return strength_id;
            }

            public void setStrength_id(int strength_id) {
                this.strength_id = strength_id;
            }

            public int getResume_id() {
                return resume_id;
            }

            public void setResume_id(int resume_id) {
                this.resume_id = resume_id;
            }

            public String getStrengthname() {
                return strengthname;
            }

            public void setStrengthname(String strengthname) {
                this.strengthname = strengthname;
            }

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public List<MenusBeanXX> getMenus() {
                return menus;
            }

            public void setMenus(List<MenusBeanXX> menus) {
                this.menus = menus;
            }

            public static class MenusBeanXX {
                /**
                 * name : add
                 * label : Add Strengths
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

        public static class HobbiesBean {
            /**
             * hobbie_id : 10
             * resume_id : 42
             * hobbiename : Testing adsasd
             * user_id : 10161
             * menus : [{"name":"add","label":"Add Hobbies"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]
             */

            private int hobbie_id;
            private int resume_id;
            private String hobbiename;
            private int user_id;
            private List<MenusBeanXXX> menus;

            public int getHobbie_id() {
                return hobbie_id;
            }

            public void setHobbie_id(int hobbie_id) {
                this.hobbie_id = hobbie_id;
            }

            public int getResume_id() {
                return resume_id;
            }

            public void setResume_id(int resume_id) {
                this.resume_id = resume_id;
            }

            public String getHobbiename() {
                return hobbiename;
            }

            public void setHobbiename(String hobbiename) {
                this.hobbiename = hobbiename;
            }

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public List<MenusBeanXXX> getMenus() {
                return menus;
            }

            public void setMenus(List<MenusBeanXXX> menus) {
                this.menus = menus;
            }

            public static class MenusBeanXXX {
                /**
                 * name : add
                 * label : Add Hobbies
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

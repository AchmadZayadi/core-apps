package com.sesolutions.ui.clickclick.notification;

public class FollowandUnfollow {


    /**
     * result : {"member":{"user_id":219,"title":"Maria Cruze","mutualFriends":"0 mutual friends","follow":{"action":"follow","text":"Follow"},"user_image":"https://d1q7rcky2z4kkl.cloudfront.net/public/user/6a/14/8aeb4064620d2d1b8f61dd4e09fd880f.jpg","membership":{"label":"Add Friend","icon":"https://frendzit.com/application/modules/User/externals/images/friends/add.png","action":"add"}},"loggedin_user_id":220}
     * session_id : 96f4t48ch1fjtm91bftbvsp37n
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
         * member : {"user_id":219,"title":"Maria Cruze","mutualFriends":"0 mutual friends","follow":{"action":"follow","text":"Follow"},"user_image":"https://d1q7rcky2z4kkl.cloudfront.net/public/user/6a/14/8aeb4064620d2d1b8f61dd4e09fd880f.jpg","membership":{"label":"Add Friend","icon":"https://frendzit.com/application/modules/User/externals/images/friends/add.png","action":"add"}}
         * loggedin_user_id : 220
         */

        private MemberBean member;
        private int loggedin_user_id;

        public MemberBean getMember() {
            return member;
        }

        public void setMember(MemberBean member) {
            this.member = member;
        }

        public int getLoggedin_user_id() {
            return loggedin_user_id;
        }

        public void setLoggedin_user_id(int loggedin_user_id) {
            this.loggedin_user_id = loggedin_user_id;
        }

        public static class MemberBean {
            /**
             * user_id : 219
             * title : Maria Cruze
             * mutualFriends : 0 mutual friends
             * follow : {"action":"follow","text":"Follow"}
             * user_image : https://d1q7rcky2z4kkl.cloudfront.net/public/user/6a/14/8aeb4064620d2d1b8f61dd4e09fd880f.jpg
             * membership : {"label":"Add Friend","icon":"https://frendzit.com/application/modules/User/externals/images/friends/add.png","action":"add"}
             */

            private int user_id;
            private String title;
            private String mutualFriends;
            private FollowBean follow;
            private String user_image;

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getMutualFriends() {
                return mutualFriends;
            }

            public void setMutualFriends(String mutualFriends) {
                this.mutualFriends = mutualFriends;
            }

            public FollowBean getFollow() {
                return follow;
            }

            public void setFollow(FollowBean follow) {
                this.follow = follow;
            }

            public String getUser_image() {
                return user_image;
            }

            public void setUser_image(String user_image) {
                this.user_image = user_image;
            }


            public static class FollowBean {
                /**
                 * action : follow
                 * text : Follow
                 */

                private String action;
                private String text;

                public String getAction() {
                    return action;
                }

                public void setAction(String action) {
                    this.action = action;
                }

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }
            }

            public static class MembershipBean {
                /**
                 * label : Add Friend
                 * icon : https://frendzit.com/application/modules/User/externals/images/friends/add.png
                 * action : add
                 */

                private String label;
                private String icon;
                private String action;

                public String getLabel() {
                    return label;
                }

                public void setLabel(String label) {
                    this.label = label;
                }

                public String getIcon() {
                    return icon;
                }

                public void setIcon(String icon) {
                    this.icon = icon;
                }

                public String getAction() {
                    return action;
                }

                public void setAction(String action) {
                    this.action = action;
                }
            }
        }
    }
}

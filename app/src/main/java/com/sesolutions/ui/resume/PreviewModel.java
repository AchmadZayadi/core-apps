package com.sesolutions.ui.resume;

import java.util.List;

public class PreviewModel {


    /**
     * result : {"template_id":[{"id":1,"url":"https://vavci.com/application/modules/Eresume/externals/images/template1.png"},{"id":2,"url":"https://vavci.com/application/modules/Eresume/externals/images/template2.png"},{"id":3,"url":"https://vavci.com/application/modules/Eresume/externals/images/template3.png"},{"id":4,"url":"https://vavci.com/application/modules/Eresume/externals/images/template4.png"},{"id":5,"url":"https://vavci.com/application/modules/Eresume/externals/images/template5.png"},{"id":6,"url":"https://vavci.com/application/modules/Eresume/externals/images/template6.png"},{"id":7,"url":"https://vavci.com/application/modules/Eresume/externals/images/template7.png"},{"id":8,"url":"https://vavci.com/application/modules/Eresume/externals/images/template8.png"},{"id":9,"url":"https://vavci.com/application/modules/Eresume/externals/images/template9.png"},{"id":10,"url":"https://vavci.com/application/modules/Eresume/externals/images/template10.png"},{"id":11,"url":"https://vavci.com/application/modules/Eresume/externals/images/template11.png"},{"id":12,"url":"https://vavci.com/application/modules/Eresume/externals/images/template12.png"},{"id":13,"url":"https://vavci.com/application/modules/Eresume/externals/images/template13.png"},{"id":14,"url":"https://vavci.com/application/modules/Eresume/externals/images/template14.png"},{"id":15,"url":"https://vavci.com/application/modules/Eresume/externals/images/template15.png"},{"id":16,"url":"https://vavci.com/application/modules/Eresume/externals/images/template16.png"},{"id":17,"url":"https://vavci.com/application/modules/Eresume/externals/images/template17.png"},{"id":18,"url":"https://vavci.com/application/modules/Eresume/externals/images/template18.png"},{"id":19,"url":"https://vavci.com/application/modules/Eresume/externals/images/template19.png"},{"id":20,"url":"https://vavci.com/application/modules/Eresume/externals/images/template20.png"}],"loggedin_user_id":10161}
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
         * template_id : [{"id":1,"url":"https://vavci.com/application/modules/Eresume/externals/images/template1.png"},{"id":2,"url":"https://vavci.com/application/modules/Eresume/externals/images/template2.png"},{"id":3,"url":"https://vavci.com/application/modules/Eresume/externals/images/template3.png"},{"id":4,"url":"https://vavci.com/application/modules/Eresume/externals/images/template4.png"},{"id":5,"url":"https://vavci.com/application/modules/Eresume/externals/images/template5.png"},{"id":6,"url":"https://vavci.com/application/modules/Eresume/externals/images/template6.png"},{"id":7,"url":"https://vavci.com/application/modules/Eresume/externals/images/template7.png"},{"id":8,"url":"https://vavci.com/application/modules/Eresume/externals/images/template8.png"},{"id":9,"url":"https://vavci.com/application/modules/Eresume/externals/images/template9.png"},{"id":10,"url":"https://vavci.com/application/modules/Eresume/externals/images/template10.png"},{"id":11,"url":"https://vavci.com/application/modules/Eresume/externals/images/template11.png"},{"id":12,"url":"https://vavci.com/application/modules/Eresume/externals/images/template12.png"},{"id":13,"url":"https://vavci.com/application/modules/Eresume/externals/images/template13.png"},{"id":14,"url":"https://vavci.com/application/modules/Eresume/externals/images/template14.png"},{"id":15,"url":"https://vavci.com/application/modules/Eresume/externals/images/template15.png"},{"id":16,"url":"https://vavci.com/application/modules/Eresume/externals/images/template16.png"},{"id":17,"url":"https://vavci.com/application/modules/Eresume/externals/images/template17.png"},{"id":18,"url":"https://vavci.com/application/modules/Eresume/externals/images/template18.png"},{"id":19,"url":"https://vavci.com/application/modules/Eresume/externals/images/template19.png"},{"id":20,"url":"https://vavci.com/application/modules/Eresume/externals/images/template20.png"}]
         * loggedin_user_id : 10161
         */

        private int loggedin_user_id;
        private List<TemplateIdBean> template_id;

        public int getLoggedin_user_id() {
            return loggedin_user_id;
        }

        public void setLoggedin_user_id(int loggedin_user_id) {
            this.loggedin_user_id = loggedin_user_id;
        }

        public List<TemplateIdBean> getTemplate_id() {
            return template_id;
        }

        public void setTemplate_id(List<TemplateIdBean> template_id) {
            this.template_id = template_id;
        }

        public static class TemplateIdBean {
            /**
             * id : 1
             * url : https://vavci.com/application/modules/Eresume/externals/images/template1.png
             */

            private int id;
            private String url;
            private Boolean ischeck;

            {
                ischeck=false;
            }

            public Boolean getIscheck() {
                return ischeck;
            }

            public void setIscheck(Boolean ischeck) {
                this.ischeck = ischeck;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}

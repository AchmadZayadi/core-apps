package com.sesolutions.ui.resume;

import java.util.List;

public class ResumeCertificateModel {


    /**
     * result : {"certificates":[{"certificate_id":17,"resume_id":32,"title":"Robtics Lab","certificateid":"R0091","photo_id":0,"owner_type":"user","owner_id":10161,"creation_date":"2021-04-21 13:13:02","modified_date":"2021-04-21 13:13:02","image_url":"https://vavci.com/application/modules/Eresume/externals/images/certificate-thumb.png","menus":[{"name":"add","label":"Add Certificate"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]},{"certificate_id":16,"resume_id":32,"title":"SSL certificate","certificateid":"CERT1002","photo_id":10922,"owner_type":"user","owner_id":10161,"creation_date":"2021-04-21 11:38:11","modified_date":"2021-04-21 11:38:12","image_url":"https://media.vavci.com/public/eresume_certificate/d4/2a/53cd6c22d49b9bd680633090a33ca306.png","menus":[{"name":"add","label":"Add Certificate"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}],"loggedin_user_id":10161,"total_page":1,"current_page":1,"next_page":2,"total":2}
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
         * certificates : [{"certificate_id":17,"resume_id":32,"title":"Robtics Lab","certificateid":"R0091","photo_id":0,"owner_type":"user","owner_id":10161,"creation_date":"2021-04-21 13:13:02","modified_date":"2021-04-21 13:13:02","image_url":"https://vavci.com/application/modules/Eresume/externals/images/certificate-thumb.png","menus":[{"name":"add","label":"Add Certificate"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]},{"certificate_id":16,"resume_id":32,"title":"SSL certificate","certificateid":"CERT1002","photo_id":10922,"owner_type":"user","owner_id":10161,"creation_date":"2021-04-21 11:38:11","modified_date":"2021-04-21 11:38:12","image_url":"https://media.vavci.com/public/eresume_certificate/d4/2a/53cd6c22d49b9bd680633090a33ca306.png","menus":[{"name":"add","label":"Add Certificate"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]}]
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
        private List<CertificatesBean> certificates;

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

        public List<CertificatesBean> getCertificates() {
            return certificates;
        }

        public void setCertificates(List<CertificatesBean> certificates) {
            this.certificates = certificates;
        }

        public static class CertificatesBean {
            /**
             * certificate_id : 17
             * resume_id : 32
             * title : Robtics Lab
             * certificateid : R0091
             * photo_id : 0
             * owner_type : user
             * owner_id : 10161
             * creation_date : 2021-04-21 13:13:02
             * modified_date : 2021-04-21 13:13:02
             * image_url : https://vavci.com/application/modules/Eresume/externals/images/certificate-thumb.png
             * menus : [{"name":"add","label":"Add Certificate"},{"name":"edit","label":"Edit"},{"name":"delete","label":"Delete"}]
             */

            private int certificate_id;
            private int resume_id;
            private String title;
            private String certificateid;
            private int photo_id;
            private String owner_type;
            private int owner_id;
            private String creation_date;
            private String modified_date;
            private String image_url;
            private List<MenusBean> menus;

            public int getCertificate_id() {
                return certificate_id;
            }

            public void setCertificate_id(int certificate_id) {
                this.certificate_id = certificate_id;
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

            public String getCertificateid() {
                return certificateid;
            }

            public void setCertificateid(String certificateid) {
                this.certificateid = certificateid;
            }

            public int getPhoto_id() {
                return photo_id;
            }

            public void setPhoto_id(int photo_id) {
                this.photo_id = photo_id;
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

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
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
                 * label : Add Certificate
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

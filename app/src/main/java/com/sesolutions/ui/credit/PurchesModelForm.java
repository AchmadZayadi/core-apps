package com.sesolutions.ui.credit;

import java.util.List;

public class PurchesModelForm {


    /**
     * result : {"customParams":{"submitURL":"https://vavci.com/sescredit/index/charge?order_id=50"},"loggedin_user_id":10103,"formFields":[{"isRequired":0,"multiple":"","name":"customerName","description":"","label":"Name:","type":"Text","value":"koushal rathor"},{"isRequired":0,"multiple":"","name":"customerPhone","description":"","label":"Phone:","type":"Text","value":""},{"isRequired":0,"multiple":"","name":"customerEmail","description":"","label":"Email:","type":"Text","value":"koushalrathor@aheadsofttech.com"},{"isRequired":0,"multiple":"","name":"orderId","description":"","label":"","type":"Hidden","value":"50"},{"isRequired":0,"multiple":"","name":"returnUrl","description":"","label":"","type":"Hidden","value":"https://vavci.com/sescredit/index/return?order_id=50&state=return"},{"isRequired":0,"multiple":"","name":"notifyUrl","description":"","label":"","type":"Hidden","value":"https://vavci.com/sescredit/ipn?order_id=50&gateway_id=8"},{"isRequired":0,"multiple":"","name":"submit","description":"","label":"Submit","type":"Button","value":""}]}
     * session_id : 22f93ih6l9bhia9pu45tfurq2b
     */
    private ResultEntity result;
    private String session_id;

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public ResultEntity getResult() {
        return result;
    }

    public String getSession_id() {
        return session_id;
    }

    public class ResultEntity {
        /**
         * customParams : {"submitURL":"https://vavci.com/sescredit/index/charge?order_id=50"}
         * loggedin_user_id : 10103
         * formFields : [{"isRequired":0,"multiple":"","name":"customerName","description":"","label":"Name:","type":"Text","value":"koushal rathor"},{"isRequired":0,"multiple":"","name":"customerPhone","description":"","label":"Phone:","type":"Text","value":""},{"isRequired":0,"multiple":"","name":"customerEmail","description":"","label":"Email:","type":"Text","value":"koushalrathor@aheadsofttech.com"},{"isRequired":0,"multiple":"","name":"orderId","description":"","label":"","type":"Hidden","value":"50"},{"isRequired":0,"multiple":"","name":"returnUrl","description":"","label":"","type":"Hidden","value":"https://vavci.com/sescredit/index/return?order_id=50&state=return"},{"isRequired":0,"multiple":"","name":"notifyUrl","description":"","label":"","type":"Hidden","value":"https://vavci.com/sescredit/ipn?order_id=50&gateway_id=8"},{"isRequired":0,"multiple":"","name":"submit","description":"","label":"Submit","type":"Button","value":""}]
         */
        private CustomParamsEntity customParams;
        private int loggedin_user_id;
        private List<FormFieldsEntity> formFields;

        public void setCustomParams(CustomParamsEntity customParams) {
            this.customParams = customParams;
        }

        public void setLoggedin_user_id(int loggedin_user_id) {
            this.loggedin_user_id = loggedin_user_id;
        }

        public void setFormFields(List<FormFieldsEntity> formFields) {
            this.formFields = formFields;
        }

        public CustomParamsEntity getCustomParams() {
            return customParams;
        }

        public int getLoggedin_user_id() {
            return loggedin_user_id;
        }

        public List<FormFieldsEntity> getFormFields() {
            return formFields;
        }

        public class CustomParamsEntity {
            /**
             * submitURL : https://vavci.com/sescredit/index/charge?order_id=50
             */
            private String submitURL;

            public void setSubmitURL(String submitURL) {
                this.submitURL = submitURL;
            }

            public String getSubmitURL() {
                return submitURL;
            }
        }

        public class FormFieldsEntity {
            /**
             * isRequired : 0
             * multiple :
             * name : customerName
             * description :
             * label : Name:
             * type : Text
             * value : koushal rathor
             */
            private int isRequired;
            private String multiple;
            private String name;
            private String description;
            private String label;
            private String type;
            private String value;

            public void setIsRequired(int isRequired) {
                this.isRequired = isRequired;
            }

            public void setMultiple(String multiple) {
                this.multiple = multiple;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public void setType(String type) {
                this.type = type;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public int getIsRequired() {
                return isRequired;
            }

            public String getMultiple() {
                return multiple;
            }

            public String getName() {
                return name;
            }

            public String getDescription() {
                return description;
            }

            public String getLabel() {
                return label;
            }

            public String getType() {
                return type;
            }

            public String getValue() {
                return value;
            }
        }
    }
}

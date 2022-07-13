package com.sesolutions.responses.Courses.Test;

import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.ui.welcome.Dummy;

import java.util.ArrayList;
import java.util.List;

public class TestResponse2 extends ErrorResponse {
    private Result result;

    public Result getResult() {
        return result;
    }

    public class Result extends PaginationHelper {
        private Options button;
        private List<Options> sort;
        private List<Options> options;
        private Test tests;
        private TestContent test;
        private List<Answer> usertest;
        private Test testresult;
        public List<Dummy.Formfields> formFields;

        public List<Options> getOptions() {
            return options;
        }

        public Options getButton() {
            return button;
        }

        public List<Options> getSort() {
            return sort;
        }

        public List<Answer> getUsertest(){
            return usertest;
        }

        public List<Dummy.Formfields> getFormFields(){
            return formFields;
        }

        public Test getTestresult() {
            return testresult;
        }
        public TestContent getTest() {
            return test;
        }

        public Test getPolls() {
            return tests;
        }

        public boolean canCreatePoll() {
            return null != button;
        }
    }
}

package com.sesolutions.responses.qna;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.poll.PollOption;

import java.util.ArrayList;
import java.util.List;

public class QAResponse extends ErrorResponse {
    private Result result;


    public class Result extends PaginationHelper {
        private List<Options> menus;
        private List<Question> questions;
        private List<Question> answers;
        private Question question;
        private Question answer;
        @SerializedName("question_options")
        private List<PollOption> questionOptions;

        private List<CategoryPage<Question>> category;
        private List<CategoryPage<Question>> categories;

        public Question getAnswer() {
            return answer;
        }

        public List<PollOption> getQuestionOptions() {
            return questionOptions;
        }

        public List<Question> getAnswers() {
            return answers;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public List<Question> getQuestions() {
            return questions;
        }

        public boolean hasQuestions() {
            return null != questions;
        }

        public boolean hasCategories() {
            return null != categories;
        }

        public List<QuestionVo> getCategories(String screenType) {
            List<QuestionVo> result = new ArrayList<>();
            if (categories != null) {
                for (CategoryPage<Question> vo : categories) {
                    result.add(new QuestionVo(screenType, vo));
                }
            }
            return result;
        }

        public List<QuestionVo> getQuestionsList(String screenType) {
            List<QuestionVo> result = new ArrayList<>();
            for (Question vo : questions) {
                result.add(new QuestionVo(screenType, vo));
            }
            return result;
        }

        public List<CategoryPage<Question>> getCategory() {
            return category;
        }

        public Question getQuestion() {
            return question;
        }
    }


    public Result getResult() {
        return result;
    }
}

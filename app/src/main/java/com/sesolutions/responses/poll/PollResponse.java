package com.sesolutions.responses.poll;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.responses.feed.Options;

import java.util.ArrayList;
import java.util.List;

public class PollResponse extends ErrorResponse {
    private Result result;

    public Result getResult() {
        return result;
    }

    public class Result extends PaginationHelper {
        private Options button;
        private List<Options> sort;
        private List<Options> options;
        private List<PollOption> gifs;
        private List<Poll> polls;
        private Poll poll;
        @SerializedName("poll_images")
        private Images pollstimage;

        public Images getPollstimage() {
            return pollstimage;
        }

        public List<Options> getOptions() {
            return options;
        }

        public List<PollOption> getGifs() {
            return gifs;
        }

        public Options getButton() {
            return button;
        }

        public List<Options> getSort() {
            return sort;
        }

        public Poll getPoll() {
            return poll;
        }

        public List<Poll> getPolls() {
            return polls;
        }

        //custom methods
        public boolean canCreatePoll() {
            return null != button;
        }

        public List<SearchVo> convertGifToSearchVo() {
            List<SearchVo> list = new ArrayList<>();
            for (PollOption vo : gifs) {
                list.add(new SearchVo(vo));
            }
            return list;
        }
    }
}

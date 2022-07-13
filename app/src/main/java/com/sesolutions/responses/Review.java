package com.sesolutions.responses;

import com.sesolutions.responses.event.Reviews;
import com.sesolutions.utils.Constant;

public class Review extends Reviews {
    private Content page;
    private Content business;
    private Content group;
    private Content owner;
    private Content store;
    private VotingButton voting;

    public Content getOwnerDetail() {
        return owner;
    }

    public Content getContent(String type) {
        switch (type) {
            case Constant.ResourceType.PAGE_REVIEW:
                return page;
            case Constant.ResourceType.GROUP_REVIEW:
                return group;
            case Constant.ResourceType.BUSINESS_REVIEW:
                return business;
            case Constant.ResourceType.STORE_REVIEW:
                return store;
            default:
                return null;

        }
    }

    @Override
    public String getOwnerImage() {
        return owner.getImages();
    }

    public VotingButton getVoting() {
        return voting;
    }


}

package com.sesolutions.responses.member;

import com.sesolutions.responses.ErrorResponse;

public class ProfileMemberResponse extends ErrorResponse {
    private ProfileMember result;

    public ProfileMember getResult() {
        return result;
    }
}

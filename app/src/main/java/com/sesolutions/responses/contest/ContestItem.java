package com.sesolutions.responses.contest;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.CommonVO;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.videos.Tags;

import java.io.Serializable;
import java.util.List;

public class ContestItem extends CommonVO {


    @SerializedName("contest_id")
    private int contestId;

    private String entries;
    private String join;
    private String status;
    @SerializedName("join_count")
    private int joinCount;
    @SerializedName("vote_count")
    private int voteCount;
    private String votes;
    @SerializedName("contest_type")
    private String contestType;
    @SerializedName("contest_status")
    private Options contestStatus;
    @SerializedName("time_left")
    private long timeLeft;
    @SerializedName("contest_image")
    private String contestImage;
    @SerializedName("award_count")
    private int awardCount;
    @SerializedName("cover_image")
    private String coverImage;
    private String award;

    private String award2;
    private String award3;
    private String award4;
    private String award5;
    private String rules;

    private List<Tags> tag;

    @SerializedName("is_vote")
    private String isVote;
    @SerializedName("can_comment")
    private boolean canComment;


    //Entry variables
    @SerializedName("contest_title")
    private String contestTitle;
    private String audio;
    @SerializedName("rich_content")
    private String richContent;
    private String video;
    @SerializedName("video_extension")
    private String videoExtension;
    @SerializedName("participant_id")
    private int participantId;

    @SerializedName("entry_image")
    private String entryImage;
    private int rank;
    @SerializedName("rule_option")
    private Options ruleOption;
    @SerializedName("media_type")
    private Options mediaType;

    private String joinedEndTime;//Jun 30, 2018, 9:01 AM (Asia/Calcutta)",
    private String joinedStartTime;//":"Jun 26, 2018, 9:01 AM",
    private String votingEndTime;//":"Jun 30, 2018, 9:01 AM (Asia/Calcutta)",
    private String votingStartTime;//":"Jun 26, 2018, 9:01 AM"

    public String getCoverImage() {
        return coverImage;
    }

    public String getRichContent() {
        return null != richContent ? richContent.replace("\\r\\n ", "") : null;
    }

    public boolean canComment() {
        return canComment;
    }

    public int getAwardCount() {
        return awardCount;
    }

    public int getRank() {
        return rank;
    }

    public String getVideo() {
        if (null != videoExtension) {
            return videoExtension;
        }
        return video;
    }

    public Options getContestStatus() {
        return contestStatus;
    }

    public Options getMediaType() {
        return mediaType;
    }

    public boolean canShowVote() {
        return null != isVote;
    }

    public boolean isContentVoted() {
        return null != isVote && "true".equals(isVote);
    }

    public void toggleVote() {
        isVote = "" + !isContentVoted();
    }

    public Options getRuleOption() {
        return ruleOption;
    }

    public String getContestImage() {
        return contestImage;
    }

    public String getContestTitle() {
        return contestTitle;
    }

    public int getParticipantId() {
        return participantId;
    }

    public String getEntryImage() {
        return entryImage;
    }

    public String getAward() {
        return award;
    }

    public String getAudio() {
        return audio;
    }

    public String getRules() {
        return rules;
    }

    public List<Tags> getTag() {
        return tag;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getContestId() {
        return contestId;
    }

    public String getEntries() {
        return entries;
    }

    public String getJoin() {
        return join;
    }

    public int getJoinCount() {
        return joinCount;
    }

    public String getStatus() {
        return status;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public String getVotes() {
        return votes;
    }

    public String getJoinedEndTime() {
        return joinedEndTime;
    }

    public String getJoinedStartTime() {
        return joinedStartTime;
    }

    public String getVotingEndTime() {
        return votingEndTime;
    }

    public String getVotingStartTime() {
        return votingStartTime;
    }

    public String getContestType() {
        return contestType;
    }

    public String getAward2() {
        return award2;
    }

    public String getAward3() {
        return award3;
    }

    public String getAward4() {
        return award4;
    }

    public String getAward5() {
        return award5;
    }
}

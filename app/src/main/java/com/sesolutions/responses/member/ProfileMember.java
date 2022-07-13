package com.sesolutions.responses.member;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.feed.Item_user;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.groups.GroupMember;
import com.sesolutions.ui.signup.UserMaster;

import java.util.ArrayList;
import java.util.List;

public class ProfileMember extends PaginationHelper {

    private Options options;
    private List<Options> menus;
    @SerializedName("members")
    private List<GroupMember> members;

    public List<GroupMember> getMembers() {
        return members;
    }

    public List<Options> getMenus() {
        return menus;
    }

    public void setMembers(List<GroupMember> members) {
        this.members = members;
    }

    public Options getOptions() {
        return options;
    }

    public List<Item_user> getItemUsers() {
        List<Item_user> list = new ArrayList<>();
        for (UserMaster vo : members) {
            list.add(new Item_user(vo.getUserId(), vo.getDisplayname(), null));
        }
        return list;
    }
}

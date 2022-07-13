package com.sesolutions.responses;

import com.sesolutions.responses.feed.Options;

import java.util.List;

public class VotingButton {
    private String label;
    private List<Options> buttons;

    public String getLabel() {
        return label;
    }

    public List<Options> getButtons() {
        return buttons;
    }

    public void toggleVotingAction(String name) {
        for (Options opt : buttons) {
            if (name.equals(opt.getName())) {
                opt.toggleValue();
                break;
            }
        }
    }
}

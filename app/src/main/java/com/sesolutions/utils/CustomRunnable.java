package com.sesolutions.utils;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.sesolutions.responses.contest.ContestItem;

public class CustomRunnable implements Runnable {

    public long millisUntilFinished = 40000;
    public TextView holder;
    Handler handler;
    private ContestItem item;
    private boolean canUpdateTime;


    public CustomRunnable(Handler handler, TextView holder) {
        this.handler = handler;
        this.holder = holder;
        // this.millisUntilFinished = millisUntilFinished;
    }

    @Override
    public void run() {
        /* update text view */
        if (canUpdateTime) {
            long seconds = millisUntilFinished;/// 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            //long days = hours / 24;
            String time = hours % 24 + "h " + minutes % 60 + "m " + seconds % 60 + "s";
            holder.setText(time);
            Log.d("DEV123", time);
        }

        //update list item :: decreamenting time
        millisUntilFinished -= 1;
        item.setTimeLeft(millisUntilFinished);


        /* and here comes the "trick" */
        handler.postDelayed(this, 1000);
    }

    public void setItem(ContestItem item) {
        this.item = item;
    }

    public void canUpdateTime(boolean canUpdateTime) {
        this.canUpdateTime = canUpdateTime;
    }
}
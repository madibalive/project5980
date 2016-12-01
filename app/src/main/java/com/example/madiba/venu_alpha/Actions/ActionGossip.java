package com.example.madiba.venu_alpha.Actions;

/**
 * Created by Madiba on 10/31/2016.
 */

public class ActionGossip {

    int chatid;
    Boolean error;

    public ActionGossip(Boolean error, int chatid) {
        this.error = error;
        this.chatid = chatid;
    }
}

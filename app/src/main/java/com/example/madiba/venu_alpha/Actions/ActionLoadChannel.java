package com.example.madiba.venu_alpha.Actions;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Madiba on 10/31/2016.
 */

public class ActionLoadChannel {

    public List<ParseObject> mdata=null;
    public Boolean error = false;

    public ActionLoadChannel(List<ParseObject> mdata, Boolean error) {
        this.mdata = mdata;
        this.error = error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }
}

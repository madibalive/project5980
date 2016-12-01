package com.example.madiba.venu_alpha.Actions;

import com.example.madiba.venu_alpha.models.ModelFeedItem;

import java.util.List;

/**
 * Created by Madiba on 10/31/2016.
 */

public class ActionLoadFeed {

    public List<ModelFeedItem> mdata=null;
    public Boolean error = false;

    public ActionLoadFeed(List<ModelFeedItem> mdata, Boolean error) {
        this.mdata = mdata;
        this.error = error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }
}

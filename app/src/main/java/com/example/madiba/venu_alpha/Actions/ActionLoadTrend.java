package com.example.madiba.venu_alpha.Actions;

import com.example.madiba.venu_alpha.models.TrendingModel;

import java.util.List;

/**
 * Created by Madiba on 10/31/2016.
 */

public class ActionLoadTrend {

    public List<TrendingModel> mdata=null;
    public Boolean error = false;

    public ActionLoadTrend(List<TrendingModel> mdata, Boolean error) {
        this.mdata = mdata;
        this.error = error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }
}

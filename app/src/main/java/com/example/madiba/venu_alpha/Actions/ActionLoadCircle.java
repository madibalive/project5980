package com.example.madiba.venu_alpha.Actions;


import com.example.madiba.venu_alpha.models.ModelCircle;

import java.util.List;

/**
 * Created by Madiba on 10/31/2016.
 */

public class ActionLoadCircle {

    public List<ModelCircle> mdata=null;
    public Boolean error = false;

    public ActionLoadCircle(List<ModelCircle> mdata, Boolean error) {
        this.mdata = mdata;
        this.error = error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }
}

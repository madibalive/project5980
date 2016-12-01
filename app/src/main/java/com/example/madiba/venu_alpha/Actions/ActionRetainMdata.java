package com.example.madiba.venu_alpha.Actions;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Madiba on 10/31/2016.
 */

public class ActionRetainMdata {

    public List<ParseObject> mdata=null;
    public int currentItem;

    public ActionRetainMdata(List<ParseObject> mdata, int currentItem) {
        this.mdata = mdata;
        this.currentItem = currentItem;
    }
}

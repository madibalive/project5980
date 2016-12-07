package com.example.madiba.venu_alpha.adapter.discover;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Madiba on 10/9/2016.
 */
public class SuggestedBasedOnFriendEvens extends BaseQuickAdapter<ParseObject> {

    public SuggestedBasedOnFriendEvens(int layoutResId, List<ParseObject> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, ParseObject parseObject) {

    }
}
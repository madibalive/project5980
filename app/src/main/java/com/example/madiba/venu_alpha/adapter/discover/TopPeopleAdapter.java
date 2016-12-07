package com.example.madiba.venu_alpha.adapter.discover;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Madiba on 10/9/2016.
 */

public class TopPeopleAdapter extends BaseQuickAdapter<ParseUser> {

    public TopPeopleAdapter(int layoutResId, List<ParseUser> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, ParseUser parseObject) {

    }
}
package com.example.madiba.venu_alpha.adapter.search;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.madiba.venu_v3.R;
import com.parse.ParseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Madiba on 10/12/2016.
 */
public class SearchPeopleAdapter
        extends BaseQuickAdapter<ParseUser> {

    public SearchPeopleAdapter(int layoutResId, List<ParseUser> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, final ParseUser user) {

        holder.setText(R.id.name, user.getUsername());

        Glide.with(mContext).load("http://uauage.org/upload/2014/10/avatar-round.png")
                .priority(Priority.LOW)
                .thumbnail(0.1f)
                .error(R.drawable.intruder_shanky)
                .fitCenter()
                .dontAnimate()
                .into((CircleImageView) holder.getView(R.id.avatar));


    }
}
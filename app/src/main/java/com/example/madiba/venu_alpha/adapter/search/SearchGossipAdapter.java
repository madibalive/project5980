package com.example.madiba.venu_alpha.adapter.search;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Madiba on 10/12/2016.
 */
public class SearchGossipAdapter
        extends BaseQuickAdapter<ParseObject> {

    public SearchGossipAdapter(int layoutResId, List<ParseObject> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, final ParseObject event) {

//        holder.setText(R.id.sh_ev_name, "sample name")
//                .setText(R.id.sh_ev_likes, "sample text")
//                .setText(R.id.sh_ev_going, "sample text");
//
//
//        Glide.with(mContext).load("http://uauage.org/upload/2014/10/avatar-round.png").crossFade().fitCenter().into((ImageView) holder.getView(R.id.notif_i_avatar));
//

    }
}
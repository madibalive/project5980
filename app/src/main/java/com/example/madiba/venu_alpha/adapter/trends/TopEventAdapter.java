package com.example.madiba.venu_alpha.adapter.trends;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Madiba on 9/25/2016.
 */


public class TopEventAdapter extends BaseQuickAdapter<ParseObject> {

    public TopEventAdapter(int layoutResId, List<ParseObject> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder holder, ParseObject event) {

//        holder.setText(R.id.tpev_user,event.getParseObject("owner").getString("username"));
//        holder.setText(R.id.tepv_title,event.getString("title"));
//        holder.setText(R.id.tpev_likes,event.getString("title"));
//        holder.setText(R.id.tpev_comment,event.getString("title"));
//
//        Glide.with(mContext).load(event.getParseFile("photo").getUrl()).crossFade().into((ImageView) holder.getView(R.id.tpev_avatar));
//        Glide.with(mContext).load(event.getParseFile("photo").getUrl()).crossFade().into((ImageView) holder.getView(R.id.tpev_image));

    }
}

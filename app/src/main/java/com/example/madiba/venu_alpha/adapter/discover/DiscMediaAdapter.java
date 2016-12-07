package com.example.madiba.venu_alpha.adapter.discover;

import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Madiba on 10/9/2016.
 */

public class DiscMediaAdapter extends BaseQuickAdapter<ParseObject> {

    public DiscMediaAdapter(int layoutResId, List<ParseObject> data) {
        super(layoutResId, data);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    protected void convert(BaseViewHolder holder, ParseObject data) {

//        final View itemView = holder.itemView;
//
//        final SpannableGridLayoutManager.LayoutParams lp =
//                (SpannableGridLayoutManager.LayoutParams) itemView.getLayoutParams();
//
//
//        final int span1 = (data.getInt("pos") == 1 || data.getInt("pos") == 3 ? 2 : 1);
//        final int span2 = (data.getInt("pos") == 1 ? 2 : (data.getInt("pos") == 3 ? 3 : 1));
//
//        final int rowSpan = span1;
//
//        if (lp.rowSpan != rowSpan || lp.colSpan != span2) {
//            lp.rowSpan = rowSpan;
//            lp.colSpan = span2;
//            itemView.setLayoutParams(lp);
//        }
//        ImageView image = holder.getView(R.id.gallery_item_image);
//
//        Glide.with(mContext).load(R.drawable.kayaks)
//                .centerCrop()
//                .into(image);
    }
}
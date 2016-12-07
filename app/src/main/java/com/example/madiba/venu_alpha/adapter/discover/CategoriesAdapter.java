package com.example.madiba.venu_alpha.adapter.discover;

import android.util.Log;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.madiba.venu_alpha.R;
import com.example.madiba.venu_alpha.models.CategoriesModel;
import com.example.madiba.venu_alpha.utils.ColorUtils;

import java.util.List;

/**
 * Created by Madiba on 10/9/2016.
 */

public class CategoriesAdapter extends BaseQuickAdapter<CategoriesModel> {

    public CategoriesAdapter(int layoutResId, List<CategoriesModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, CategoriesModel category) {
        Log.i(TAG, "convert: categories" + category.getmName());
        holder.setText(R.id.ct_title, category.getmName());

        RelativeLayout m = holder.getView(R.id.ct_bgrnd);
        m.setBackground(ColorUtils.colorDrawable(mContext.getResources(),R.drawable.category_back,R.color.venu_red,mContext));


    }
}
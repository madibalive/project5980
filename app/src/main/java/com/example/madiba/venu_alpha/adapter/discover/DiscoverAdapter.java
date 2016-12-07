package com.example.madiba.venu_alpha.adapter.discover;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.madiba.venu_alpha.R;
import com.example.madiba.venu_alpha.models.DiscoverModel;

import java.util.List;

/**
 * Created by Madiba on 10/9/2016.
 */

public class DiscoverAdapter extends BaseQuickAdapter<DiscoverModel> {

    public DiscoverAdapter(int layoutResId, List<DiscoverModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected int getDefItemViewType(int position) {
        if (getItem(position).getType() == DiscoverModel.CATEGORIES)
            return DiscoverModel.CATEGORIES;

        else if (getItem(position).getType() == DiscoverModel.TOP_PEOPLE)
            return DiscoverModel.TOP_PEOPLE;

        else if (getItem(position).getType() == DiscoverModel.New_PEOPLE)
            return DiscoverModel.New_PEOPLE;

        else if (getItem(position).getType() == DiscoverModel.DIS_EXPLORE)
            return DiscoverModel.DIS_EXPLORE;

        else if (getItem(position).getType() == DiscoverModel.NEW_EVENT)
            return DiscoverModel.NEW_EVENT;

      else
            return super.getDefItemViewType(position);


    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateDefViewHolder: test here");
        
        if (viewType == DiscoverModel.CATEGORIES)
            return new CategoriesViewHolder(getItemView(R.layout.container_recycler,parent));
        else if (viewType == DiscoverModel.TOP_PEOPLE)
            return new TopPeopleViewHolder(getItemView(R.layout.container_cardview, parent));
        else if (viewType == DiscoverModel.New_PEOPLE)
            return new TopPeopleViewHolder(getItemView(R.layout.container_cardview, parent));
        else if (viewType == DiscoverModel.DIS_EXPLORE)
            return new DiscoverExploreViewHolder(getItemView(R.layout.container_cardview, parent));
        else if (viewType == DiscoverModel.NEW_EVENT)
            return new NewEventViewHolder(getItemView(R.layout.container_cardview, parent));

        return super.onCreateDefViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        DiscoverModel n = getItem(position);

        if (holder instanceof CategoriesViewHolder) {
            RecyclerView rview = ((CategoriesViewHolder) holder).getView(R.id.container_recyclerview);

            CategoriesAdapter mAdapter = new CategoriesAdapter(R.layout.item_category, n.getListCategories());
            rview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            rview.setAdapter(mAdapter);
            mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {
                Toast.makeText(mContext,"sample",Toast.LENGTH_SHORT).show();

//                    mContext.startActivity(new Intent(mContext, ExpandedCategoryActivity.class));
            });


        }
        else if (holder instanceof TopPeopleViewHolder) {
            TextView rl = ((TopPeopleViewHolder) holder).getView(R.id.card_container_title);
            rl.setText("New Peeps");
            RecyclerView rview = ((TopPeopleViewHolder) holder).getView(R.id.cards_recyclerview);
            GridLayoutManager layMng = new GridLayoutManager(mContext, 4);


            TopPeopleAdapter mAdapter = new TopPeopleAdapter(R.layout.item_discover_user, n.getListUsers());
            rview.setLayoutManager(layMng);
            rview.setAdapter(mAdapter);
            mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {

            });

        }
        else if (holder instanceof MapViewViewHolder) {


        }

        else if (holder instanceof DiscoverExploreViewHolder) {
            Log.e(TAG, "onBindViewHolder: explore data sixe " + n.getListObject().size());
            TextView rl = ((DiscoverExploreViewHolder) holder).getView(R.id.card_container_title);
            rl.setText("New Peeps");
            RecyclerView rview = ((DiscoverExploreViewHolder) holder).getView(R.id.cards_recyclerview);
            GridLayoutManager layMng = new GridLayoutManager(mContext, 3);
            DiscMediaAdapter mAdapter = new DiscMediaAdapter(R.layout.item_gallery, n.getListObject());
            rview.setLayoutManager(layMng);
            rview.setAdapter(mAdapter);
            mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {
            });
        }

        else if (holder instanceof NewEventViewHolder) {
            TextView rl = ((NewEventViewHolder) holder).getView(R.id.card_container_title);
            rl.setText("New Peeps");
            RecyclerView rview = ((NewEventViewHolder) holder).getView(R.id.cards_recyclerview);
            SuggestedBasedOnFriendEvens mAdapter = new SuggestedBasedOnFriendEvens(R.layout.sub_event_box_large, n.getListObject());
            rview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            rview.setAdapter(mAdapter);
            mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {

            });


        }else {
       }

        super.onBindViewHolder(holder, position, payloads);

    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, DiscoverModel discoverModel) {

    }

    private class CategoriesViewHolder extends BaseViewHolder {
        public CategoriesViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class NewEventViewHolder extends BaseViewHolder {
        public NewEventViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class TopPeopleViewHolder extends BaseViewHolder {
        public TopPeopleViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class MapViewViewHolder extends BaseViewHolder {
        public MapViewViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class DiscoverExploreViewHolder extends BaseViewHolder {
        public DiscoverExploreViewHolder(View itemView) {
            super(itemView);
        }
    }


}

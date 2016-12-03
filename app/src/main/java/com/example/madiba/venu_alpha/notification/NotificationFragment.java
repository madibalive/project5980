package com.example.madiba.venu_alpha.notification;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.madiba.venu_alpha.R;
import com.example.madiba.venu_alpha.utils.DividerItemDecoration;
import com.example.madiba.venu_alpha.utils.NetUtils;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class NotificationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private static final String TAG = "NOTIF_FRAGMENT";
    private RecyclerView mRecyclerView;
    private NotifAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View notLoadingView;
    private int delayMillis = 1000;
    private int mCurrentCounter = 0;
    private List<ParseObject> mDatas;

    ParseQuery<ParseObject> notifQuery;
    public NotificationFragment() {
    }

    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatas = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.container_core, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.core_recyclerview);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        initAdapter();
        initialLoad();
    }

    private void initAdapter(){
        mAdapter = new NotifAdapter(R.layout.item_notif, mDatas);
        mAdapter.setOnLoadMoreListener(this);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        mAdapter.setOnRecyclerViewItemClickListener((view1, i) -> {
            ParseObject notif = mAdapter.getItem(i);
            switch (notif.getInt("objectType")) {
                case 0:
                    // event
                    break;
                case 1:
                    //Photo
                    break;
                case 2:
                    //video;
                    break;
                default:
                    //handle defualt
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

    }


    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext()))
            load();
        else
            mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onLoadMoreRequested() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())) {
            load();
            if (notifQuery != null) {
                notifQuery.cancel();
            }
            notifQuery = ParseQuery.getQuery("Activities");
            notifQuery.whereEqualTo("to", ParseUser.getCurrentUser());
            notifQuery.include("from");
            notifQuery.orderByAscending("Created");
            notifQuery.setLimit(20);
            notifQuery.setSkip(mCurrentCounter);
            notifQuery.findInBackground((data, e) -> {
                if (e == null) {

                    if (data.size() <= 0) {
                        mAdapter.notifyDataChangedAfterLoadMore(false);
                        if (notLoadingView == null) {
                            notLoadingView = getActivity().getLayoutInflater().inflate(R.layout.view_no_more_data, (ViewGroup) mRecyclerView.getParent(), false);
                        }
                        mAdapter.addFooterView(notLoadingView);
                    } else {
                        new Handler().postDelayed(() -> {
                            mAdapter.notifyDataChangedAfterLoadMore(data, true);
                            mCurrentCounter = mAdapter.getData().size();

                            ParseObject.unpinAllInBackground("notifications", e1 -> {
                                ParseObject.pinAllInBackground("notifications", mAdapter.getData());
                            });
                        }, delayMillis);
                    }

                } else {
                    Timber.e("Error loading ontap %s",e.getMessage());
                }

            });
        }
    }

    void load() {
        if (notifQuery != null){
            notifQuery.cancel();
        }
        notifQuery = ParseQuery.getQuery("Activities");
        notifQuery.whereEqualTo("to", ParseUser.getCurrentUser());
        notifQuery.include("from");
        notifQuery.orderByAscending("Created");
        notifQuery.setLimit(20);
        notifQuery.findInBackground((objects, e) -> {
            if (e == null) {
                new Handler().postDelayed(() -> {
                    mAdapter.setNewData(objects);
                    mAdapter.openLoadMore(20, true);
                    mAdapter.removeAllFooterView();
                    mSwipeRefreshLayout.setRefreshing(false);
                    mCurrentCounter = mAdapter.getData().size();
                    ParseObject.unpinAllInBackground("notifications", new DeleteCallback() {
                        public void done(ParseException e1) {
                            ParseObject.pinAllInBackground("notifications", objects);
                        }
                    });
                }, 500);
            } else {
                Timber.e("error %s" ,e.getMessage());
            }
        });
    }

    void initialLoad() {
        notifQuery = ParseQuery.getQuery("Events");
        notifQuery.whereEqualTo("to", ParseUser.getCurrentUser());
        notifQuery.include("from");
        notifQuery.orderByAscending("updatedAt");
        notifQuery.fromLocalDatastore();
        notifQuery.findInBackground((data, e) -> {
            if (e == null) {
                new Handler().postDelayed(() -> {
                    mAdapter.setNewData(data);
                }, 500);
            } else {
                Timber.e("error %s" ,e.getMessage());
            }
        });
    }

    private class NotifAdapter
            extends BaseQuickAdapter<ParseObject> {

        NotifAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final ParseObject notif) {

            holder.setText(R.id.notif_i_name, notif.getParseUser("from").getUsername())
                    .setText(R.id.notif_i_content, notif.getString("message"));

//            Glide.with(mContext)
//                    .load("http://uauage.org/upload/2014/10/avatar-round.png")
//                    .crossFade()
//                    .placeholder(R.drawable.ic_default_avatar)
//                    .error(R.drawable.placeholder_error_media)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .centerCrop()
//                    .fallback(R.drawable.ic_default_avatar)
//                    .thumbnail(0.4f)
//                    .into((ImageView) holder.getView(R.id.notif_i_avatar));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            initialLoad();
        }
    }



    @Override
    public void onStop() {
        if (notifQuery != null){
            notifQuery.cancel();
        }
        super.onStop();
    }
}

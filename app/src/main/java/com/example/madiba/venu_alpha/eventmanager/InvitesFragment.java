package com.example.madiba.venu_alpha.eventmanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.madiba.venu_alpha.LaunchActivity;
import com.example.madiba.venu_alpha.R;
import com.example.madiba.venu_alpha.models.GlobalConstants;
import com.example.madiba.venu_alpha.utils.NetUtils;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;

public class InvitesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "USER EVENTS";
    private RecyclerView mRecyclerView;
    private List<ParseObject> mDatas = new ArrayList<>();
    private TextView totalCount;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private UserEventsAdapter mAdapter;
    PopupMenu popupMenu ;
    RxLoaderManager loaderManager;



    public InvitesFragment() {
    }

    public static InvitesFragment newInstance() {
        return new InvitesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.container_core, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.container_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);

        mAdapter = new UserEventsAdapter(R.layout.my_event_layout,mDatas);
        mAdapter.setOnRecyclerViewItemClickListener((view1, i) -> gotoEvent(i));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL ,false));
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onRefresh() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext()))
            reload();
        else
            mSwipeRefreshLayout.setRefreshing(false);
    }

    private void initload(){
        loaderManager.create(
                LoaderEventManager.loadInvites(),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (value.size()>0)
                                mAdapter.setNewData(value);
                        },500);
                    }

                    @Override
                    public void onStarted() {
                        Timber.d("stated");
                        super.onStarted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("stated error %s",e.getMessage());
                        super.onError(e);
                        mSwipeRefreshLayout.setRefreshing(false);

                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("completed");
                        super.onCompleted();
                    }
                }

        ).start();
    }
    private void reload(){
        RxLoader<List<ParseObject>> reload =loaderManager.create(
                LoaderEventManager.loadInvites(),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (value.size()>0)
                                mAdapter.setNewData(value);
                        },500);
                    }

                    @Override
                    public void onStarted() {
                        Timber.d("stated");
                        super.onStarted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("stated error %s",e.getMessage());
                        super.onError(e);
                        mSwipeRefreshLayout.setRefreshing(false);

                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("completed");
                        super.onCompleted();
                    }
                }

        );
    }


    private class UserEventsAdapter
            extends BaseQuickAdapter<ParseObject> {

        public UserEventsAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final ParseObject data) {
            holder.setText(R.id.event_title, data.getString("title"));
            holder.setText(R.id.event_tag,"#"+ data.getString("tag"));

            String day = (String) DateFormat.format("dd", data.getDate("date"));
            String stringMonth = (String) DateFormat.format("MMM", data.getDate("date"));
            String timeOfevent = (String) DateFormat.format("HH:mm", data.getDate("time"));

            holder.setText(R.id.event_time,timeOfevent)
                    .setText(R.id.event_date,day + " \n "+stringMonth);

            Glide.with(mContext).load(data.getParseFile("image").getUrl())
                    .thumbnail(0.1f)
                    .crossFade()
                    .centerCrop()
                    .into((ImageView) holder.getView(R.id.event_image));

        }

    }

    private void gotoEvent(int pos) {
        ParseObject a= mAdapter.getItem(pos);
        Intent event = new Intent(getActivity(), LaunchActivity.class);
        event.putExtra(GlobalConstants.EVP_BANNER, a.getParseFile("image").getUrl());
        event.putExtra(GlobalConstants.EVP_TITLE,a.getString("title"));
        event.putExtra(GlobalConstants.EVP_Hashtag, a.getString("title"));
        event.putExtra(GlobalConstants.EVP_DESC, a.getString("title"));
        event.putExtra(GlobalConstants.EVP_LIKES, a.getInt("likes"));
        event.putExtra(GlobalConstants.EVP_COMMENTS, a.getInt("commets"));
        event.putExtra(GlobalConstants.EVP_SHARES, a.getInt("shares"));
        event.putExtra(GlobalConstants.EVP_DATE, a.getDate("date"));
        event.putExtra(GlobalConstants.EVP_TIME, a.getDate("time"));
        event.putExtra(GlobalConstants.PASS_ID, a.getObjectId());
        event.putExtra(GlobalConstants.PASS_CLASSNAME, a.getClassName());
        event.putExtra("isLike", true);
        startActivity(event);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext()))
            initload();
    }
}

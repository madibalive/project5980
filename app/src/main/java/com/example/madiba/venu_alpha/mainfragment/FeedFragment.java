package com.example.madiba.venu_alpha.mainfragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.madiba.venu_alpha.R;
import com.example.madiba.venu_alpha.adapter.FeedAdapter;
import com.example.madiba.venu_alpha.models.ModelFeedItem;
import com.example.madiba.venu_alpha.models.TrendingModel;
import com.example.madiba.venu_alpha.obervables.GeneralLoaders;
import com.parse.ParseObject;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;

public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,BaseQuickAdapter.RequestLoadMoreListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerview;
    private FeedAdapter mAdapter;
    RxLoaderManager loaderManager;
    private RxLoader<List<ModelFeedItem>> listRxLoader;

    private View notLoadingView;
    private int PAGE_SIZE=10;
    private int mCurrentCounter = 0;
    private int delayMillis = 500;


    public FeedFragment() {
        // Required empty public constructor
    }


    public static FeedFragment newInstance(String param1, String param2) {

        return new FeedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLoader();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.container_core, container, false);
        mRecyclerview = (RecyclerView) view.findViewById(R.id.core_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);

    }

    private void initAdapter(){

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMoreRequested() {

    }

    private void initLoader(){
       listRxLoader= loaderManager.create(
                GeneralLoaders.loadNotifications(mCurrentCounter),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        if (value.size() <= 0) {
                            mAdapter.notifyDataChangedAfterLoadMore(false);
                            if (notLoadingView == null) {
                                notLoadingView = getActivity().getLayoutInflater().inflate(R.layout.view_no_more_data, (ViewGroup) mRecyclerview.getParent(), false);
                            }
                            mAdapter.addFooterView(notLoadingView);

                        } else {
                            new Handler().postDelayed(() -> {
                                mAdapter.removeAllFooterView();
                                if (mSwipeRefreshLayout.isRefreshing()) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                    mAdapter.setNewData(value);
                                    mAdapter.openLoadMore(20, true);
                                    mAdapter.removeAllFooterView();
                                }else {
                                    mAdapter.notifyDataChangedAfterLoadMore(value, true);
                                    mCurrentCounter = mAdapter.getData().size();
                                }
                                mCurrentCounter = mAdapter.getData().size();
                                ParseObject.unpinAllInBackground("notifications", e1 -> {
                                    ParseObject.pinAllInBackground("notifications", value);
                                });
                            }, 500);

                        }
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


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

}

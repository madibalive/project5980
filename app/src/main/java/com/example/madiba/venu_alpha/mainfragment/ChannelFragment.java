package com.example.madiba.venu_alpha.mainfragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.madiba.venu_alpha.R;
import com.example.madiba.venu_alpha.obervables.GeneralLoaders;
import com.example.madiba.venu_alpha.utils.NetUtils;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.lucasr.twowayview.widget.SpannableGridLayoutManager;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;

public class ChannelFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private TwoWayView mRecyclerView;
    private ChannelAdapter mAdapter;
    private List<ParseObject>  mDatas = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RxLoader<List<ParseObject>> listRxLoader;
    private RxLoaderManager loaderManager;


    public ChannelFragment() {
        // Required empty public constructor
    }

    public static ChannelFragment newInstance() {
        return new ChannelFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.container_core, container, false);
        mRecyclerView = (TwoWayView) view.findViewById(R.id.core_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);


    }

    @Override
    public void onRefresh() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            listRxLoader.restart();
        }else
            mSwipeRefreshLayout.setRefreshing(false);
    }

    private void initAdapter(){
        mAdapter = new ChannelAdapter(R.layout.item_channel, mDatas);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLongClickable(true);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void initload(){
        listRxLoader =loaderManager.create(
                GeneralLoaders.loadOnTap(),
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



    private class ChannelAdapter extends BaseQuickAdapter<ParseObject> {

        public ChannelAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
        }

        @Override
        protected void convert(BaseViewHolder holder, ParseObject data) {
            final View itemView = holder.itemView;

            int pos = getData().indexOf(data);
            Log.i(TAG, "convert: pos of current item " +pos);

            final SpannableGridLayoutManager.LayoutParams lp =
                    (SpannableGridLayoutManager.LayoutParams) itemView.getLayoutParams();

            final int span1 = (pos == 1 || pos == 3 ? 2 : 1);
            final int span2 = (pos == 1 ? 2 : (pos == 3 ? 3 : 1));

            final int rowSpan = span1;

            if (lp.rowSpan != rowSpan || lp.colSpan != span2) {
                lp.rowSpan = rowSpan;
                lp.colSpan = span2;
                itemView.setLayoutParams(lp);
            }
//            ImageView image = holder.getView(R.id.channel_item_imageView);

//            Glide.with(mContext).load(data.getParseFile("image").getUrl())
//                    .into(image);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            listRxLoader.start();
        }
    }

}

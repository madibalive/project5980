package com.example.madiba.venu_alpha.mainfragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
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
import com.example.madiba.venu_alpha.obervables.GeneralLoaders;
import com.example.madiba.venu_alpha.ui.StateButton;
import com.example.madiba.venu_alpha.utils.NetUtils;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;


public class ChallangeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private List<ParseObject>   mDatas = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ChallangesAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private StateButton globalBtn,localBtn;
    private ParseObject mMyChallange;
    private View headView;
    private RxLoader<List<ParseObject>> listRxLoader;

    private RxLoaderManager loaderManager;



    public ChallangeFragment() {
    }

    public static ChallangeFragment newInstance() {
        return new ChallangeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initload();
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.container_core, container, false);
        headView = inflater.inflate(R.layout.challange_header, container, false);
        globalBtn = (StateButton) headView.findViewById(R.id.ch_view_Global);
        localBtn = (StateButton) headView.findViewById(R.id.ch_view_Friends);
        localBtn.setRadius(new float[]{60, 60, 0, 0, 0, 0, 60, 60});
        globalBtn.setRadius(new float[]{0, 0, 60, 60, 60, 60, 0, 0});
        mRecyclerView = (RecyclerView) view.findViewById(R.id.core_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);

        initAdapter();




    }


    @Override
    public void onRefresh() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            listRxLoader.restart();
        }else
            mSwipeRefreshLayout.setRefreshing(false);
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


    private void initAdapter(){

        mAdapter = new ChallangesAdapter(R.layout.item_challange, mDatas);
//        challange_viewer.setOnClickListener(view1 -> {
//            DialogFragment challangeViewDailog = new ChallangeInformationDialog();
//            challangeViewDailog.show(getChildFragmentManager(), "private");
//        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
            }
        });
        mAdapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                String content = null;
                ParseObject status = (ParseObject) adapter.getItem(position);
                switch (view.getId()) {
                    case R.id.ch_i_avatar:
                        content = status.getString("tag");
                        break;
                }
            }
        });
        mAdapter.addHeaderView(headView);

        mRecyclerView.setAdapter(mAdapter);
    }

    private class ChallangesAdapter extends BaseQuickAdapter<ParseObject> {
        private int pos;

        public ChallangesAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
            setHasStableIds(true);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
            pos = position;

        }

        @Override
        protected void convert(BaseViewHolder holder, ParseObject challange) {
            holder.setText(R.id.ch_i_name, challange.getParseUser("from").getUsername())
                    .setText(R.id.ch_i_content, "sample text")
                    .setText(R.id.ch_i_pos, "#" + getData().indexOf(challange)+1)
                    .setOnClickListener(R.id.ch_i_avatar, new OnItemChildClickListener());


            Glide.with(mContext).load("http://uauage.org/upload/2014/10/avatar-round.png")
                    .centerCrop()
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into((ImageView) holder.getView(R.id.ch_i_avatar));

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

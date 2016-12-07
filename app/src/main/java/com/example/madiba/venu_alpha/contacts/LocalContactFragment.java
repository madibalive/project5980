package com.example.madiba.venu_alpha.contacts;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.madiba.venu_alpha.R;
import com.example.madiba.venu_alpha.models.ModelLoadLocal;
import com.example.madiba.venu_alpha.obervables.LoaderLocalContact;
import com.example.madiba.venu_alpha.ui.DividerItemDecoration;
import com.example.madiba.venu_alpha.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;


public class LocalContactFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerview;
    private List<ModelLoadLocal> mDatas = new ArrayList<>();
    private MainAdapter mAdapter;

    RxLoaderManager loaderManager;
    private Boolean loadFollowers;
    private String userId;

    public LocalContactFragment() {
    }

    public static LocalContactFragment newInstance() {


        return new LocalContactFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_base, container, false);
        mRecyclerview = (RecyclerView) view.findViewById(R.id.core_recyclerview);
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

    private void initAdapter(){
        mAdapter=new MainAdapter(R.layout.item_ontap,mDatas);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setAdapter(mAdapter);
        mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {
        });


    }

    @Override
    public void onRefresh() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            initload();
        }else
            mSwipeRefreshLayout.setRefreshing(false);
    }

    void initload(){
        loaderManager.create(
                LoaderLocalContact.load(getActivity().getApplicationContext()),
                new RxLoaderObserver<List<ModelLoadLocal>>() {
                    @Override
                    public void onNext(List<ModelLoadLocal> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (value.size()>0)
                                mAdapter.setNewData(value);
                        },500);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                }

        ).start();
    }


    private class MainAdapter
            extends BaseQuickAdapter<ModelLoadLocal> {

        MainAdapter(int layoutResId, List<ModelLoadLocal> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final ModelLoadLocal data) {
//
            if (data.getType()==ModelLoadLocal.TYPE_LOCAL){
                holder.setText(R.id.cc_i_name, data.getPhoneContact().getUsername());

            }else {
                holder.setText(R.id.cc_i_name, data.getParseUser().getUsername());
                Glide.with(mContext).load(data.getParseUser().getParseFile("avatar").getUrl())
                        .thumbnail(0.1f).dontAnimate().into((ImageView) holder.getView(R.id.cc_i_avatar));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            initload();
        }
    }
}

package com.example.madiba.venu_alpha.ontap;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.madiba.venu_alpha.R;
import com.example.madiba.venu_alpha.modal.RequestFragment;
import com.example.madiba.venu_alpha.utils.DividerItemDecoration;
import com.example.madiba.venu_alpha.utils.NetUtils;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class OnTapActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerview;
    private View root;

    private OnTapDirAdapter mAdapter;
    private List<ParseObject> mDatas=new ArrayList<>();
    private ParseQuery<ParseObject> notifQuery;
    public OnTapActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root=inflater.inflate(R.layout.container_core_fab, container, false);
        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        mRecyclerview = (RecyclerView) root.findViewById(R.id.core_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.core_swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        fab.setOnClickListener(view -> openRequest());
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();


    }
    private void initAdapter(){
        mAdapter=new OnTapDirAdapter(R.layout.item_ontap,mDatas);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setAdapter(mAdapter);

        mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {

        });

        mAdapter.setOnRecyclerViewItemLongClickListener((view, i) -> {
            delete(i);
            return false;

        });
    }
    private void openRequest(){
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            DialogFragment requestDialog = new RequestFragment();
            requestDialog.show(getChildFragmentManager(),"request");
        }
    }

    @Override
    public void onRefresh() {
        if (notifQuery != null){
            notifQuery.cancel();
        }
        load();
    }


    private void delete(int pos){
        try {
            mAdapter.getItem(pos).deleteInBackground(e -> {
                if (e == null ){
                    if (root != null)
                        Snackbar.make(root, "Delete Successful", Snackbar.LENGTH_SHORT).show();

                }else{
                    if (root != null)
                        Snackbar.make(root, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            });
            mAdapter.remove(pos);
            mAdapter.notifyItemRemoved(pos);

        }catch (IndexOutOfBoundsException e){
            Timber.e("error deleting object %s",e.getMessage());
        }

    }

    private void load() {
        notifQuery = ParseQuery.getQuery("OnTapRequest");
        notifQuery.whereEqualTo("from",ParseUser.getCurrentUser());
        notifQuery.findInBackground((objects, e) -> {
            if (e == null) {
                new Handler().postDelayed(() -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (objects.size()>0)
                        mAdapter.setNewData(objects);
                },500);
            } else {
                Timber.e("Error loading ontap %s",e.getMessage());
            }
        });
    }

    private class OnTapDirAdapter
            extends BaseQuickAdapter<ParseObject> {

        public OnTapDirAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }
        @Override
        protected void convert(BaseViewHolder holder, final ParseObject request) {
            holder.setText(R.id.ot_i_order_item, request.getString("categoryName"))
                    .setText(R.id.ot_i_number,request.getString("number"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            load();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        if (notifQuery != null){
            notifQuery.cancel();
        }
        super.onStop();
    }




}

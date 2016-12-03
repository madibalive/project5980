package com.example.madiba.venu_alpha.eventmanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.madiba.venu_alpha.R;
import com.example.madiba.venu_alpha.models.GlobalConstants;
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

public class UserEventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "USER EVENTS";
    private RecyclerView mRecyclerView;
    private List<ParseObject> mDatas = new ArrayList<>();
    private TextView totalCount;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private UserEventsAdapter mAdapter;
    private OnFragmentInteractionListener mListener;
    private ParseQuery<ParseObject> notifQuery;
    private ImageButton more;
    PopupMenu popupMenu ;

    public UserEventsFragment() {
    }

    public static UserEventsFragment newInstance() {
        return new UserEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.container_core, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.core_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initAdapter();

    }

    private void initAdapter(){
        mAdapter = new UserEventsAdapter(R.layout.my_event_layout,mDatas);
        mAdapter.setOnRecyclerViewItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.event_type:
                    showPopup(view);
                    break;
            }
        });

        mAdapter.setOnRecyclerViewItemClickListener((view, i) -> gotoEvent(i));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL ,false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showPopup(View view) {
        popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.menu_discover);
        popupMenu.setOnMenuItemClickListener(item -> false);
        popupMenu.show();
    }


    @Override
    public void onRefresh() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext()))
            networkLoad();
        else
            mSwipeRefreshLayout.setRefreshing(false);

    }

    private void EditEvent(ParseObject event) {
    }

    private void networkLoad() {
        notifQuery= ParseQuery.getQuery("EventsVersion3");
        notifQuery.whereEqualTo("from", ParseUser.getCurrentUser());
        notifQuery.orderByAscending("createdAt");
        notifQuery.findInBackground((data, e) -> {
            if (e == null) {
                new Handler().postDelayed(() -> {
                    mAdapter.setNewData(data);
                    mSwipeRefreshLayout.setRefreshing(false);
                    ParseObject.unpinAllInBackground("user_events", e1 -> {
                        ParseObject.pinAllInBackground("user_events", data);
                    });
                }, 500);
            } else {
                Timber.e("Error loading ontap %s",e.getMessage());

            }
        });
    }

    private void initialLoad() {

        notifQuery= ParseQuery.getQuery("EventsVersion3");
        notifQuery.whereEqualTo("from", ParseUser.getCurrentUser());
        notifQuery.orderByAscending("createdAt");
        notifQuery.fromLocalDatastore();
        notifQuery.findInBackground((data, e) -> {
            if (e == null) {
                new Handler().postDelayed(() -> mAdapter.setNewData(data), 500);
            } else {
                Timber.e("Error loading ontap %s",e.getMessage());

            }
        });
    }

    private void gotoEvent(int pos) {
        ParseObject a= mAdapter.getItem(pos);
        Intent event = new Intent(getActivity(), EventPageV2Activity.class);
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


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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

            holder.setVisible(R.id.event_type,true)
                    .setOnClickListener(R.id.event_title, new OnItemChildClickListener());

            Glide.with(mContext).load(data.getParseFile("image").getUrl())
                    .thumbnail(0.1f)
                    .error(R.drawable.kayaks)
                    .crossFade()
                    .centerCrop()
                    .into((ImageView) holder.getView(R.id.event_image));
        }

    }

    @Override
    public void onPause() {
        if (notifQuery !=null)
            notifQuery.cancel();
        super.onPause();

    }

    @Override
    public void onStop() {
        if (notifQuery !=null)
            notifQuery.cancel();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext()))
            initialLoad();

    }
}

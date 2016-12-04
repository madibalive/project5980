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
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class InvitesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "USER EVENTS";
    private RecyclerView mRecyclerView;
    private List<ParseObject> mDatas = new ArrayList<>();
    private TextView totalCount;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserEventsAdapter mAdapter;
    private OnFragmentInteractionListener mListener;
    private ParseQuery<ParseObject> notifQuery;
    private ImageButton more;
    PopupMenu popupMenu ;


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
        View view= inflater.inflate(R.layout.fragment_event_manager_v3, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.invites_rcview);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new UserEventsAdapter(R.layout.event_card_invite,mDatas);

        mAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                gotoEvent(i);

            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL ,false));
        mRecyclerView.setAdapter(mAdapter);

    }


    @Override
    public void onRefresh() {
        networkLoad();

    }



    private void networkLoad() {
        notifQuery= ParseQuery.getQuery("EventsVersion3");
        notifQuery.whereEqualTo("from", ParseUser.getCurrentUser());
        notifQuery.orderByAscending("createdAt");
        notifQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> data, ParseException e) {
                if (e == null) {
                    Log.i(TAG, "done: got initial data" + data.size());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setNewData(data);
                            swipeRefreshLayout.setRefreshing(false);
                            ParseObject.unpinAllInBackground("user_events", new DeleteCallback() {
                                public void done(ParseException e) {
                                    ParseObject.pinAllInBackground("user_events", data);
                                }
                            });
                        }
                    }, 500);
                } else {
                    Log.i(TAG, "done:error " + e.getMessage());

                }
            }
        });
    }

    private void initialLoad() {

        notifQuery= ParseQuery.getQuery("EventsVersion3");
        notifQuery.whereEqualTo("from", ParseUser.getCurrentUser());
        notifQuery.orderByAscending("createdAt");
        notifQuery.fromLocalDatastore();
        notifQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> data, ParseException e) {
                if (e == null) {
                    Log.i(TAG, "done: got initial data" + data.size());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setNewData(data);
                        }
                    }, 500);
                } else {
                    Log.i(TAG, "done:error " + e.getMessage());

                }
            }
        });
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

            Glide.with(mContext).load(data.getParseFile("image").getUrl())
                    .thumbnail(0.1f)
                    .error(R.drawable.kayaks)
                    .crossFade()
                    .centerCrop()
                    .into((ImageView) holder.getView(R.id.event_image));

        }

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
        initialLoad();

    }
}

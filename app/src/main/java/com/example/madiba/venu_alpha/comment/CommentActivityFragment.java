package com.example.madiba.venu_alpha.comment;


import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.liuzhuang.rcimageview.RoundCornerImageView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.example.madiba.venu_alpha.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class CommentActivityFragment extends BottomSheetDialogFragment implements BaseQuickAdapter.RequestLoadMoreListener {

    private static final String TAG = "NOTIF_FRAGMENT";
    private RecyclerView mRecyclerView;
    private CommentAdapter mAdapter;
    private View notLoadingView;
    private int delayMillis = 1000;
    private int mCurrentCounter = 0;
    private boolean hideStatus = false;
    ProgressBar progressBar;

    private EditText mMesssage;
    private ImageButton mSend;
    private List<ParseObject> mDatas = new ArrayList<>();
    private ImageButton close;
    private ParseUser currentUser = ParseUser.getCurrentUser();
    private ParseObject toObject;
    ParseQuery<ParseObject> notifQuery;
    //    final ParseObject comment;
    private String toId, toClass;

    public CommentActivityFragment() {
    }


    public static CommentActivityFragment newInstance(String toId, String toClass, boolean hide) {
        CommentActivityFragment fragment = new CommentActivityFragment();
        Bundle args = new Bundle();
        args.putString("toId", toId);
        args.putString("toClass", toClass);
        args.putBoolean("hide", hide);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            toId = getArguments().getString("toId");
            toClass = getArguments().getString("toClass");
            hideStatus = getArguments().getBoolean("hide");

            toObject = ParseObject.createWithoutData(toClass, toId);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.comment_dailog_recyclerview);
        mSend = (ImageButton) view.findViewById(R.id.sendmsg);
        progressBar = (ProgressBar) view.findViewById(R.id.comment_dailog_progress);
        mMesssage = (EditText) view.findViewById(R.id.comment_dailog_message);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(android.support.design.R.id.design_bottom_sheet);
            assert bottomSheetInternal != null;
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new CommentAdapter(mDatas);


        mSend.setOnClickListener(view1 -> attemptComment());

        mAdapter.setOnLoadMoreListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);


    }


    @Override
    public void onLoadMoreRequested() {
        Log.i(TAG, "onLoadMoreRequested: before loadmore " + mCurrentCounter);
        notifQuery = ParseQuery.getQuery("CommentV2");
        notifQuery.whereEqualTo("to", toObject);
        notifQuery.include("from");
        notifQuery.orderByAscending("updatedAt");
        notifQuery.setLimit(20);
        notifQuery.setSkip(mCurrentCounter);
        notifQuery.findInBackground((data, e) -> {
            if (e == null) {

                if (data.size() <= 0) {
                    Log.i(TAG, "done: onloadmore data is zero ");
                    mAdapter.notifyDataChangedAfterLoadMore(false);
                    if (notLoadingView == null) {
                        notLoadingView = getActivity().getLayoutInflater().inflate(R.layout.view_empty, (ViewGroup) mRecyclerView.getParent(), false);
                    }
                    mAdapter.addFooterView(notLoadingView);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            mAdapter.notifyDataChangedAfterLoadMore(data, true);
                            mCurrentCounter = mAdapter.getData().size();


                            Log.i(TAG, "run: current count" + mCurrentCounter);
                            ParseObject.unpinAllInBackground("notifications", new DeleteCallback() {
                                public void done(ParseException e) {
                                    ParseObject.pinAllInBackground("notifications", mAdapter.getData());
                                }
                            });

                        }
                    }, delayMillis);
                }

            } else {
                Log.i(TAG, "done:error " + e.getMessage());
            }
        });
    }


    private void attemptComment() {
        mMesssage.setError(null);
        Boolean send = true;
        String message = mMesssage.getText().toString();

        if (TextUtils.isEmpty(message)) {
            send = false;
            mMesssage.setError("Message is empty");
        }

        if (toObject == null)
            send = false;

        if (send)
            sendComment(message);
    }


    private void sendComment(final String message) {
        mSend.setEnabled(false);

        final ParseObject comment = new ParseObject("CommentV2");
        comment.put("from", ParseUser.getCurrentUser());
        comment.put("fromId", ParseUser.getCurrentUser().getObjectId());
        comment.put("message", message);
        comment.put("to", toObject);
        comment.put("toId", toObject.getObjectId());
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mSend.setEnabled(true);
                if (e == null) {
                    mMesssage.setText("");
                    mAdapter.add(mAdapter.getData().size(), comment);
                    mAdapter.notifyItemInserted(mAdapter.getData().size());

                } else
                    Log.i(TAG, "done: err" + e.getMessage());
            }
        });
    }

    void loadComments() {
        ParseQuery<ParseObject> notifQuery = ParseQuery.getQuery("CommentV2");
        notifQuery.whereEqualTo("to", toObject);
        notifQuery.include("from");
        notifQuery.orderByAscending("createdAt");
        notifQuery.setLimit(20);
        notifQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.i(TAG, "done: got initial data" + objects.size());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setNewData(objects);
                            mAdapter.openLoadMore(20, true);
                            mAdapter.removeAllFooterView();
                            mCurrentCounter = mAdapter.getData().size();


                        }
                    }, 500);
                } else {
                    Log.i(TAG, "done:error " + e.getMessage());
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private class CommentAdapter
            extends BaseQuickAdapter<ParseObject> {
        private static final int TEXT_IN = 1;
        private static final int TEXT_OUT = 2;

        public void addItem(ParseObject item) {
            if (item != null) {
                mData.add(item);
                notifyDataSetChanged();
            }
        }

        public void removeItem(ParseObject item) {
            if (item != null) {
                int position = mData.indexOf(item);
                mData.remove(item);
                notifyItemRemoved(position);
            }
        }

        public CommentAdapter(List<ParseObject> data) {
            super(R.layout.comment_item_out, data);
        }

        @Override
        protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TEXT_IN)
                return new MessageInViewHolder(getItemView(R.layout.comment_item_in, parent));
            else if (viewType == TEXT_OUT)
                return new MessageInViewHolder(getItemView(R.layout.comment_item_out, parent));
            return super.onCreateDefViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {

            super.onBindViewHolder(holder, position, payloads);

        }

        @Override
        protected void convert(BaseViewHolder holder, final ParseObject comment) {

            holder.setText(R.id.comment_msg, comment.getString("message"));
            if (holder instanceof MessageInViewHolder) {
                Glide.with(mContext).load(comment.getParseUser("from").getString("avatarUrl")).crossFade().fitCenter().into((RoundCornerImageView) holder.getView(R.id.comment_avatar));
            }


        }

        public class MessageInViewHolder extends BaseViewHolder {
            public MessageInViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class MessageOutViewHolder extends BaseViewHolder {
            public MessageOutViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    @Override
    public void onPause() {
        if (notifQuery != null)
            notifQuery.cancel();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadComments();
    }

    @Override
    public void onStop() {

        if (hideStatus) {
            if (Build.VERSION.SDK_INT > 10) {
                int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;

                getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
            } else {
                getActivity().getWindow()
                        .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        if (notifQuery != null)
            notifQuery.cancel();

        super.onStop();
    }
}

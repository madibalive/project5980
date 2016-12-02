package com.example.madiba.venu_alpha.gossip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.madiba.venu_alpha.R;

public class CreateGossipSheet extends BottomSheetDialogFragment {
    TextInputEditText mTitle;
    TextInputEditText mDesc;
    Button mPost;

    public CreateGossipSheet() {
    }

    static CreateGossipSheet newInstance(String string) {
        return new CreateGossipSheet();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_gossip, container, false);
        mTitle = (TextInputEditText) v.findViewById(R.id.post_gossip_title);
        mPost = (Button) v.findViewById(R.id.post_gossip_post);
        mPost.setOnClickListener(view -> attemptLogin());
        return v;
    }

    private void attemptLogin() {
        mTitle.setError(null);
        String title = mTitle.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(title)) {
            mTitle.setError("title cannot be empty");
            focusView = mTitle;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
//            EventBus.getDefault().post(new ActionGossip(title));
            dismiss();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}

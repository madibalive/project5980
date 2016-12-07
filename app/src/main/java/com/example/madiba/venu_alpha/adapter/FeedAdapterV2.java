package com.example.madiba.venu_alpha.adapter;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.madiba.venu_v3.R;
import com.example.madiba.venu_v3.galleryv2.PhotoHolderActivity;
import com.example.madiba.venu_v3.servicesv2.GeneralService;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import de.hdodenhof.circleimageview.CircleImageView;


public class FeedAdapterV2 extends BaseQuickAdapter<ParseObject> {
    private static final int MEDIA_TYPE = 1;
    private static final int EVENT_TYPE = 2;
    private static final int GOSSIP_TYPE = 3;

    FragmentManager ft;

    private ParseObject mEvent;
    private ParseObject mMedia;
    private ParseObject mGossip;
    private ParseObject mShare;

    public FeedAdapterV2(int layoutResId, List<ParseObject> data, FragmentManager ft) {
        super(layoutResId, data);
        mEvent = new ParseObject("Events");
        mMedia = new ParseObject("Media");
        mGossip = new ParseObject("Gossip");
        mShare = new ParseObject("Share");
        this.ft = ft;

    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, ParseObject feedItem) {

    }

    @Override
    protected int getDefItemViewType(int position) {
        Log.i(TAG, "getDefItemViewType: " + getItem(position).getClassName());

        if (getItem(position).getClassName().equals(mMedia.getClassName()))
            return MEDIA_TYPE;

        else if (getItem(position).getClassName().equals(mEvent.getClassName()))
            return EVENT_TYPE;

        else if (getItem(position).getClassName().equals(mGossip.getClassName()))
            return GOSSIP_TYPE;

        else if (getItem(position).getClassName().equals(mShare.getClassName())){
            if (getItem(position).getInt("type")==0)
                return MEDIA_TYPE;
            else
                return EVENT_TYPE;
        }

        return super.getDefItemViewType(position);
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MEDIA_TYPE)
            return new MediaViewHolder(getItemView(R.layout.media_v2, parent));
        if (viewType == EVENT_TYPE)
            return new EventViewHolder(getItemView(R.layout.event, parent));
        if (viewType == GOSSIP_TYPE)
            return new GossipViewHolder(getItemView(R.layout.gossip, parent));

        return super.onCreateDefViewHolder(parent, viewType);
    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int positions) {
        ParseObject n = getItem(positions);
        ParseUser user;
        ParseObject obj;

        Log.i(TAG, "onBindViewHolder: started creating rows");

        if (holder instanceof MediaViewHolder) {

            if (n.getClassName().equals(mMedia.getClassName()))
                 obj= n;
            else if(n.getClassName().equals(mShare.getClassName())){
                obj = n.getParseObject("object");
            }else {
                obj=n;
            }
            user=n.getParseUser("from");

            ((MediaViewHolder) holder).isVideo(n);
//            try {
//                ((MediaViewHolder) holder).mUsername.setText(user.getUsername());
//                ((MediaViewHolder) holder).mHashtag.setText(String.valueOf(obj.getInt("likes")));
//                ((MediaViewHolder) holder).like.setText(String.valueOf(obj.getInt("comments")));
//                ((MediaViewHolder) holder).like.setText(String.valueOf(obj.getInt("comments")));
//                if (obj.getInt("isPrivate") != 1)
//                    ((MediaViewHolder) holder).comment.setText(String.valueOf(obj.getInt("shares")));
//                else {
//                    ((MediaViewHolder) holder).comment.setVisibility(View.GONE);
//
//                }
//
//                Glide.with(mContext).load("")
//                        .placeholder(12321)
//                        .priority(Priority.LOW)
//                        .crossFade()
//                        .into(((MediaViewHolder) holder).mAvatar);
//
//                Glide.with(mContext).load("")
//                        .placeholder(12321)
//                        .priority(Priority.HIGH)
//                        .crossFade()
//                        .into(((MediaViewHolder) holder).image);
//
//            } catch (NullPointerException e){
//                Log.i(TAG, "onBindViewHolder: ");
//            }

            ((MediaViewHolder) holder).likeCheck(obj.getObjectId());
            ((MediaViewHolder) holder).updateLike(obj);
            ((MediaViewHolder) holder).setupImage();
            ((MediaViewHolder) holder).setUpComment(obj.getObjectId());

//            if (n.getInt("isPrivate")!=1)
                ((MediaViewHolder) holder).setUpShare(obj);


        }


        if (holder instanceof EventViewHolder) {

            if (n.getClassName().equals(mMedia.getClassName()))
                obj= getItem(positions);
            else if(n.getClassName().equals(mShare.getClassName())){
                obj = n.getParseObject("object");
            }else {
                obj= n;
            }
            user=n.getParseUser("from");

//            try {
//                ((EventViewHolder) holder).mUsername.setText(obj.getString("tag"));
//                ((EventViewHolder) holder).mHashtag.setText(String.valueOf(obj.getInt("likes")));
//                ((EventViewHolder) holder).like.setText(String.valueOf(obj.getInt("comments")));
//                ((EventViewHolder) holder).like.setText(String.valueOf(obj.getInt("comments")));
//                if (obj.getInt("isPrivate") != 1)
//                    ((EventViewHolder) holder).comment.setText(String.valueOf(obj.getInt("shares")));
//                else {
//                    ((EventViewHolder) holder).comment.setVisibility(View.GONE);
//
//                }
//
//
//                Glide.with(mContext).load("")
//                        .placeholder(12321)
//                        .priority(Priority.LOW)
//                        .crossFade()
//                        .into(((EventViewHolder) holder).mAvatar);
//
//                Glide.with(mContext).load("")
//                        .placeholder(12321)
//                        .priority(Priority.HIGH)
//                        .crossFade()
//                        .into(((EventViewHolder) holder).image);
//
//            } catch (NullPointerException e){
//                Log.i(TAG, "onBindViewHolder: ");
//            }

            ((EventViewHolder) holder).likeCheck();
            ((EventViewHolder) holder).updateLike(obj);
            ((EventViewHolder) holder).setupImage();
            ((EventViewHolder) holder).setUpComment();
            if (n.getInt("isPrivate")!=1)
                ((EventViewHolder) holder).setUpShare(obj);

        }

        if (holder instanceof GossipViewHolder) {
//            try {
//                ((GossipViewHolder) holder).mUsername.setText(n.getString("tag"));
//                ((GossipViewHolder) holder).mTitle.setText(String.valueOf(n.getInt("likes")));
//                ((GossipViewHolder) holder).like.setText(String.valueOf(n.getInt("comments")));
//                ((GossipViewHolder) holder).like.setText(String.valueOf(n.getInt("shares")));
//
//                Glide.with(mContext).load("")
//                        .placeholder(12321)
//                        .priority(Priority.LOW)
//                        .crossFade()
//                        .into(((GossipViewHolder) holder).mAvatar);
//
//            } catch (NullPointerException e){
//                Log.i(TAG, "onBindViewHolder: ");
//            }

//            final String chatid = n.getString("chatId");
//
//            ((GossipViewHolder) holder).likeCheck();
//            ((GossipViewHolder) holder).updateLike(n);
//            ((GossipViewHolder) holder).mTitle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ((GossipViewHolder) holder).gotoGossip(chatid);
//                }
//            });
        }

        super.onBindViewHolder(holder, positions);
    }

     private class MediaViewHolder extends BaseViewHolder {

        private TextView mUsername,mHashtag,mTimeAgo;
        private CircleImageView mAvatar;
        private TextView like,share,comment;
        private LikeButton likeButton,shareButton,commentButton;
        private ImageView image;
        private ImageButton playIcon;
         private Boolean isVideo=false;

         MediaViewHolder(View v) {
            super(v);
            like = (TextView) v.findViewById(R.id.f4_like);
//            share = (TextView) v.findViewById(R.id.f4_share);
            comment = (TextView) v.findViewById(R.id.f4_comment);
            mUsername = (TextView) v.findViewById(R.id.f4_name);
            mHashtag = (TextView) v.findViewById(R.id.f4_hashtag);
            mTimeAgo = (TextView) v.findViewById(R.id.f4_time_ago);

            mAvatar = (CircleImageView) v.findViewById(R.id.f4_avatar);
//            image = (ImageView) v.findViewById(R.id.main_media_image);
//            playIcon = (ImageButton) v.findViewById(R.id.main_media_isVideo);

            likeButton = (LikeButton) v.findViewById(R.id.f4_like_ic);
             shareButton = (LikeButton) v.findViewById(R.id.f4_share_ic);
           commentButton = (LikeButton) v.findViewById(R.id.f4_comment_ic);
       }

        void setupImage(){
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isVideo){
                        mContext.startActivity(new Intent(mContext, PhotoHolderActivity.class));

                    }else {
                        mContext.startActivity(new Intent(mContext, PhotoHolderActivity.class));

                    }
                }
            });
        }
        void updateLike(final ParseObject n){
            likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    like.setText("like");
                    GeneralService.startActionLike(mContext,true,n.getObjectId(),n.getClassName());
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    like.setText("unlike");
                    GeneralService.startActionLike(mContext,false,n.getObjectId(),n.getClassName());
                }
            });
        }
        void isVideo(final ParseObject n){
                if (n.getBoolean("type")){
                    isVideo = true;
                    playIcon.setVisibility(View.VISIBLE);
                }
            }

         void likeCheck(String id) {
             Log.i(TAG, "likeCheck: " + id);
             ParseRelation<ParseObject> a= ParseUser.getCurrentUser().getRelation("likes");

             a.getQuery().whereEqualTo("objectId",id).fromPin("Likes").getFirstInBackground(
                     new GetCallback<ParseObject>() {
                         @Override
                         public void done(ParseObject object, ParseException e) {
                             if (e == null){
                                 if (object !=null){
                                     likeButton.setLiked(true);
                                 }
                             }else{
                             }
                         }
                     }
             );

         }

         void setUpComment(String id){
//             commentButton.setOnLikeListener(new OnLikeListener() {
//                 @Override
//                 public void liked(LikeButton likeButton) {
//                     DialogFragment newFragment = CommentActivityFragment.newInstance();
//                     newFragment.show(ft, "comment");
//                 }
//
//                 @Override
//                 public void unLiked(LikeButton likeButton) {
//                     DialogFragment newFragment = CommentActivityFragment.newInstance();
//                     newFragment.show(ft, "comment");
//                 }
//             });
         }

         void setUpShare(final ParseObject n) {

             final String id = n.getObjectId();
             final String classname = n.getClassName();

             shareButton.setOnLikeListener(new OnLikeListener() {
                 @Override
                 public void liked(LikeButton likeButton) {
                     Log.i(TAG, "liked: " + classname + id);
//                    GeneralService .startActionShare(mContext,true,id,classname);
                 }

                 @Override
                 public void unLiked(LikeButton likeButton) {

                 }
             });
         }
     }

     private class GossipViewHolder extends BaseViewHolder {

         private TextView mUsername,mTitle,mTimeAgo,mTimeLeft;
         private CircleImageView mAvatar;
         private TextView like, share;
         private LikeButton likeButton,shareButton;

         GossipViewHolder(View v) {
            super(v);
//             like = (TextView) v.findViewById(R.id.f5_like);
//             share = (TextView) v.findViewById(R.id.f5_share);
//
//             mUsername = (TextView) v.findViewById(R.id.f4_name);
//             mTitle = (TextView) v.findViewById(R.id.f4_hashtag);
//             mTimeAgo = (TextView) v.findViewById(R.id.f4_time_ago);
//
//             mAvatar = (CircleImageView) v.findViewById(R.id.f4_avatar);
//
//             likeButton = (LikeButton) v.findViewById(R.id.f5_like_ic);
//             shareButton = (LikeButton) v.findViewById(R.id.f5_share_ic);
        }

          void updateLike(final ParseObject n){
             likeButton.setOnLikeListener(new OnLikeListener() {
                 @Override
                 public void liked(LikeButton likeButton) {
                     like.setText("like");
                     GeneralService.startActionLike(mContext,true,n.getObjectId(),n.getClassName());
                 }

                 @Override
                 public void unLiked(LikeButton likeButton) {
                     like.setText("unlike");
                     GeneralService.startActionLike(mContext,false,n.getObjectId(),n.getClassName());
                 }
             });
         }

         void likeCheck() {
             Task.callInBackground(new Callable<Boolean>() {
                 @Override
                 public Boolean call() throws Exception {
                     for (int i = 0; i < 8; i++) {
                         Log.i(TAG, "check: 1");
                     }
                     return true;
                 }
             }).onSuccess(new Continuation<Boolean, Void>() {
                 @Override
                 public Void then(Task<Boolean> task) throws Exception {
                     likeButton.setLiked(true);
                     return null;
                 }
             },Task.UI_THREAD_EXECUTOR);
         }

         void gotoGossip(String id){
             Intent intent = new Intent(mContext, ConversationActivity.class);
             intent.putExtra(ConversationUIService.GROUP_ID, id);
             mContext.startActivity(intent);
         }

         void setUpShare(final ParseObject n) {
             shareButton.setOnLikeListener(new OnLikeListener() {
                 @Override
                 public void liked(LikeButton likeButton) {
                     Task.callInBackground(new Callable<Void>() {
                         @Override
                         public Void call() throws Exception {

                             ParseObject shareObject= new ParseObject("Share");
                             shareObject.put("from",ParseUser.getCurrentUser());
                             shareObject.put("type",0);
                             shareObject.put("object",n);
                             shareObject.put("fromID",ParseUser.getCurrentUser().getObjectId());

                             shareObject.save();
                             return null;
                         }
                     }).onSuccess(new Continuation<Void, Void>() {
                         @Override
                         public Void then(Task<Void> task) throws Exception {
                             return null;
                         }
                     });
                 }

                 @Override
                 public void unLiked(LikeButton likeButton) {

                 }
             });
         }
    }

     private class EventViewHolder extends BaseViewHolder {
         private TextView mUsername,mHashtag,mTimeAgo,mTitle,mLocation,mDate,mTime;
         private CircleImageView mAvatar;
         private TextView like, share,comment;
         private LikeButton likeButton,shareButton,commentButton;
         private ImageView image;
         EventViewHolder(View v) {
            super(v);
             like = (TextView) v.findViewById(R.id.f5_like);
             share = (TextView) v.findViewById(R.id.f5_share);
             comment = (TextView) v.findViewById(R.id.f5_comment);

             mUsername = (TextView) v.findViewById(R.id.f4_name);
             mHashtag = (TextView) v.findViewById(R.id.f4_hashtag);
             mTimeAgo = (TextView) v.findViewById(R.id.f4_time_ago);

             mAvatar = (CircleImageView) v.findViewById(R.id.f4_avatar);
             image = (ImageView) v.findViewById(R.id.main_event_image);

             likeButton = (LikeButton) v.findViewById(R.id.f5_like_ic);
             shareButton = (LikeButton) v.findViewById(R.id.f5_share_ic);
             commentButton = (LikeButton) v.findViewById(R.id.f5_comment_ic);

        }
         void setupImage(){
             image.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     mContext.startActivity(new Intent(mContext, PhotoHolderActivity.class));
                 }
             });
         }
         void updateLike(final ParseObject n){
             likeButton.setOnLikeListener(new OnLikeListener() {
                 @Override
                 public void liked(LikeButton likeButton) {
                     like.setText("like");
                     GeneralService.startActionLike(mContext,true,n.getObjectId(),n.getClassName());
                 }

                 @Override
                 public void unLiked(LikeButton likeButton) {
                     like.setText("unlike");
                     GeneralService.startActionLike(mContext,false,n.getObjectId(),n.getClassName());
                 }
             });
         }

         void likeCheck() {
             Task.callInBackground(new Callable<Boolean>() {
                 @Override
                 public Boolean call() throws Exception {
                     for (int i = 0; i < 8; i++) {
                         Log.i(TAG, "check: 1");
                     }
                     return true;
                 }
             }).onSuccess(new Continuation<Boolean, Void>() {
                 @Override
                 public Void then(Task<Boolean> task) throws Exception {
                     likeButton.setLiked(true);
                     return null;
                 }
             },Task.UI_THREAD_EXECUTOR);
         }

         void setUpComment(){
             commentButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
//                     DialogFragment newFragment = CommentActivityFragment.newInstance();
//                     newFragment.show(ft, "comment");
                 }
             });
         }

         void setUpShare(final ParseObject n) {
             shareButton.setOnLikeListener(new OnLikeListener() {
                 @Override
                 public void liked(LikeButton likeButton) {
                     Task.callInBackground(new Callable<Void>() {
                         @Override
                         public Void call() throws Exception {

                             ParseObject shareObject= new ParseObject("Share");
                             shareObject.put("from",ParseUser.getCurrentUser());
                             shareObject.put("type",0);
                             shareObject.put("object",n);
                             shareObject.put("fromID",ParseUser.getCurrentUser().getObjectId());

                             shareObject.save();
                             return null;
                         }
                     }).onSuccess(new Continuation<Void, Void>() {
                         @Override
                         public Void then(Task<Void> task) throws Exception {
                             return null;
                         }
                     });
                 }

                 @Override
                 public void unLiked(LikeButton likeButton) {

                 }
             });
         }
    }
}

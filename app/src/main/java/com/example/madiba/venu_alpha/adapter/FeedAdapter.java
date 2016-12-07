package com.example.madiba.venu_alpha.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.madiba.venu_v3.R;
import com.example.madiba.venu_v3.mainfragments.MainFeedragmentV2;
import com.example.madiba.venu_v3.model.EventLiked;
import com.example.madiba.venu_v3.model.EventPost;
import com.example.madiba.venu_v3.model.EventShare;
import com.example.madiba.venu_v3.model.EventUnliked;
import com.facebook.drawee.view.SimpleDraweeView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.example.madiba.venu_v3.model.FeedModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Madiba on 9/12/2016.
 */

public class FeedAdapter extends BaseQuickAdapter<FeedModel> {

    public FeedAdapter(int layoutResId, List<FeedModel> data) {
        super(layoutResId, data);

    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, FeedModel feedItem) {

    }

    @Override
    protected int getDefItemViewType(int position) {
        if (getItem(position).getMtype() == FeedModel.MEDIA_IMAGE)
            return FeedModel.MEDIA_IMAGE;

        else if (getItem(position).getMtype() == FeedModel.MEDIA_VIDEO)
            return FeedModel.MEDIA_VIDEO;

        else if (getItem(position).getMtype() == FeedModel.EVENT)
            return FeedModel.EVENT;

        else if (getItem(position).getMtype() == FeedModel.GOSSIP)
            return FeedModel.GOSSIP;

        return super.getDefItemViewType(position);
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FeedModel.MEDIA_IMAGE)
            return new MediaViewHolder(getItemView(R.layout.media_v2, parent));

        if (viewType == FeedModel.MEDIA_VIDEO)
            return new MediaViewHolder(getItemView(R.layout.media_v2, parent));
        if (viewType == FeedModel.EVENT)
            return new EventViewHolder(getItemView(R.layout.eventv2, parent));
        if (viewType == FeedModel.GOSSIP)
            return new GossipViewHolder(getItemView(R.layout.gossip, parent));

        return super.onCreateDefViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int positions) {

        Log.i(TAG, "onBindViewHolder: started creating rows");

        if (holder instanceof MediaViewHolder) {

            //initial item
            final FeedModel data = getItem(positions);

            // set text varribles
            ((MediaViewHolder) holder).setText(R.id.f_hashtag,"#"+data.getMhashtag());
            ((MediaViewHolder) holder).setText(R.id.f_like,String.valueOf(data.getMlikes()));
            ((MediaViewHolder) holder).setText(R.id.f_comment,String.valueOf(data.getMcomment()));
            ((MediaViewHolder) holder).setText(R.id.f_share,String.valueOf(data.getMshare()));
            ((MediaViewHolder) holder).setText(R.id.f_time_ago,data.getMdate());
            ((MediaViewHolder) holder).setText(R.id.f_name,data.getMname());

//            //sett images
//            Glide.with(mContext).load(data.getMurl())
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .priority(Priority.HIGH)
//                    .crossFade().into((ImageView) ((MediaViewHolder) holder).getView(R.id.f10_image));

            Uri uri1 = Uri.parse(data.getMurl());
            SimpleDraweeView draweeView1 =((MediaViewHolder) holder).getView(R.id.f10_image);
            draweeView1.setImageURI(uri1);

            if (data.getMavatar()!=null){
                Uri uri = Uri.parse(data.getMavatar());
                SimpleDraweeView draweeView =((MediaViewHolder) holder).getView(R.id.f_avatar);
                draweeView.setImageURI(uri);
            }

//            Glide.with(mContext).load(data.getMavatar())
//                    .priority(Priority.LOW)
//                    .diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate().into((ImageView)  ((MediaViewHolder) holder).getView(R.id.f_avatar));

            if (data.getMtype() == FeedModel.MEDIA_VIDEO){
//                // enable button
//                SimpleDraweeView v=((MediaViewHolder) holder).getView(R.id.f10_image);
//                v.setColorFilter(ContextCompat.getColor(mContext,R.color.background_dark), android.graphics.PorterDuff.Mode.MULTIPLY);
//                ((MediaViewHolder) holder).setVisible(R.id.f10_isVideo,true);
//                ((MediaViewHolder) holder).setOnClickListener(R.id.f10_isVideo, new OnItemChildClickListener());

            }else {
                ((MediaViewHolder) holder).setOnClickListener(R.id.f10_image, new OnItemChildClickListener());

            }
            //setonclick listeners
            LikeButton b = ((MediaViewHolder) holder).getView(R.id.f_like_ic);
            if (data.getMislikeBoolean())
                b.setLiked(true);
            else
                b.setLiked(false);
            b.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    EventBus.getDefault().post(new EventLiked(positions));
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    EventBus.getDefault().post(new EventUnliked(positions));
                }
            });
            LikeButton s = ((MediaViewHolder) holder).getView(R.id.f_share_ic);

            s.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    EventBus.getDefault().post(new EventShare(positions));
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    EventBus.getDefault().post(new EventShare(positions));
                }
            });

            ((MediaViewHolder) holder).setOnClickListener(R.id.f_avatar, new OnItemChildClickListener());
            ((MediaViewHolder) holder).setOnClickListener(R.id.f_hashtag, new OnItemChildClickListener());
            ((MediaViewHolder) holder).setOnClickListener(R.id.f_comment_ic, new OnItemChildClickListener());

        }

        if (holder instanceof EventViewHolder) {
            final FeedModel data = getItem(positions);

            ((EventViewHolder) holder).setText(R.id.f_hashtag,"#"+data.getMhashtag());
//            ((EventViewHolder) holder).setText(R.id.f_like,String.valueOf(data.getMlikes()));
//            ((EventViewHolder) holder).setText(R.id.f_comment,String.valueOf(data.getMcomment()));
//            ((EventViewHolder) holder).setText(R.id.f_share,String.valueOf(data.getMshare()));
            ((EventViewHolder) holder).setText(R.id.f_time_ago,data.getMdate());
            ((EventViewHolder) holder).setText(R.id.f_name,data.getMname());

            ((EventViewHolder) holder).setText(R.id.main_event_title,String.valueOf(data.getMeventtitle()));
            ((EventViewHolder) holder).setText(R.id.main_event_time,String.valueOf(data.getMeventtime()));
//            ((EventViewHolder) holder).setText(R.id.main_event_location,String.valueOf(data.getMeventlocation()));
//            ((EventViewHolder) holder).setText(R.id.main_event_category,data.getMeventdate());
            ((EventViewHolder) holder).setText(R.id.main_event_date,data.getMeventdate());

            if (data.getShared()){
                ((EventViewHolder) holder).setVisible(R.id.f_share,true);

            }

            Glide.with(mContext).load(data.getMurl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .crossFade().into((ImageView) ((EventViewHolder) holder).getView(R.id.main_event_image));
            Glide.with(mContext).load(data.getMavatar())
                    .priority(Priority.LOW)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate().into((ImageView)  ((EventViewHolder) holder).getView(R.id.f_avatar));


//            ((EventViewHolder) holder).setOnClickListener(R.id.f10_image, new OnItemChildClickListener());
//
            ((EventViewHolder) holder).setOnClickListener(R.id.main_event_image, new OnItemChildClickListener());
//            ((EventViewHolder) holder).setOnClickListener(R.id.f4_hashtag, new OnItemChildClickListener());
//            ((EventViewHolder) holder).setOnClickListener(R.id.main_event_title, new OnItemChildClickListener());
//
//            ((EventViewHolder) holder).setOnClickListener(R.id.f5_like_ic, new OnItemChildClickListener());
//            ((EventViewHolder) holder).setOnClickListener(R.id.f5_share_ic, new OnItemChildClickListener());
//            ((EventViewHolder) holder).setOnClickListener(R.id.f5_comment_ic, new OnItemChildClickListener());
            LikeButton b = ((EventViewHolder) holder).getView(R.id.f_like_ic);
            if (data.getMislikeBoolean())
                b.setLiked(true);
            else
                b.setLiked(false);
            b.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    EventBus.getDefault().post(new EventLiked(positions));
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    EventBus.getDefault().post(new EventUnliked(positions));
                }
            });

            ((EventViewHolder) holder).setOnClickListener(R.id.f_avatar, new OnItemChildClickListener());
            ((EventViewHolder) holder).setOnClickListener(R.id.f_hashtag, new OnItemChildClickListener());
            ((EventViewHolder) holder).setOnClickListener(R.id.f_comment_ic, new OnItemChildClickListener());

        }

        if (holder instanceof GossipViewHolder) {
            FeedModel data = getItem(positions);

            ((GossipViewHolder) holder).setText(R.id.f_like,String.valueOf(data.getMlikes()));
            ((GossipViewHolder) holder).setText(R.id.f_share,String.valueOf(data.getMshare()));
            ((GossipViewHolder) holder).setText(R.id.main_gossip_elapsed_time,data.getMgossipenddate());
            ((GossipViewHolder) holder).setText(R.id.f_name,data.getMname());
            ((GossipViewHolder) holder).setText(R.id.main_gossip_title,data.getMgossiptitle());
            ((GossipViewHolder) holder).setOnClickListener(R.id.main_gossip_title, new OnItemChildClickListener());

            Glide.with(mContext).load(data.getMavatar()).diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .priority(Priority.LOW)
                    .dontAnimate()
                    .into((ImageView)  ((GossipViewHolder) holder).getView(R.id.f_avatar));
            LikeButton b = ((GossipViewHolder) holder).getView(R.id.f_like_ic);

            b.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    EventBus.getDefault().post(new EventLiked(positions));
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    EventBus.getDefault().post(new EventUnliked(positions));
                }
            });
            LikeButton s = ((GossipViewHolder) holder).getView(R.id.f_share_ic);

            s.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    EventBus.getDefault().post(new EventShare(positions));
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    EventBus.getDefault().post(new EventShare(positions));
                }
            });


            ((GossipViewHolder) holder).setOnClickListener(R.id.f_avatar, new OnItemChildClickListener());
        }

        super.onBindViewHolder(holder, positions);
    }

    public class MediaViewHolder extends BaseViewHolder {
        public MediaViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class GossipViewHolder extends BaseViewHolder {
        public GossipViewHolder(View itemView) {
            super(itemView);

        }
    }

    public class EventViewHolder extends BaseViewHolder {
        public EventViewHolder(View itemView) {
            super(itemView);
        }
    }
}

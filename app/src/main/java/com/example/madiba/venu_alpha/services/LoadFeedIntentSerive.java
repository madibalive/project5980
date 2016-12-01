package com.example.madiba.venu_alpha.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.util.Log;

import com.example.madiba.venu_alpha.Actions.ActionLoadFeed;
import com.example.madiba.venu_alpha.models.GlobalConstants;
import com.example.madiba.venu_alpha.models.ModelFeedItem;
import com.example.madiba.venu_alpha.utils.TimeUitls;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LoadFeedIntentSerive extends IntentService {

    private static final String TAG=LoadFeedIntentSerive.class.getSimpleName();

    private static final String ACTION_LOAD = "com.example.madiba.venu_v3.servicesv2.action.LOAD";
    private static final String ACTION_BAZ = "com.example.madiba.venu_v3.servicesv2.action.BAZ";

    private static final String EXTRA_LIMIT = "com.example.madiba.venu_v3.servicesv2.extra.LIMIT";
    private static final String EXTRA_SKIP = "com.example.madiba.venu_v3.servicesv2.extra.SKIP";

    public LoadFeedIntentSerive() {
        super("LoadFeedIntentSerive");
    }


    public static void startActionLoad(Context context, int param1, int param2,String classname, String id) {
        Intent intent = new Intent(context, LoadFeedIntentSerive.class);
        intent.setAction(ACTION_LOAD);
        intent.putExtra(EXTRA_LIMIT, param1);
        intent.putExtra(EXTRA_SKIP, param2);
        context.startService(intent);
    }


    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, LoadFeedIntentSerive.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_LIMIT, param1);
        intent.putExtra(EXTRA_SKIP, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOAD.equals(action)) {
                final int param1 = intent.getIntExtra(EXTRA_LIMIT,20);
                final int param2 = intent.getIntExtra(EXTRA_SKIP,0);
                handleActionLOAD(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_LIMIT);
                final String param2 = intent.getStringExtra(EXTRA_SKIP);
                handleActionBaz(param1, param2);
            }
        }
    }



    private  void handleActionLOAD(int limit, int skip){
        List<ParseObject> mdatas= new ArrayList<>();
        List<ModelFeedItem> fdatas= new ArrayList<>();

        //INITIAL VARIABLES AND BUCKETS
        List<ParseObject> likes =new ArrayList<ParseObject>();
        List<ParseObject> thumbs = new ArrayList<ParseObject>();
        List<ParseObject> favs= new ArrayList<ParseObject>();

        // followers querys
        ParseQuery<ParseObject> followersQuery = ParseQuery.getQuery("FollowVersion3");
        followersQuery.whereEqualTo("from", ParseUser.getCurrentUser());

        // main peep query
        ParseQuery<ParseObject> mediaQuery = ParseQuery.getQuery("Peep");
        mediaQuery.whereMatchesKeyInQuery(GlobalConstants.OBJ_FROM, "to", followersQuery);

        // main feed query from me
        ParseQuery<ParseObject> mediaQueryMe = ParseQuery.getQuery("Peep");
        mediaQueryMe.whereEqualTo(GlobalConstants.OBJ_FROM, ParseUser.getCurrentUser());

        // private mediaQuery
        ParseQuery<ParseObject> privatemediaQuery = ParseQuery.getQuery("Peep");
        privatemediaQuery.whereEqualTo(GlobalConstants.INT_ISPRIVATE, GlobalConstants.TYPE_PRIVATE);
        privatemediaQuery.whereEqualTo(GlobalConstants.AR_GROUP, ParseUser.getCurrentUser().getObjectId());

        // combination of three queries
        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(mediaQuery);
        queries.add(mediaQueryMe);
        queries.add(privatemediaQuery);

        // extra condition on mainquery
        ParseQuery<ParseObject> mainMediaQuery = ParseQuery.or(queries);
        mainMediaQuery.orderByDescending("createdAt");
        mainMediaQuery.setLimit(limit);
        mainMediaQuery.setSkip(skip);
        mainMediaQuery.whereExists("image");
        mainMediaQuery.include("from");


        // Share QUERIES
        ParseQuery<ParseObject> shareQuery = ParseQuery.getQuery("ShareVersion3");
//                shareQuery.whereMatchesKeyInQuery("from","from", followersQuery);
        shareQuery.include("from");
        shareQuery.include("event");
        shareQuery.include("peep");
        shareQuery.setLimit(limit);
        shareQuery.setSkip(skip);
        //MAIN GOSSIP QUERY
        ParseQuery<ParseObject> gossipQuery = ParseQuery.getQuery("Gossip");
        gossipQuery.whereMatchesKeyInQuery(GlobalConstants.OBJ_FROM, GlobalConstants.OBJ_FROM, followersQuery);

        // GOSSIP ME QUERY
        ParseQuery<ParseObject> gossipQueryMe = ParseQuery.getQuery("Gossip");
        gossipQueryMe.whereEqualTo("user",ParseUser.getCurrentUser());

        // EXPERIMENTAL GOSSIP QUERY
        List<ParseQuery<ParseObject>> gossipQueries = new ArrayList<ParseQuery<ParseObject>>();
        gossipQueries.add(gossipQuery);
        gossipQueries.add(gossipQueryMe);

        ParseQuery<ParseObject> mainGossipQueries = ParseQuery.or(gossipQueries);
        mainGossipQueries.orderByDescending("createdAt");
        mainGossipQueries.setLimit(limit);
        mainGossipQueries.setSkip(skip);
        mainGossipQueries.include("from");
        mainGossipQueries.whereExists("image");

        //RELATION QUERY
        ParseQuery queryR = ParseQuery.getQuery("UserRelations");
        queryR.whereEqualTo("user",ParseUser.getCurrentUser());
        queryR.fromLocalDatastore();

        try {
            // main query load
            mdatas.addAll( mainMediaQuery.find());
            mdatas.addAll(shareQuery.find());
            mdatas.addAll(mainGossipQueries.find());
            ParseObject relation = queryR.getFirst();

            // getting user relations
            if (relation != null) {
                ParseRelation<ParseObject> relationLikes = relation.getRelation("likes");
                likes= relationLikes.getQuery().find();
                ParseRelation<ParseObject> relationThumbs = relation.getRelation("thumbs");
                thumbs = relationThumbs.getQuery().find();
                ParseRelation<ParseObject> Relationfav = relation.getRelation("favorites");
                favs = Relationfav.getQuery().find();
            }

            //RESORT ALL ACCORDING TO TIME
            Collections.sort(mdatas, (t1, t2) -> (t2.getCreatedAt().compareTo(t1.getCreatedAt())));

            for (ParseObject t : mdatas){
                Boolean isShared = false;
                ModelFeedItem feedItem = new ModelFeedItem();
                ParseObject currentObj;

                //set object

                //SETTING GLOBAL DATE
                feedItem.setDateToString(TimeUitls.getRelativeTime(t.getCreatedAt()));

                Log.i(TAG, "call: check is shared");
                if (t!=null&& t.getClassName().equals("ShareVersion3")) {
                    isShared = true;
                    feedItem.setShared(true);
                    Log.i(TAG, "call: shared");
                }else {
                    Log.i(TAG, "call: not shared");
                    feedItem.setShared(false);
                }

                // set avatar ,name,shares
                Log.i(TAG, "call: setting user avatar and aname ");

                if (t.getParseUser("from").getString("avatarUrl") != null)
                    feedItem.setAvatar(t.getParseUser("from").getParseFile("avatar").getUrl());

                feedItem.setName(t.getParseUser("from").getUsername());
                feedItem.setShr(t.getInt("shares"));

                //first branch version two
                Log.i(TAG, "call: setting grabbed object");
                if (isShared){
                    if (t.getInt("type")==1) {
                        Log.i(TAG, "call: grabbedObject set to event");
                        currentObj = t.getParseObject("event");
                    }
                    else {
                        Log.i(TAG, "grabbedObject: shared to peep");
                        currentObj =t.getParseObject("peep");
                    }
                }else{
                    Log.i(TAG, "grabbedObject: using directly not shared");
                    currentObj =t;
                }

                Log.i(TAG, "call: checking gossip" + currentObj.getClassName() + currentObj.getObjectId());
                if ( currentObj.getClassName().equals("Gossip")){

                    // SET TYPE
                    feedItem.setType(ModelFeedItem.TYPP_GOSSIP);

                    Log.i(TAG, "call: checking thumb size" );
                    if (relation != null){
                        feedItem.setGpIsThumbsUp(thumbs.contains(t));
                    }

                    feedItem.gpElapseTime=currentObj.getCreatedAt();

                    // set gossip title
                    feedItem.setGpTitle(currentObj.getString("title"));
                    feedItem.setGpChatId(currentObj.getInt("chatId"));

                    Log.i(TAG, "call: done gossip check");
                }

                else {

                    // set tag,comment,likes
                    feedItem.setHashtag(currentObj.getString("tag"));
                    feedItem.setCmt(currentObj.getInt("comments"));
                    feedItem.setRct(currentObj.getInt("likes"));

                    //if not event
                    Log.i(TAG, "call: checking not eventtest ");
                    if (!currentObj.getClassName().equals("EventsVersion")){

                        // check from likes list
                        Log.i(TAG, "call: checking likes size" );
                        if (relation != null){
                            feedItem.setPpIsLike(likes.contains(currentObj));
                        }
                        //set url
                        feedItem.setUrl(currentObj.getString("url"));

                        //set isVideo
                        if (currentObj.getInt("typeV2")==1){
                            feedItem.setType(ModelFeedItem.TYPP_MEDIA_VIDEO);
                        }else {
                            feedItem.setType(ModelFeedItem.TYPP_MEDIA_IMAGE);
                        }

                    }else{

                        Log.i(TAG, "call: setting type == event");
                        feedItem.setType(ModelFeedItem.TYPP_EVENT);


                        if (relation != null){
                            feedItem.setEvIsInterest(favs.contains(currentObj));
                        }

                        feedItem.setUrl(currentObj.getParseFile("image").getUrl());

                        feedItem.setEvTitle(currentObj.getString("title"));
                        feedItem.setEvDate(currentObj.getDate("date"));
                        feedItem.setEvTime(currentObj.getDate("time"));
                        feedItem.setEvDesc(currentObj.getString("desc"));

                    }// END OF EVENT

                } //END OF FOR EACH

                fdatas.add(feedItem);

            }

            // is first time
            if (skip==0){
//                ModelFeedItem.deleteAll(ModelFeedItem.class);
//                ModelFeedItem.saveInTx(fdatas);
//                EventBus.getDefault().post(new ActionLoadFeed(ModelFeedItem.listAll(ModelFeedItem.class),false));
            }else {
                EventBus.getDefault().post(new ActionLoadFeed(fdatas,false));
            }


        } catch (ParseException |SQLException e) {
            Log.i(TAG, "call: error"  +e.getMessage());
            EventBus.getDefault().post(new ActionLoadFeed(null,false));

            e.printStackTrace();
        }finally {
            mdatas.clear();

        }
    }


    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

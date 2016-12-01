package com.example.madiba.venu_alpha.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicommons.people.contact.Contact;

import com.example.madiba.venu_alpha.Actions.ActionCheck;
import com.example.madiba.venu_alpha.Actions.ActionLoadEvent;
import com.example.madiba.venu_alpha.Actions.ActionLoadMedia;
import com.example.madiba.venu_alpha.Actions.ActionLoadChallange;
import com.example.madiba.venu_alpha.Actions.ActionLoadChannel;
import com.example.madiba.venu_alpha.Actions.ActionLoadCircle;
import com.example.madiba.venu_alpha.models.DeprecatedFeedModel;
import com.example.madiba.venu_alpha.models.GlobalConstants;
import com.example.madiba.venu_alpha.models.ModelCircle;
import com.example.madiba.venu_alpha.models.PhoneContact;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class LoadService extends IntentService {

    private static final String ACTION_FOO = "com.example.madiba.venu_v2.services.action.FOO";
    private static final String TAG = "LOCALSERVICE";
    private static final String ACTION_BAZ = "com.example.madiba.venu_v2.services.action.BAZ";
    private static final String ACTION_UPDATE_CHAT = "com.example.madiba.venu_v2.services.action.UPDATE_CHAT";
    private static final String ACTION_LOAD_FEED = "com.example.madiba.venu_v2.services.action.LOAD_FEED";
    private static final String ACTION_LOAD_CHALLANGE = "com.example.madiba.venu_v2.services.action.LOAD_CHALLANGE";
    private static final String ACTION_LOAD_CHANNEL = "com.example.madiba.venu_v2.services.action.LOAD_CHANNEL";
    private static final String ACTION_LOAD_CIRCLE = "com.example.madiba.venu_v2.services.action.LOAD_CIRCLE";

    private static final String EXTRA_PARAM1 = "com.example.madiba.venu_v2.services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.madiba.venu_v2.services.extra.PARAM2";

    public LoadService() {
        super("LoadService");
    }


    public static void startActionFoo(Context context) {
        Intent intent = new Intent(context, LoadService.class);
        intent.setAction(ACTION_UPDATE_CHAT);
        context.startService(intent);
    }
    public static void startActionLoadCircle(Context context) {
        Intent intent = new Intent(context, LoadService.class);
        intent.setAction(ACTION_LOAD_CIRCLE);
        context.startService(intent);
    }

    public static void startActionChannelLoad(Context context) {
        Intent intent = new Intent(context, LoadService.class);
        intent.setAction(ACTION_LOAD_CHANNEL);
        context.startService(intent);
    }

    public static void startLoadFeed(Context context) {
        Intent intent = new Intent(context, LoadService.class);
        intent.setAction(ACTION_LOAD_FEED);
        context.startService(intent);
    }
    public static void startLoadChallange(Context context) {
        Intent intent = new Intent(context, LoadService.class);
        intent.setAction(ACTION_LOAD_CHALLANGE);
        context.startService(intent);
    }
    public static void startActionUpdateChat(Context context) {
        Intent intent = new Intent(context, LoadService.class);
        intent.setAction(ACTION_UPDATE_CHAT);
        context.startService(intent);
    }


    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, LoadService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_UPDATE_CHAT.equals(action)) {

                handleActionUpdateChat();
            } else if (ACTION_LOAD_CHALLANGE.equals(action)) {

                handleActionLoadChallange();
            }else if (ACTION_LOAD_FEED.equals(action)) {

                handleActionLoadFeed();
            }else if (ACTION_LOAD_CHANNEL.equals(action)) {

                handleActionLoadChannel();
            }else if (ACTION_LOAD_CIRCLE.equals(action)) {

                handleActionLoadCircle();
            }

        }
    }

    private void handleActionCheck(String id){
        ParseQuery followingQ = ParseQuery.getQuery("FollowVersion3");
        followingQ.whereEqualTo("from",ParseUser.getCurrentUser());
        followingQ.whereEqualTo("to",ParseUser.createWithoutData(ParseUser.class,id));
        followingQ.fromLocalDatastore();

        ParseObject a = null;
        try {
            a = followingQ.getFirst();
            if (a !=null){
                EventBus.getDefault().post(new ActionCheck(true,false));

            }else {
                EventBus.getDefault().post(new ActionCheck(false,false));

            }
        } catch (ParseException e) {
            EventBus.getDefault().post(new ActionCheck(false,true));
            e.printStackTrace();
        }
    }

    private void handleActionLoadMedia(String id,int limit,int skip){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Peep");
        query.whereEqualTo("from",ParseUser.createWithoutData(ParseUser.class,id));
        query.include("from");
        query.orderByAscending("createdAt");
        query.setLimit(limit);
        query.setSkip(skip);

        try {

            List<ParseObject> b = query.find();
            if (b.size()>0)
                EventBus.getDefault().post(new ActionLoadMedia(b,false,limit,skip));
            else
                EventBus.getDefault().post(new ActionLoadMedia(b,false,limit,skip));

        } catch (ParseException e) {
            EventBus.getDefault().post(new ActionLoadMedia(null,true,0,0));

        }
    }



    private void handleActionShare(String id,String className){
        ParseObject m =ParseObject.createWithoutData(id,className);
//        ParseCloud.callFunction()
    }

    private void handleActionDelete(String id,String className){

        ParseObject m = ParseObject.createWithoutData(className,id);
        try {
            m.fetch();
            m.save();

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    private void handleActionFollow(String id,String className){

        ParseObject m = new ParseObject("FollowVersion3");
        m.put("from", ParseUser.getCurrentUser());
        m.put("fromId",ParseUser.getCurrentUser().getObjectId());
        m.put("to",ParseUser.createWithoutData(ParseUser.class,id));
        m.put("toId",id);
        try {
            m.save();

            ParseUser user =ParseUser.createWithoutData(ParseUser.class,id);
            user.fetch();

//            List<ModelCircle> data= ModelCircle.find(ModelCircle.class,"type=?",String.valueOf(ModelCircle.FOLLOWER),"userid=?",user.getObjectId(),"limit?","1");

//            if (data.size()>0){
//
//            ModelCircle n= new ModelCircle();
//            n.setAvatar(user.getParseFile("avatar").getUrl());
//            n.setName(user.getUsername());
//            n.setParseId(m.getObjectId());
//            n.setParseClassName(m.getClassName());
//            n.setType(ModelCircle.FOLLOW);
//            n.setParseUserId(user.getObjectId());
//            n.setPhone(user.getString("phone"));
//            n.save();
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    private void handleActionLoadEvents(String id,int limit,int skip,String cateogry){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Peep");
        query.whereEqualTo("from",ParseUser.createWithoutData(ParseUser.class,id));
        query.include("from");
        query.orderByAscending("createdAt");
        query.setLimit(limit);
        query.setSkip(skip);

        try {

            List<ParseObject> b = query.find();
            if (b.size()>0)
                EventBus.getDefault().post(new ActionLoadEvent(b,false,limit,skip));
            else
                EventBus.getDefault().post(new ActionLoadEvent(b,false,limit,skip));

        } catch (ParseException e) {
            EventBus.getDefault().post(new ActionLoadEvent(null,true,0,0));
        }

    }

    private void handleActionLoadOfflineCircle(){
//        List<ModelCircle> mDatas = ModelCircle.listAll(ModelCircle.class);
//        EventBus.getDefault().post(new ActionLoadCircle(mDatas,false));


    }

    private void handleActionLoadCircle(){
        List<ParseObject> mDatasFollowers = new ArrayList<>();
        List<ParseObject> mDatasFollowing = new ArrayList<>();
        List<ModelCircle> mUniqueDatas = new ArrayList<>();
        List<PhoneContact> alContacts = new ArrayList<>();
        ArrayList<String> phoneNumbers = new ArrayList<>();

        ParseQuery<ParseObject> followsMeQuery = ParseQuery.getQuery("FollowVersion3");
        followsMeQuery.whereEqualTo("to", ParseUser.getCurrentUser());
        followsMeQuery.include("from");

        ParseQuery<ParseObject> followingQuery = ParseQuery.getQuery("FollowVersion3");
        followingQuery.whereEqualTo("from", ParseUser.getCurrentUser());
        followingQuery.include("to");

        try {
            mDatasFollowing = followingQuery.find();
            mDatasFollowers = followsMeQuery.find();

            ContentResolver cr = getApplicationContext().getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    PhoneContact person = new PhoneContact();

                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));
                    person.setId(id);
                    person.setUsername(name);


                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contactNumber=contactNumber.replaceAll("\\s+", "");
                            person.setPhoneNumber(contactNumber);
                            phoneNumbers.add(contactNumber);
                            break;
                        }
                        pCur.close();
                    }

                    alContacts.add(person);

                } while (cursor.moveToNext());
            } else {
                cursor.close();
            }


            for (ParseObject a: mDatasFollowing){
                Boolean isin=true;
                for (ParseObject b: mDatasFollowers){
                    if (b.getParseUser("from").equals(a.getParseUser("to"))){
                        isin =false;
                        Log.e(TAG, "handleActionLoadCircle: match" );

                    }
                }
                if (isin) {
                    ModelCircle m = new ModelCircle();
                    m.setType(ModelCircle.FOLLOW);
                    if (a.getParseUser("to").getParseFile("avatar")!=null)
                        m.setAvatar(a.getParseUser("to").getParseFile("avatar").getUrl());
                    m.setName(a.getParseUser("to").getUsername());
                    m.setPhone(a.getParseUser("to").getString("phone"));
                    m.setParseClassName(a.getClassName());
                    m.setParseId(a.getObjectId());
                    m.setParseUserId(a.getParseUser("to").getObjectId());
//                    m.save();
                    mUniqueDatas.add(m);
                }
            }
            for (ParseObject a : mDatasFollowers){
                ModelCircle m = new ModelCircle();

                m.setType(ModelCircle.FOLLOWER);
                if (a.getParseUser("from").getParseFile("avatar")!=null)
                    m.setAvatar(a.getParseUser("from").getParseFile("avatar").getUrl());
                m.setName(a.getParseUser("from").getUsername());
                m.setPhone(a.getParseUser("from").getString("phone"));
                m.setParseClassName(a.getClassName());
                m.setParseId(a.getObjectId());
                m.setParseUserId(a.getParseUser("from").getObjectId());
//                m.save();
                mUniqueDatas.add(m);


            }

            for (PhoneContact a : alContacts){
                Boolean isin=true;

                for (ModelCircle b:mUniqueDatas){
                    if (b.getPhone() != null && a.getPhoneNumber() !=null){
                        if (a.getPhoneNumber().equals(b.getPhone())){
                            isin=false;
                        }
                    }
                }
                if (isin && a.getPhoneNumber()!=null ) {
                    ModelCircle m = new ModelCircle();
                    m.setType(ModelCircle.LOCAL);
                    m.setPhone(a.getPhoneNumber());
                    m.setName(a.getUsername());
                    m.setLocalId(a.getId());
//                    m.save();
                    mUniqueDatas.add(m);
                }
            }
//            ModelCircle.deleteAll(ModelCircle.class);
//            for (ModelCircle c:mUniqueDatas){
//                c.save();
//            }

//            List<ModelCircle> returnData = ModelCircle.listAll(ModelCircle.class);

//            EventBus.getDefault().post(new ActionLoadCircle(returnData,false));


            Log.e(TAG, "handleActionLoadCircle: unique data :" + mUniqueDatas.size() );


        } catch (ParseException |SQLException | NullPointerException e) {
            Log.e(TAG, "handleActionLoadCircle: error :" +e.getMessage() );
            EventBus.getDefault().post(new ActionLoadCircle(null,false));

            e.printStackTrace();
        }finally {
            Log.i(TAG, "handleActionLoadCircle: final called");
            mDatasFollowers.clear();
            mDatasFollowing.clear();
            mUniqueDatas.clear();
        }

    }
    private void handleActionLoadCircleV2(){

    }

    private void handleActionLoadChannel() {
        ParseQuery<ParseObject> channelQuery = ParseQuery.getQuery("Peep");
        channelQuery.setLimit(4);
//        channelQuery.include("image");
//        channelQuery.whereExists("image");
//        channelQuery.orderByAscending("pos");
        try {
            List<ParseObject> mdata = channelQuery.find();
            EventBus.getDefault().post(new ActionLoadChannel(mdata,false));
            mdata.clear();

        } catch (ParseException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new ActionLoadChannel(null,false));

        } finally {
        }

    }


    private void handleActionLoadFeed(){
        int PAGE_SIZE=10;

        List<ParseObject> mdatas= new ArrayList<>();
        List<DeprecatedFeedModel> fdatas= new ArrayList<>();

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
        mainMediaQuery.setLimit(5);
        mainMediaQuery.whereExists("image");
        mainMediaQuery.include("from");


        // Share QUERIES
        ParseQuery<ParseObject> shareQuery = ParseQuery.getQuery("ShareVersion3");
//                shareQuery.whereMatchesKeyInQuery("from","from", followersQuery);
        shareQuery.include("from");
        shareQuery.include("event");
        shareQuery.include("peep");
        shareQuery.setLimit(PAGE_SIZE);

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
        mainMediaQuery.orderByDescending("createdAt");
        mainMediaQuery.setLimit(PAGE_SIZE);
        mainGossipQueries.include("from");
        mainMediaQuery.whereExists("image");

        try {


            mdatas.addAll( mainMediaQuery.find());
            mdatas.addAll(shareQuery.find());
            mdatas.addAll(mainGossipQueries.find());
//            mdatas.addAll(gossipQueryMe.find());


            //INITIAL VARIABLES AND BUCKETS
            long  now = System.currentTimeMillis();
            List<ParseObject> likes =new ArrayList<ParseObject>();
            List<ParseObject> thumbs = new ArrayList<ParseObject>();
            List<ParseObject> favs= new ArrayList<ParseObject>();

            //sort date
            Collections.sort(mdatas, new Comparator<ParseObject>() {
                @Override
                public int compare(ParseObject t1, ParseObject t2) {
                    return (t2.getCreatedAt().compareTo(t1.getCreatedAt()));
                }
            });


            // getting user relations
            ParseQuery queryR = ParseQuery.getQuery("UserRelations");
            queryR.whereEqualTo("user",ParseUser.getCurrentUser());
            ParseObject relation = queryR.getFirst();

            if (relation != null) {

                ParseRelation<ParseObject> relationLikes = relation.getRelation("likes");
                likes= relationLikes.getQuery().find();
                ParseRelation<ParseObject> relationThumbs = relation.getRelation("thumbs");
                thumbs = relationThumbs.getQuery().find();
                ParseRelation<ParseObject> Relationfav = relation.getRelation("favorites");
                favs = Relationfav.getQuery().find();
            }


            for (ParseObject t : mdatas){

                Boolean isShared = false;

                ParseObject grabbedObject;
                DeprecatedFeedModel pt = new DeprecatedFeedModel();
                //set object
                pt.setObject(t);

                //setDate

                pt.setMdate(DateUtils.getRelativeTimeSpanString(t.getCreatedAt().getTime(),
                        System.currentTimeMillis(),
                        DateUtils.SECOND_IN_MILLIS)
                        .toString().toLowerCase());



                Log.i(TAG, "call: check is shared");
                //setshated
                if (t!=null&& t.getClassName().equals("ShareVersion3")) {
                    pt.setShared(true);
                    isShared = true;
                    Log.i(TAG, "call: shared");
                }else {
                    Log.i(TAG, "call: not shared");
                }

                // set avatar ,name,shares
                Log.i(TAG, "call: setting user avatar and aname ");
                pt.setMshare(t.getInt("shares"));
                pt.setMname(t.getParseUser("from").getUsername());
                if (t.getParseUser("from").getString("avatarUrl") != null)
                    pt.setMavatar(t.getParseUser("from").getString("avatarUrl"));

                //first branch version two
                Log.i(TAG, "call: setting grabbed object");
                if (isShared){
                    if (t.getInt("type")==1) {
                        Log.i(TAG, "call: grabbedObject set to event");
                        grabbedObject = t.getParseObject("event");
                    }
                    else {
                        Log.i(TAG, "grabbedObject: shared to peep");
                        grabbedObject = t.getParseObject("peep");
                    }
                }else{
                    Log.i(TAG, "grabbedObject: using directly not shared");
                    grabbedObject = t;

                }


                // first branch if !gossip / gossip
                Log.i(TAG, "call: checking gossip" + grabbedObject.getClassName() + grabbedObject.getObjectId());
                if ( grabbedObject.getClassName().equals("Gossip")){
                    // SET TYPE
                    pt.setMtype(DeprecatedFeedModel.GOSSIP);

                    //set thumbs up
                    // check from local likes
                    Log.i(TAG, "call: checking thumb size" );
                    if (relation != null)
                        pt.setMislikeBoolean(thumbs.contains(t));

                    // set end date
                    // parse true relative
                    long diffInMillisec = grabbedObject.getCreatedAt().getTime() -now;
                    long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMillisec);

                    float hours = diffInSec % 24;
                    Log.i(TAG, "call: remaining time "+ hours);
                    pt.setMgossipenddate(String.valueOf(Math.round(hours)));

                    // set gossip title
                    pt.setMgossiptitle(grabbedObject.getString("title"));

                    Log.i(TAG, "call: done gossip check");
                }

                else {

                    // set tag,comment,likes
                    pt.setMhashtag(grabbedObject.getString("tag"));
                    pt.setMlikes(grabbedObject.getInt("likes"));
                    pt.setMcomment(grabbedObject.getInt("comments"));

                    //if not event
                    Log.i(TAG, "call: checking not eventtest ");
                    if (!grabbedObject.getClassName().equals("EventsVersion3")){

                        //set loved
                        // check from likes list
                        Log.i(TAG, "call: checking likes size" );

                        if (relation != null)
                            pt.setMislikeBoolean(likes.contains(grabbedObject));

                        //set url
                        pt.setMurl(grabbedObject.getString("url"));

                        //set isVideo
                        if (grabbedObject.getInt("typeV2")==1){
                            Log.i(TAG, "call: got video" +grabbedObject.getString("video"));
                            pt.setMtype(DeprecatedFeedModel.MEDIA_VIDEO);
                        }else {
                            pt.setMtype(DeprecatedFeedModel.MEDIA_IMAGE);
                        }

                    }else{

                        //set type
                        Log.i(TAG, "call: setting type == event");
                        pt.setMtype(DeprecatedFeedModel.EVENT);

                        //set fave
                        // check via likes
                        Log.i(TAG, "call: checking fav size" );

                        if (relation != null)
                            pt.setMislikeBoolean(favs.contains(grabbedObject));


                        Log.i(TAG, "call: sett url");
                        pt.setMurl(grabbedObject.getParseFile("image").getUrl());

                        //setInterest
//                                 check via interest
//                                    pt.setIsinterested(listInterest.contains(grabbedObject));

                        //set title,location
                        pt.setMeventtitle(grabbedObject.getString("title"));
//                                pt.setMeventlocation(grabbedObject.getString("Location"));

                        //is past
                        Log.i(TAG, "call: checking past");
                        Date today=new Date();

                        if (today.before(grabbedObject.getDate("date"))){
                            Log.i(TAG, "call: setting  passed");
                            //setDate not past/time/price/location
                            String day = (String) DateFormat.format("dd", grabbedObject.getDate("date")); //20
                            String stringMonth = (String) DateFormat.format("MMM", grabbedObject.getDate("date")); //Jun
                            String timeOfevent = (String) DateFormat.format("HH:mm", grabbedObject.getDate("time")); //Jun
                            pt.setMprice(grabbedObject.getInt("price"));
                            Log.i(TAG, "call: past == fales : " + day + "/n"+stringMonth);
                            pt.setMeventdate(day + "/n"+stringMonth);

                            pt.setMeventtime(timeOfevent);

                        }else {
                            //setDate not past/time/price/location
                            Log.i(TAG, "call: setting not passed");
                            String day = (String) DateFormat.format("dd", grabbedObject.getUpdatedAt()); //20
                            String timeOfevent = (String) DateFormat.format("HH:mm", grabbedObject.getDate("time")); //Jun
                            String stringMonth = (String) DateFormat.format("MMM", grabbedObject.getUpdatedAt()); //Jun
//                                            pt.setMprice(grabbedObject.getInt("price"));
                            pt.setMeventdate(day + " \n "+stringMonth);
                            pt.setMeventtime(timeOfevent);
                        }


                    }
                }

                Log.i(TAG, "call: add data" + grabbedObject.getClassName());
                fdatas.add(pt);
            }

//            EventBus.getDefault().post(new EventFeedLoad(fdatas,false));




        } catch (ParseException e) {
            Log.i(TAG, "call: error"  +e.getMessage());
//            EventBus.getDefault().post(new EventFeedLoad(null,false));

            e.printStackTrace();
        }finally {

        }
    }

    private void handleActionLoadChallange() {
        List<ParseObject> mDatas = new ArrayList<>();
        ParseObject me ;
        int state  = 0;


        //FOLLOWERS QIUERY
        ParseQuery<ParseObject> followersQuery = ParseQuery.getQuery("FollowVersion3");
        followersQuery.whereEqualTo("to", ParseUser.getCurrentUser());
//
//        ParseQuery<ParseObject> gossipQuery = ParseQuery.getQuery("Gossip");
//        gossipQuery.whereMatchesKeyInQuery(GlobalConstants.OBJ_FROM, GlobalConstants.OBJ_FROM, followersQuery);


        //CHALLANGE ME
        ParseQuery<ParseObject> challangeQueryMe = ParseQuery.getQuery("Peep");
//        challangeQueryMe.whereStartsWith(GlobalConstants.STRING_HASHTAG, "venuchallange");
//        challangeQueryMe.whereEqualTo("from", ParseUser.getCurrentUser());
        challangeQueryMe.include(GlobalConstants.OBJ_FROM);

        //TOP 10 QUERY
        ParseQuery<ParseObject> challangeQuery = ParseQuery.getQuery("Peep");
//        challangeQuery.whereStartsWith(GlobalConstants.STRING_HASHTAG, "venuchallange");
        challangeQuery.setLimit(20);
        challangeQuery.include(GlobalConstants.OBJ_FROM);
        challangeQuery.orderByAscending("createdAt");


        try {
            Log.i(TAG, "handleActionLoadChallange: starting challange feed");
            me = challangeQueryMe.getFirst();

            mDatas =challangeQuery.find();

            if (me !=null){
                if (mDatas.contains(me)){
                    EventBus.getDefault().post(new ActionLoadChallange(mDatas,false));
                }else {
                    mDatas.add(me);
                    EventBus.getDefault().post(new ActionLoadChallange(mDatas,false));
                }
            }else {
                EventBus.getDefault().post(new ActionLoadChallange(mDatas,false));
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ChallangeLoad", "doInBackground: " );
            EventBus.getDefault().post(new ActionLoadChallange(null,false));

        }

    }


    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void handleActionUpdateChat() {
        final List<Contact> contactList = new ArrayList<Contact>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Activities");
        query.whereEqualTo("to",ParseUser.getCurrentUser());
        query.include("from");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    Context context = getApplicationContext();
                    AppContactService appContactService = new AppContactService(context);
                    Log.i("DISPATCH", "SUCCESS: " + objects.size()  );
                    for (ParseObject u : objects){
                        Contact contact = new Contact();
                        contact.setUserId(u.getParseUser("from").getObjectId());
                        contact.setFullName(u.getParseUser("from").getUsername());
                        contact.setEmailId(u.getParseUser("from").getEmail());
                        contact.setImageURL("R.drawable.ic_fingerprint");
                        Log.i("DISPATACH", "done:  " + contact.toString());
                        contactList.add(contact);
                    }
                    appContactService.addAll(contactList);

                }else {
                    Log.e("DISPATCH", "done:error  " + e.getMessage());
                }
            }
        });

    }
}

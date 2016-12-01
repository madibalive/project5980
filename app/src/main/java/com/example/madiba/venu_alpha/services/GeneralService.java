package com.example.madiba.venu_alpha.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import com.example.madiba.venu_alpha.R;
import com.example.madiba.venu_alpha.models.GlobalConstants;
import com.example.madiba.venu_alpha.utils.ColorUtils;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GeneralService extends IntentService {
    private static final String TAG = GeneralService.class.getSimpleName();

    private static final String ACTION_FOLLOWS = "com.example.madiba.venu_v2.action.FOLLOW";
    private static final String ACTION_UPLOAD_VID = "com.example.madiba.venu_v2.action.UPLOAD_VIDEO";
    private static final String ACTION_NOTIFICATIONS = "com.example.madiba.venu_v2.action.NOTIFICATIONS";
    private static final String ACTION_SEND_PUSH_MSG = "com.example.madiba.venu_v2.action.PUSH_MSG";
    private static final String ACTION_UPLOAD_IMAGE = "com.example.madiba.venu_v2.action.UPLOUD_IMAGE";
    private static final String ACTION_UPLOAD_EVENT = "com.example.madiba.venu_v2.action.UPLOUD_EVENT";
    private static final String ACTION_LIKE = "com.example.madiba.venu_v2.action.LIKE";
    private static final String ACTION_GENERIC_ACTION = "com.example.madiba.venu_v2.action.GENERIC_ACTION";
    private static final String ACTION_INTEREST = "com.example.madiba.venu_v2.action.INTEREST";
    private static final String ACTION_ADD_REM_LIKE = "com.example.madiba.venu_v2.action.LIKE2";
    private static final String ACTION_SHARE = "com.example.madiba.venu_v2.action.SHARE";
    private static final String ACTION_STARTUP = "com.example.madiba.venu_v2.action.STARTUP";

    public GeneralService() {
        super("GeneralService");
    }


    public static void startActionEventUploud(Context context, String id, String filePath,String classname) {
        Log.i(TAG, "startActionEventUploud: " + id + " " + filePath);
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_UPLOAD_EVENT);
        intent.putExtra("object_id", id);
        intent.putExtra("uri", filePath);
        intent.putExtra("className", classname);
        Log.i(TAG, "startActionEventUploud: starting services ");
        context.startService(intent);
    }
    public static void startActionNotification(Context context, String id,String classname) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_NOTIFICATIONS);
        intent.putExtra("object_id", id);
        intent.putExtra("className", classname);
        Log.i(TAG, "startActionEventUploud: starting services ");
        context.startService(intent);
    }

    public static void startActionLike(Context context, Boolean state, String id, String className) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_LIKE);
        intent.putExtra("state", state);
        intent.putExtra("id", id);
        intent.putExtra("className", className);
        Log.i(TAG, "startActionLike: starting anction" + className);
        context.startService(intent);
    }


    public static void startActionGenericAction(Context context, Boolean state, String id, String className,String relName) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_GENERIC_ACTION);
        intent.putExtra("state", state);
        intent.putExtra("id", id);
        intent.putExtra("relName", relName);
        intent.putExtra("className", className);
        context.startService(intent);
    }

    public static void startActionShare(Context context,  String id, String className) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_SHARE);
        intent.putExtra("id", id);
        intent.putExtra("className", className);
        context.startService(intent);
    }

    public static void startActionStartUp(Context context) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_STARTUP);
        context.startService(intent);
    }

    public static void startActionInterest(Context context, Boolean state, String id, String className) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_LIKE);
        intent.putExtra("state", state);
        intent.putExtra("id", id);
        intent.putExtra("classNmae", className);
        context.startService(intent);
    }



    public static void startActionImgUploud(Context context, String uri, String recipient_id, String className) {

        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_UPLOAD_IMAGE);
        intent.putExtra("uri", uri);
        intent.putExtra("class_name", className);
        intent.putExtra("recipient_id", recipient_id);

        context.startService(intent);
    }

    public static void startActionVidUploud(Context context, String uri, String id,String className) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_UPLOAD_VID);
        intent.putExtra("className", className);
        intent.putExtra("filePath", uri);
        intent.putExtra("id", id);
        context.startService(intent);
    }


    public static void startActionSendPushMsg(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_SEND_PUSH_MSG);
        intent.putExtra("recipient_id", param1);
        intent.putExtra("conversation_id", param2);
        context.startService(intent);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent: started");
        if (intent != null) {
            Log.i(TAG, "onHandleIntent: " +intent.getAction());
            final String action = intent.getAction();
            if (ACTION_UPLOAD_EVENT.equals(action)) {
                final String event_id = intent.getStringExtra("object_id");
                final String image_uri = intent.getStringExtra("uri");
                final String className = intent.getStringExtra("className");

                Log.i(TAG, "onHandleIntent: passing to background");
                handleActionEventUploud(event_id, image_uri,false,className);

            } else if (ACTION_LIKE.equals(action)) {
                Log.i(TAG, "onHandleIntent: liked action trigger");
                final String className = intent.getStringExtra("className");
                final String id = intent.getStringExtra("id");
                final Boolean state = intent.getBooleanExtra("state", false);
                handleActionLike(state, id, className);


            } else if (ACTION_GENERIC_ACTION.equals(action)) {
                Log.i(TAG, "onHandleIntent: liked action trigger");
                final String className = intent.getStringExtra("className");
                final String id = intent.getStringExtra("id");
                final String relName = intent.getStringExtra("relName");
                final Boolean state = intent.getBooleanExtra("state", false);
                handleActionGenericAction(state, id, className,relName);


            } else if (ACTION_STARTUP.equals(action)) {
                Log.i(TAG, "onHandleIntent: liked action trigger");
                handleActionUpdateRelation();

            } else if (ACTION_SHARE.equals(action)) {
                Log.i(TAG, "onHandleIntent: liked action trigger");
                final String className = intent.getStringExtra("className");
                final String id = intent.getStringExtra("id");
                handleActionShare( id, className);

            } else if (ACTION_INTEREST.equals(action)) {
                final String className = intent.getStringExtra("className");
                final String id = intent.getStringExtra("id");
                final Boolean state = intent.getBooleanExtra("state", false);
                handleActionInterested(state, id, className);

            } else if (ACTION_SEND_PUSH_MSG.equals(action)) {

                final String param1 = intent.getStringExtra("Recipient_Id");
                final String param2 = intent.getStringExtra("Conversation_Id");
                final String message = intent.getStringExtra("message");
                handleActionPushMsg(param1, param2, message);

            } else if (ACTION_UPLOAD_IMAGE.equals(action)) {

                final String uri = intent.getStringExtra("uri");
                final String className = intent.getStringExtra("class_name");
                final String recipient_id = intent.getStringExtra("recipient_id");
                saveScaledPhoto(uri, recipient_id, className);


            } else if (ACTION_UPLOAD_VID.equals(action)) {
                final String uri = intent.getStringExtra("filePath");
                final String className = intent.getStringExtra("className");
                final String recipient_id = intent.getStringExtra("id");

                handleActionVidUploud2(uri, recipient_id, className);

            }else {
                Log.i(TAG, "onHandleIntent: no action matched");
            }
        }
    }




    private void handleActionEventUploud(String event_id, String uri,Boolean isVideo,String className) {
        Log.e(TAG, "handleActionEventUploud: started  id: "+ event_id + " image  :  " + uri + " classname : " + className);

        Bitmap bitmap = BitmapFactory.decodeFile(uri);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (bitmap!=null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            byte[] byteData = outputStream.toByteArray();

            ParseFile photoFile = new ParseFile(ParseUser.getCurrentUser().getUsername(), byteData);

            try {
                photoFile.save();
                ParseObject event = ParseObject.createWithoutData(className, event_id);
                event.fetchFromLocalDatastore();
                event.put("image", photoFile); // //
                event.save();

                ParseObject shareObject = new ParseObject("ShareVersion3");
                shareObject.put("from", ParseUser.getCurrentUser());
                shareObject.put("fromID", ParseUser.getCurrentUser().getObjectId());
                shareObject.put("event", event);
                shareObject.put("eventId", event.getObjectId());
                shareObject.put("type",1);
                shareObject.save();
                createNotification("event created  successfully", "", 200);

                Log.i(TAG, "handleActionEventUploud: saving here");
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e(TAG, "handleActionEventUploud: error " + e.getMessage());
                createNotification(e.getMessage(),"", 200);

            }
        }else {
            Log.e(TAG, "handleActionEventUploud:  file not found"  );
        }

    }

    private void handleCreateNotification(String event_id,String className){
        ParseObject object = ParseObject.createWithoutData(className, event_id);
        try {
            object.fetch();
            ParseObject notifcationObject = new ParseObject(GlobalConstants.CLASS_NOTIFICATION);
            notifcationObject.put("from", ParseUser.getCurrentUser());
            notifcationObject.put("fromID", ParseUser.getCurrentUser().getObjectId());
            notifcationObject.put("to",object);
            notifcationObject.put("toId", object.getObjectId());
            notifcationObject.put("className",object.getClassName());
            notifcationObject.put("object_id", object.getObjectId());

            if (object.getClassName().equals(GlobalConstants.CLASS_EVENT)){
                notifcationObject.put("type",1);
                notifcationObject.put("event",object);

            }else if(className.equals(GlobalConstants.CLASS_PEEP)){
                notifcationObject.put("type",0);
                notifcationObject.put("peep",object);



            }
            notifcationObject.save();
            createNotification("event created  successfully", "", 200);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void handleActionPushMsg(String recId, String convId, String msg) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("recipient_id", recId);
        params.put("conversation_id", convId);
        params.put("message", msg);

        ParseCloud.callFunctionInBackground("sendPush", params, new
                FunctionCallback<Object>() {
                    @Override
                    public void done(Object object, ParseException e) {
                        if (e == null) {
                        } else {

                        }
                    }
                });
    }

    private void handleActionLike(Boolean state, String id, String className) {
        Log.i(TAG, "handleActionLike: started id : "+ id + " classname: "+className);
        if (id != null && className !=null) {
            ParseObject m = ParseObject.createWithoutData(className, id);

            ParseQuery query = ParseQuery.getQuery("UserRelations");
            query.whereEqualTo("user",ParseUser.getCurrentUser());
            try {
                ParseObject queryObject = query.getFirst();

                if (queryObject !=null) {
                    ParseRelation<ParseObject> relation = queryObject.getRelation("likes");

                    if (state)
                        relation.add(m);
                    else
                        relation.remove(m);

                    queryObject.save();
                }

                Log.i(TAG, "handleActionLike: saved");

            } catch (ParseException e) {
                e.printStackTrace();
            }



        }

    }

    private void handleActionGenericAction(Boolean state, String id, String className,String relname) {
        Log.i(TAG, "generic : started id : "+ id + " classname: "+className +"rel " +relname + " state " + state);
        if (id != null && className !=null) {
            ParseObject m = ParseObject.createWithoutData(className, id);

            ParseQuery query = ParseQuery.getQuery("UserRelations");
            query.whereEqualTo("user",ParseUser.getCurrentUser());
            try {
                ParseObject queryObject = query.getFirst();

                if (queryObject !=null) {
                    ParseRelation<ParseObject> relation = queryObject.getRelation(relname);
                    if (state)
                        relation.add(m);
                    else
                        relation.remove(m);

                    queryObject.save();
                    Log.i(TAG, "handleActionLike: saved");

                }else
                    Log.i(TAG, "handleActionLike: query returned null");

            } catch (ParseException e) {
                e.printStackTrace();
            }



        }

    }

    private void handleActionShare( String id, String className) {
        Log.i(TAG, "handleActionLike: started id : " + id + " classname: " + className);

        if (id != null && className != null) {
            ParseObject m = ParseObject.createWithoutData(className, id);
            try {

                ParseQuery<ParseObject> shareQ = ParseQuery.getQuery("Share");
                shareQ.whereEqualTo("object", m);
                shareQ.whereEqualTo("from", ParseUser.getCurrentUser());

                ParseObject exist = shareQ.getFirst();

                if (exist == null) {
                    m.fetch();
                    m.increment("shares");
                    m.save();
                    Log.i(TAG, "handleActionShare: about to save");

                    ParseObject shareObject= new ParseObject("Share");
                    shareObject.put("from", ParseUser.getCurrentUser());
                    shareObject.put("fromID",ParseUser.getCurrentUser().getObjectId());
                    shareObject.put("object",m);
                    shareObject.save();
                }else {
                    Log.i(TAG, "handleActionShare: already shared");
                }


            } catch (ParseException e) {
                Log.i(TAG, "handleActionShare: error" + e.getMessage());

            }

        }
    }

    private void handleActionInterested(Boolean state, String id, String className) {

        ParseObject m = ParseObject.createWithoutData(className, id);

        ParseQuery relationsQ = ParseQuery.getQuery("UserRelations");
        relationsQ.whereEqualTo("user",ParseUser.getCurrentUser());
        ParseObject relations = null;
        try {
            relations = relationsQ.getFirst();

            ParseRelation<ParseObject> relationInterest = relations.getRelation("Interest");
            ParseQuery<ParseObject> relationInterestQuery =relationInterest.getQuery();
            relationInterestQuery.whereEqualTo("Event",m);
            relationInterestQuery.getFirst();

            if (relationInterestQuery == null){
                relationInterest.add(m);
                relations.save();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        // add new relation




    }

    private void handleActionUpdateRelation(){

        ParseQuery relations = ParseQuery.getQuery("UserRelations");
        relations.whereEqualTo("user",ParseUser.getCurrentUser());
        try {
            ParseObject relationLikeQ = relations.getFirst();

            ParseRelation<ParseObject> relationLikes = relationLikeQ.getRelation("likes");
            List<ParseObject> likes = relationLikes.getQuery().find();
            if (likes.size()> 0){
                ParseObject.unpinAll("USER_LIKES");
                ParseObject.pinAll("USER_LIKES",likes);
            }

            ParseRelation<ParseObject> relationFav = relationLikeQ.getRelation("favourite");
            List<ParseObject> favs = relationFav.getQuery().find();
            if (favs.size()> 0){
                ParseObject.unpinAll("USER_FAVORITES");
                ParseObject.pinAll("USER_FAVORITES",favs);
            }

            ParseRelation<ParseObject> relationThumbs = relationLikeQ.getRelation("thumbsup");
            List<ParseObject> thumbs = relationThumbs.getQuery().find();
            if (thumbs.size()> 0){
                ParseObject.unpinAll("USERS_THUMBS_UP");
                ParseObject.pinAll("USERS_THUMBS_UP",thumbs);
            }
            Log.i(TAG, "handleActionUpdateRelation: all user relation check");

        } catch (ParseException e) {
            e.printStackTrace();

            Log.i(TAG, "handleActionUpdateRelation: "  +e.getMessage());
        }

    }

    private void handleActionVidUploud2(String filepath, String id, String classname) {
        Log.i(TAG, "handleActionVidUploud2: " + filepath + " " + id + " " + classname);
        Map config = new HashMap();
        config.put("cloud_name", "venu-video");
        config.put("api_key", "912582153739168");
        config.put("api_secret", "jZpLwLo_LsMm5ZVipbRrAnvPpfk");

        Cloudinary cloudinary = new Cloudinary(config);
        final String url;

        File file = new File(filepath);

        @SuppressWarnings("rawtypes")
        Map cloudinaryResult;
        if (file.exists()) {
            try {
                // Cloudinary: Upload file using the retrieved signature and upload params
                cloudinaryResult = cloudinary.uploader().upload(file, ObjectUtils.asMap("resource_type", "video"));
                Log.i("UploadUrl", cloudinaryResult.get("url").toString());
                url = cloudinaryResult.get("url").toString();

                //save pallete

                //save thumb
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filepath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                thumb.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                byte[] scaledData = bos.toByteArray();

                Palette palette = Palette.from(thumb).generate();
                int deflt = 0x000000;
                int color = palette.getVibrantColor(deflt);
                ParseFile photoFile = new ParseFile(ParseUser.getCurrentUser().getUsername(), scaledData);
                photoFile.save();
                //update object

                ParseObject media = ParseObject.createWithoutData(classname, id);

                media.fetchFromLocalDatastore();
                media.put("videoUrl", url);
                media.put("typeV2",1);
                media.put("image",photoFile);
                media.put("url",photoFile.getUrl());
                media.put("pallete",String.valueOf(color));
                media.save();
                createNotification("peep sent successfully","",100);

                thumb.recycle();



//                media.fetchInBackground(new GetCallback<ParseObject>() {
//                    @Override
//                    public void done(ParseObject object, ParseException e) {
//                        if (e ==null){
//                            object.put("videoUrl", url);
//                            object.put("type",true);
//                            try {
//                                object.save();
//                                createNotification("peep sent successfully","",100);
//
//                            } catch (ParseException e1) {
//                                e1.printStackTrace();
//                                createNotification("peep failed",e.getMessage(),100);
//
//                            }
//
//                        }else {
//                            Log.i(TAG, "done: " +e.getMessage());
//                        }
//                    }
//                });


            } catch (RuntimeException | IOException e) {
                Log.e("upload", "Error uploading file");
                createNotification("failed to send peep",e.getMessage(),100);

            } catch (ParseException e) {
                e.printStackTrace();
                createNotification("failed to send peep",e.getMessage(),100);

            }

        }
    }


    private void saveScaledPhoto(String uri, final String recipient_id, final String className) {

        Log.i(TAG, "saveScaledPhoto: " + recipient_id + className + " " + uri);

        final Bitmap bitmap = BitmapFactory.decodeFile(uri);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (bitmap!=null){
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,outputStream);

            final byte[] byteData = outputStream.toByteArray();

            // get pallete
            Palette.from(bitmap)
                    .clearFilters()
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            float darkAlpha = 0.25f;
                            float lightAlpha =0.5f;
                            int rippleColor = R.color.mid_grey_200;

                            if (palette != null) {
                                // try the named swatches in preference order
                                if (palette.getVibrantSwatch() != null) {
                                    rippleColor =
                                            ColorUtils.modifyAlpha(palette.getVibrantSwatch().getRgb(), darkAlpha);

                                } else if (palette.getLightVibrantSwatch() != null) {
                                    rippleColor = ColorUtils.modifyAlpha(palette.getLightVibrantSwatch().getRgb(),
                                            lightAlpha);
                                } else if (palette.getDarkVibrantSwatch() != null) {
                                    rippleColor = ColorUtils.modifyAlpha(palette.getDarkVibrantSwatch().getRgb(),
                                            darkAlpha);
                                } else if (palette.getMutedSwatch() != null) {
                                    rippleColor = ColorUtils.modifyAlpha(palette.getMutedSwatch().getRgb(), darkAlpha);
                                } else if (palette.getLightMutedSwatch() != null) {
                                    rippleColor = ColorUtils.modifyAlpha(palette.getLightMutedSwatch().getRgb(),
                                            lightAlpha);
                                } else if (palette.getDarkMutedSwatch() != null) {
                                    rippleColor =
                                            ColorUtils.modifyAlpha(palette.getDarkMutedSwatch().getRgb(), darkAlpha);
                                }

                                ParseFile imageByte = new ParseFile(ParseUser.getCurrentUser().getUsername(),byteData);

                                try {
                                    imageByte.save();
                                    ParseObject peep = ParseObject.createWithoutData(className, recipient_id);
                                    peep.fetchFromLocalDatastore();
                                    peep.put("palleteV2",rippleColor);
                                    peep.put("url",imageByte.getUrl());
                                    peep.put("image",imageByte);
                                    peep.put("typeV2",0);

                                    peep.save();

                                    createNotification("peep sent successfully"+ imageByte.getUrl(),"saved",100);
                                    Log.i(TAG, "onGenerated: color" + rippleColor);
                                    bitmap.recycle();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    createNotification("failed to send peep",e.getMessage(),100);

                                }
                            }else {
                                Log.e(TAG, "onGenerated: return null");
                            }
                        }
                    });


        }else {
            Log.e(TAG, "saveScaledPhoto: bitmap return null" );
        }





    }

    private void sendSuccessNotification() {
    }

    private void sendFailureNotification() {

    }

    public void createNotification(String title,String message,int nId) {

        Context ctx = getApplicationContext();
//        Intent intent = new Intent(ctx, DeadLineService.class);
//        PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {

            Notification noti = null;
            noti = new Notification.Builder(ctx)
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

            NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            // hide the notification after its selected
            noti.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(nId, noti);
        }
    }
}

package com.example.madiba.venu_alpha.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.util.Log;

import com.applozic.mobicomkit.api.people.ChannelInfo;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.ChannelMetadata;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import com.example.madiba.venu_alpha.Actions.ActionGossip;
import com.example.madiba.venu_alpha.utils.ImageUitls;
import com.example.madiba.venu_alpha.utils.NotificationUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UploadService extends IntentService {

    private static final String TAG= UploadService.class.getSimpleName();

    private static final String ACTION_EVENT = "com.example.madiba.venu_v3.servicesv2.action.EVENT";
    private static final String ACTION_VIDEO = "com.example.madiba.venu_v3.servicesv2.action.VIDEO";
    private static final String ACTION_IMAGE = "com.example.madiba.venu_v3.servicesv2.action.IMAGE";
    private static final String ACTION_GOSSIP = "com.example.madiba.venu_v3.servicesv2.action.GOSSIP";

    private static final String EXTRA_ID = "com.example.madiba.venu_v3.servicesv2.extra.ID";
    private static final String EXTRA_CLASSNAME = "com.example.madiba.venu_v3.servicesv2.extra.CLASSNAME";
    private static final String EXTRA_URL = "com.example.madiba.venu_v3.servicesv2.extra.URL";
    private static final String EXTRA_TITLE = "com.example.madiba.venu_v3.servicesv2.extra.TITLE";
    private static final String EXTRA_ISVIDEO = "com.example.madiba.venu_v3.servicesv2.extra.ISVIDEO";

    public UploadService() {
        super("UploadService");
    }

    public static void startActionEvent(Context context, String id, String className,String url,Boolean isVideo) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_EVENT);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_CLASSNAME, className);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_ISVIDEO, isVideo);
        context.startService(intent);
    }


    public static void startActionVideo(Context context, String id, String className,String url) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_VIDEO);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_CLASSNAME, className);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }
    public static void startActionImage(Context context, String id, String className,String url) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_VIDEO);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_CLASSNAME, className);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }

    public static void startActionGossip(Context context, String title) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_VIDEO);
        intent.putExtra(EXTRA_TITLE, title);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_EVENT.equals(action)) {
                final String id = intent.getStringExtra(EXTRA_ID);
                final String className = intent.getStringExtra(EXTRA_CLASSNAME);
                final String url = intent.getStringExtra(EXTRA_URL);
                final Boolean isVideo = intent.getBooleanExtra(EXTRA_ISVIDEO,false);
                handleActionEvent(id, className,url,isVideo);
            } else if (ACTION_VIDEO.equals(action)) {
                final String id = intent.getStringExtra(EXTRA_ID);
                final String className = intent.getStringExtra(EXTRA_CLASSNAME);
                final String url = intent.getStringExtra(EXTRA_URL);
                handleActionVideo(id, className,url);
            }else if (ACTION_IMAGE.equals(action)) {
                final String id = intent.getStringExtra(EXTRA_ID);
                final String className = intent.getStringExtra(EXTRA_CLASSNAME);
                final String url = intent.getStringExtra(EXTRA_URL);
                handleActionImage(id, className,url);
            }else if (ACTION_GOSSIP.equals(action)) {
                final String title = intent.getStringExtra(EXTRA_TITLE);
                handleActionGossip(title);
            }

        }
    }

    /**
     * Handle action Event in the provided background thread with the provided
     * parameters.
     */

    private void handleActionEvent(String id, String className, String uri,Boolean isVideo) {
        Log.e(TAG, "handleActionEventUploud: started  id: " + id + " image  :  " + uri + " classname : " + className);

        int vibrantColor = 0;
        int mutedtColor = 0;
        int textcolor = 0;
        if (isVideo) {

            Map config = new HashMap();

            config.put("cloud_name", "venu-video");
            config.put("api_key", "912582153739168");
            config.put("api_secret", "jZpLwLo_LsMm5ZVipbRrAnvPpfk");

            Cloudinary cloudinary = new Cloudinary(config);
            String url;

            File file = new File(uri);

            @SuppressWarnings("rawtypes")
            Map cloudinaryResult;
            if (file.exists()) {
                try {
                    cloudinaryResult = cloudinary.uploader().upload(file, ObjectUtils.asMap("resource_type", "video"));
                    url = cloudinaryResult.get("url").toString();

                    // create thumbnail
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(uri, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    thumb.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                    byte[] scaledData = bos.toByteArray();

                    // generate pallete
                    Palette palette = Palette.from(thumb)
                            .clearFilters()
                            .generate();

                    // try the named swatches in preference order
                    if (palette.getVibrantSwatch() != null) {
//                            rippleColor =
//                                    ColorUtils.modifyAlpha(palette.getVibrantSwatch().getRgb(), darkAlpha);

                    } else if (palette.getLightVibrantSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getLightVibrantSwatch().getRgb(),
//                                    lightAlpha);
                    } else if (palette.getDarkVibrantSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getDarkVibrantSwatch().getRgb(),
//                                    darkAlpha);
                    } else if (palette.getMutedSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getMutedSwatch().getRgb(), darkAlpha);
                    } else if (palette.getLightMutedSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getLightMutedSwatch().getRgb(),
//                                    lightAlpha);
                    } else if (palette.getDarkMutedSwatch() != null) {
//                        rippleColor =
//                                ColorUtils.modifyAlpha(palette.getDarkMutedSwatch().getRgb(), darkAlpha);
                    }

                    //save parsefile
                    ParseFile photoFile = new ParseFile(ParseUser.getCurrentUser().getUsername(), scaledData);
                    photoFile.save();

                    //save parse object
                    ParseObject event = ParseObject.createWithoutData(className, id);
                    event.fetchFromLocalDatastore();
                    event.put("palleteVibrant", vibrantColor);
                    event.put("palleteMuted", mutedtColor);
                    event.put("palleteText", textcolor);
                    event.put("videoUrl", url);
                    event.put("url", photoFile.getUrl());
                    event.put("thumbv2", photoFile);
                    event.put("typeV2", 1);
                    event.save();

                    ParseObject shareObject = new ParseObject("ShareVersion3");
                    shareObject.put("from", ParseUser.getCurrentUser());
                    shareObject.put("fromID", ParseUser.getCurrentUser().getObjectId());
                    shareObject.put("event", event);
                    shareObject.put("toId", event.getObjectId());
                    shareObject.put("type", 1);
                    shareObject.save();


                    thumb.recycle();
                } catch (RuntimeException | ParseException | IOException e) {
                    Log.e("upload", "Error uploading file");
                    ParseObject media = ParseObject.createWithoutData(className, id);
                    media.deleteEventually();

                } finally {

                }

            }

        } else{
            byte[] originalBytes;
            byte[] compressBytes;
            if (ImageUitls.exists(getApplicationContext(), uri)) {
                //get bitmap
                final Bitmap bitmap = BitmapFactory.decodeFile(uri);
                if (bitmap != null) {
                    // compress bytes
                    originalBytes = ImageUitls.bitmap2Bytes(bitmap, Bitmap.CompressFormat.JPEG, 100);
                    compressBytes = ImageUitls.bitmap2Bytes(bitmap, Bitmap.CompressFormat.JPEG, 20);
                    ParseFile origImgs = new ParseFile(ParseUser.getCurrentUser().getUsername(), originalBytes);
                    ParseFile cmprImgs = new ParseFile(ParseUser.getCurrentUser().getUsername(), compressBytes);

                    try {
                        cmprImgs.save();
                        origImgs.save();

                        Palette palette = Palette.from(bitmap)
                                .clearFilters()
                                .generate();

                        // try the named swatches in preference order
                        if (palette.getVibrantSwatch() != null) {
//                            rippleColor =
//                                    ColorUtils.modifyAlpha(palette.getVibrantSwatch().getRgb(), darkAlpha);

                        } else if (palette.getLightVibrantSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getLightVibrantSwatch().getRgb(),
//                                    lightAlpha);
                        } else if (palette.getDarkVibrantSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getDarkVibrantSwatch().getRgb(),
//                                    darkAlpha);
                        } else if (palette.getMutedSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getMutedSwatch().getRgb(), darkAlpha);
                        } else if (palette.getLightMutedSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getLightMutedSwatch().getRgb(),
//                                    lightAlpha);
                        } else if (palette.getDarkMutedSwatch() != null) {
//                        rippleColor =
//                                ColorUtils.modifyAlpha(palette.getDarkMutedSwatch().getRgb(), darkAlpha);
                        }


                        ParseObject event = ParseObject.createWithoutData(className, id);
                        event.fetchFromLocalDatastore();
                        event.put("palleteVibrant", vibrantColor);
                        event.put("palleteMuted", mutedtColor);
                        event.put("palleteText", textcolor);
                        event.put("url", origImgs.getUrl());
                        event.put("image", origImgs); // //
                        event.put("thumb", cmprImgs);
                        event.put("type", 2);
                        event.save();

                        ParseObject shareObject = new ParseObject("ShareVersion3");
                        shareObject.put("from", ParseUser.getCurrentUser());
                        shareObject.put("fromID", ParseUser.getCurrentUser().getObjectId());
                        shareObject.put("event", event);
                        shareObject.put("toId", event.getObjectId());
                        shareObject.put("type", 1);
                        shareObject.save();

                        Log.i(TAG, "handleActionEventUploud: saving here");
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e("upload", "Error uploading file");
                        ParseObject media = ParseObject.createWithoutData(className, id);
                        media.deleteEventually();

                    }
                } else {
                    Log.e(TAG, "handleActionEventUploud:  file not found");
                }

            }

        }
    }

        /**
         * Handle action Video in the provided background thread with the provided
         * parameters.
         */
    private void handleActionVideo( String id, String classname,String filepath) {
        Log.i(TAG, "handleActionVidUploud2: " + filepath + " " + id + " " + classname);
        Map config = new HashMap();

        config.put("cloud_name", "venu-video");
        config.put("api_key", "912582153739168");
        config.put("api_secret", "jZpLwLo_LsMm5ZVipbRrAnvPpfk");

        Cloudinary cloudinary = new Cloudinary(config);
        String url;
        int vibrantColor = 0;
        int mutedtColor = 0;
        int textcolor =0;
        File file = new File(filepath);

        @SuppressWarnings("rawtypes")
        Map cloudinaryResult;
        if (file.exists()) {
            try {
                cloudinaryResult = cloudinary.uploader().upload(file, ObjectUtils.asMap("resource_type", "video"));
                url = cloudinaryResult.get("url").toString();

                // create thumbnail
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filepath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                thumb.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                byte[] scaledData = bos.toByteArray();

                // generate pallete
                Palette palette = Palette.from(thumb)
                        .clearFilters()
                        .generate();

                // try the named swatches in preference order
                if (palette.getVibrantSwatch() != null) {
//                            rippleColor =
//                                    ColorUtils.modifyAlpha(palette.getVibrantSwatch().getRgb(), darkAlpha);

                } else if (palette.getLightVibrantSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getLightVibrantSwatch().getRgb(),
//                                    lightAlpha);
                } else if (palette.getDarkVibrantSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getDarkVibrantSwatch().getRgb(),
//                                    darkAlpha);
                } else if (palette.getMutedSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getMutedSwatch().getRgb(), darkAlpha);
                } else if (palette.getLightMutedSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getLightMutedSwatch().getRgb(),
//                                    lightAlpha);
                } else if (palette.getDarkMutedSwatch() != null) {
//                        rippleColor =
//                                ColorUtils.modifyAlpha(palette.getDarkMutedSwatch().getRgb(), darkAlpha);
                }

                //save parsefile
                ParseFile photoFile = new ParseFile(ParseUser.getCurrentUser().getUsername(), scaledData);
                photoFile.save();

                //save parse object
                ParseObject media = ParseObject.createWithoutData(classname, id);

                media.fetchFromLocalDatastore();
                media.put("videoUrl", url);
                media.put("palleteVibrant",vibrantColor);
                media.put("palleteMuted",mutedtColor);
                media.put("palleteText",textcolor);
                media.put("url",photoFile.getUrl());
                media.put("image",photoFile);
                media.put("thumbv2",photoFile);
                media.put("typeV2",1);

                thumb.recycle();
            } catch (RuntimeException |ParseException| IOException e) {
                Log.e("upload", "Error uploading file");
                ParseObject media = ParseObject.createWithoutData(classname, id);
                media.deleteEventually();

            } finally {

            }

        }
    }



    /**
     * Handle action Video in the provided background thread with the provided
     * parameters.
     */

    private void handleActionImage(String id, String className,String url) {

        // intial variable
        byte[] originalBytes;
        byte[] compressBytes;
        float darkAlpha = 0.25f;
        float lightAlpha =0.5f;
        int vibrantColor = 0;
        int mutedtColor = 0;
        int textcolor =0;

        if (ImageUitls.exists(getApplicationContext(),url)){
            //get bitmap
            final Bitmap bitmap = BitmapFactory.decodeFile(url);
            if (bitmap !=null){
                // compress bytes
                originalBytes = ImageUitls.bitmap2Bytes(bitmap, Bitmap.CompressFormat.JPEG,100);
                compressBytes = ImageUitls.bitmap2Bytes(bitmap, Bitmap.CompressFormat.JPEG,20);
                ParseFile origImgs = new ParseFile(ParseUser.getCurrentUser().getUsername(),originalBytes);
                ParseFile cmprImgs = new ParseFile(ParseUser.getCurrentUser().getUsername(),compressBytes);

                try {
                    cmprImgs.save();
                    origImgs.save();

                    Palette palette = Palette.from(bitmap)
                            .clearFilters()
                            .generate();

                    // try the named swatches in preference order
                    if (palette.getVibrantSwatch() != null) {
//                            rippleColor =
//                                    ColorUtils.modifyAlpha(palette.getVibrantSwatch().getRgb(), darkAlpha);

                    } else if (palette.getLightVibrantSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getLightVibrantSwatch().getRgb(),
//                                    lightAlpha);
                    } else if (palette.getDarkVibrantSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getDarkVibrantSwatch().getRgb(),
//                                    darkAlpha);
                    } else if (palette.getMutedSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getMutedSwatch().getRgb(), darkAlpha);
                    } else if (palette.getLightMutedSwatch() != null) {
//                            rippleColor = ColorUtils.modifyAlpha(palette.getLightMutedSwatch().getRgb(),
//                                    lightAlpha);
                    } else if (palette.getDarkMutedSwatch() != null) {
//                        rippleColor =
//                                ColorUtils.modifyAlpha(palette.getDarkMutedSwatch().getRgb(), darkAlpha);
                    }

                    ParseObject peep = ParseObject.createWithoutData(className, id);
                    peep.fetchFromLocalDatastore();
                    peep.put("palleteVibrant",vibrantColor);
                    peep.put("palleteMuted",mutedtColor);
                    peep.put("palleteText",textcolor);
                    peep.put("url",origImgs.getUrl());
                    peep.put("image",origImgs);
                    peep.put("thumbv2",cmprImgs);
                    peep.put("typeV2",0);
                    peep.save();

                    NotificationUtils.sendLocatNotification(getApplicationContext(),"Saved success","success",null);


                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    bitmap.recycle();

                }
            }

        }

    }


    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGossip(String title) {

        ChannelMetadata channelMetadata = new ChannelMetadata();
        channelMetadata.setCreateGroupMessage(ChannelMetadata.ADMIN_NAME + " created " + ChannelMetadata.GROUP_NAME);
        channelMetadata.setAddMemberMessage(ChannelMetadata.ADMIN_NAME + " added " + ChannelMetadata.USER_NAME);
        channelMetadata.setRemoveMemberMessage(ChannelMetadata.ADMIN_NAME + " removed " + ChannelMetadata.USER_NAME);
        channelMetadata.setGroupNameChangeMessage(ChannelMetadata.USER_NAME + " changed group name " + ChannelMetadata.GROUP_NAME);
        channelMetadata.setJoinMemberMessage(ChannelMetadata.USER_NAME + " joined");
        channelMetadata.setGroupLeftMessage(ChannelMetadata.USER_NAME + " left group " + ChannelMetadata.GROUP_NAME);
        channelMetadata.setGroupIconChangeMessage(ChannelMetadata.USER_NAME + " changed icon");
        channelMetadata.setDeletedGroupMessage(ChannelMetadata.ADMIN_NAME + " deleted group " + ChannelMetadata.GROUP_NAME);
        List<String> channelMembersList =  new ArrayList<String>();

        ChannelInfo channelInfo  = new ChannelInfo(title,channelMembersList);
        channelInfo.setType(Channel.GroupType.OPEN.getValue().intValue()); //group type
        channelInfo.setChannelMetadata(channelMetadata); //Optional option for setting group meta data

        Channel channel = ChannelService.getInstance(getApplicationContext()).createChannel(channelInfo); //Thread or Async task
        if(channel != null){
            Log.i(TAG,"channelKey"+channel.getKey());
            ParseObject gossip =new ParseObject("Gossip");
            gossip.put("title", title);
            gossip.put("shares",0);
            gossip.put("likes",0);
            gossip.put("from", ParseUser.getCurrentUser());
            gossip.put("fromId", ParseUser.getCurrentUser().getObjectId());
            gossip.put("chat_id",channel.getKey());
            try {
                gossip.save();
                EventBus.getDefault().post(new ActionGossip(false, channel.getKey()));

            } catch (ParseException e) {
                EventBus.getDefault().post(new ActionGossip(false, 0));

                e.printStackTrace();
            }

        }else {
        }
    }


}

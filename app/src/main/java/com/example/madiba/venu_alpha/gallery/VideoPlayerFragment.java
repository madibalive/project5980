package com.example.madiba.venu_alpha.gallery;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.madiba.venu_alpha.Application;
import com.example.madiba.venu_alpha.R;
import com.example.madiba.venu_alpha.comment.CommentActivityFragment;
import com.example.madiba.venu_alpha.models.GlobalConstants;
import com.parse.ParseObject;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.WINDOW_SERVICE;


public class VideoPlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener ,View.OnTouchListener,CacheListener {

    private VideoView videoView;
    private String url;
    private Uri uri;
    public static final String TAG = "VIDEOFRAGMENT";
    private GestureDetectorCompat detector;
    private AlphaAnimation showAnimation, hideAnimation;
    private RelativeLayout bottom_bar;
    private LinearLayout   top_bar;
    private ProgressBar pbLoading;
    private SeekBar sbProgress;
    private ImageButton ibPlay, ibBack, ibForward,close_btn;
    private boolean isControllerShown;
    private UIHandler mHandler;
    private TextView tvTime;
    private String totalTime;
    private int totalTimeMilli, maxVolume;
    private int lastPosition;
    private AudioManager audioManager;
    private ImageView cacheStatusImageView;
    private static final int MESSAGE_SHOW_UI = 0;
    private static final int MESSAGE_HIDE_UI = 1;
    private static final int MESSAGE_UPDATE_PROGRESS = 2;
    private static final int MESSAGE_STOP_UPDATE_PROGRESS = 3;
    private ProgressBar seekArcComplete;
    private int screenY;
    private int screenX;
    private float volumeInFloat;
    private boolean isOperating;
    private boolean isScrollingX;
    private boolean isScrollingY;
    private boolean isOffline = false;
    private long lastPlayingTime;
    private final long timeToShowLoading = 500;

    private TextView mHashtag,mLikes,mShares,mComments,mUsername,mImageUrl;
    private CircleImageView avatar;
    private ParseObject mCurrentObject;
    private String mAvatarUrl;
    private ImageView imageView;


    public VideoPlayerFragment() {
    }

    public static VideoPlayerFragment newInstance(String name,String avatarUrl, String tag,
                                                  String url,int liks,int comment,String
                                                          id,String className,boolean isLike) {

        VideoPlayerFragment fragment = new VideoPlayerFragment();

        Bundle arguments = new Bundle();
        arguments.putString(GlobalConstants.PHP_NAME, name );
        arguments.putString(GlobalConstants.PHP_AVATAR,avatarUrl);
        arguments.putString(GlobalConstants.PHP_HASHTAG, tag);
        arguments.putString(GlobalConstants.PHP_URL,url );
        arguments.putInt(GlobalConstants.PHP_LIKES,liks);
        arguments.putInt(GlobalConstants.PHP_COMMENTS,comment );
        arguments.putString(GlobalConstants.PASS_ID,id);
        arguments.putString(GlobalConstants.PASS_CLASSNAME, className);
        arguments.putBoolean("isLike", isLike );
//        arguments.putString("imageUrl",imageUrl);
        fragment.setArguments(arguments);

        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_video_player, container, false);
        seekArcComplete = (ProgressBar) view.findViewById(R.id.seekArcComplete);
        mUsername = (TextView) view.findViewById(R.id.md_name);
        mHashtag = (TextView) view.findViewById(R.id.md_hashtag);
        mComments = (TextView) view.findViewById(R.id.md_comment);
        mLikes = (TextView) view.findViewById(R.id.md_cnt);
        mShares = (TextView) view.findViewById(R.id.md_share);
        top_bar = (LinearLayout) view.findViewById(R.id.md_top_bar);
        bottom_bar = (RelativeLayout) view.findViewById(R.id.md_bottom_bar);
        videoView = (VideoView) view.findViewById(R.id.video_view);
        pbLoading = (ProgressBar) view.findViewById(R.id.videoProgress);
        close_btn= (ImageButton) view.findViewById(R.id.md_closeBtn);
        view.setOnTouchListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initData();
        initUI(view);
        initListener();
        initialIistener();
        createAnimation();
        initGestureListinner();
        parseIntent();
        checkCachedState();

        Glide.with(getActivity())
                .load("")
                .thumbnail(0.5f)
                .priority(Priority.LOW)
                .crossFade()
                .into(imageView);

        HttpProxyCacheServer proxy =Application.getProxy(getActivity().getApplicationContext());
        proxy.registerCacheListener(this, url);
        String proxyUrl = proxy.getProxyUrl(url);
        videoView.setVideoPath(proxyUrl);
        videoView.start();

    }

    private void checkCachedState() {
        HttpProxyCacheServer proxy =Application.getProxy(getActivity().getApplicationContext());
        boolean fullyCached = proxy.isCached(url);
        if (fullyCached) {
            seekArcComplete.setProgress(100);
        }
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        seekArcComplete.setProgress(percentsAvailable);

    }
    @Override
    public void onPause() {
        if (videoView != null) {
            videoView.stopPlayback();
        }
        super.onPause();

    }

    @Override
    public void onStop() {
        changeProgress(false);

        super.onStop();

    }

    @Override
    public boolean onTouch(View view, MotionEvent ev) {

        Log.e(TAG, "onTouch: " + ev);
        this.detector.onTouchEvent(ev);

        return true;
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        Log.d(TAG, "buffered:" + i);
        seekArcComplete.setProgress(i);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        changeProgress(false);
        if (seekArcComplete.getVisibility() == View.VISIBLE) {
            seekArcComplete.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        Toast.makeText(getActivity(), mediaPlayer.toString(), Toast.LENGTH_SHORT).show();

//        pbLoading.setVisibility(View.GONE);
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        totalTimeMilli = mediaPlayer.getDuration();
//        pbLoading.setVisibility(View.GONE);
//        seekArcComplete.set(videoView.getDuration());
        int milliseconds = videoView.getDuration();
        int seconds = (milliseconds / 1000) % 60;
        int minutes = milliseconds / (1000 * 60);
        int hours = milliseconds / (1000 * 60 * 60);
        totalTime =
                (hours < 10 ? "0"+hours : hours) + ":"
                        + (minutes < 10 ? "0"+minutes : minutes) + ":"
                        + (seconds < 10 ? "0"+seconds : seconds);
//        tvTime.setText("00:00:00 / "+totalTime);
        changeProgress(true);
    }

    private void parseIntent(){
        Bundle extras;
        // handle fragment arguments
        if(getArguments() != null){
            extras= getArguments();

        }else {
            extras = getActivity().getIntent().getExtras();
        }

        if (extras != null){
            mUsername.setText(extras.getString(GlobalConstants.PHP_NAME));
            mHashtag.setText(extras.getString(GlobalConstants.PHP_HASHTAG));
            mLikes.setText(String.valueOf(extras.getInt(GlobalConstants.PHP_LIKES,0)));
            mComments.setText(String.valueOf(extras.getInt(GlobalConstants.PHP_COMMENTS,0)));
            mShares.setText(String.valueOf(extras.getInt(GlobalConstants.PHP_SHARES,0)));
            mAvatarUrl = extras.getString(GlobalConstants.PHP_AVATAR);
            url = extras.getString(GlobalConstants.PHP_URL);
            mCurrentObject = ParseObject.createWithoutData(extras.getString(GlobalConstants.PASS_CLASSNAME),extras.getString(GlobalConstants.PASS_ID));

            uri = Uri.parse(url);

        }else {
        }


    }

    private void initData() {
        mHandler = new UIHandler();
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumeInFloat = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Display display = ((WindowManager)getActivity().getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        screenY = display.getHeight();
        screenX = display.getWidth();
    }

    private void initUI(View view) {
        isControllerShown = true;
        videoView.postDelayed(() -> mHandler.sendEmptyMessage(MESSAGE_HIDE_UI), 3000);

   }

    private void initialIistener (){

        close_btn.setOnClickListener(view -> {
            Toast.makeText(getActivity(),"close",Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });

        mShares.setOnClickListener(view -> {

        });

        mLikes.setOnClickListener(view -> {

        });


        mComments.setOnClickListener(view -> {
            mCurrentObject.pinInBackground("currentComment");
            DialogFragment newFragment = CommentActivityFragment.newInstance(mCurrentObject.getObjectId(),mCurrentObject.getClassName(),true);
            newFragment.show(getChildFragmentManager(), "comment");
        });

//        mLikeIc.setOnLikeListener(new OnLikeListener() {
//            @Override
//            public void liked(LikeButton likeButton) {
//                //increase likes
//                int number = Integer.valueOf(mLikes.getText().toString());
//                number = number+1;
//                mLikes.setText(String.valueOf(number));
//                //increase activity like
//
//                mCurrentObject.increment("likes");
//                mCurrentObject.saveEventually();
//                GeneralService.startActionGenericAction(getActivity(),true,mCurrentObject.getObjectId(),mCurrentObject.getClassName(),GlobalConstants.GENERIC_LIKE);
//            }
//
//            @Override
//            public void unLiked(LikeButton likeButton) {
//                int number = Integer.valueOf(mLikes.getText().toString());
//                number = number-1;
//                mLikes.setText(String.valueOf(number));
//
//                mCurrentObject.increment("likes",-1);
//                mCurrentObject.saveEventually();
//                GeneralService.startActionGenericAction(getActivity(),false,mCurrentObject.getObjectId(),mCurrentObject.getClassName(),GlobalConstants.GENERIC_LIKE);
//            }
//        });
//
//        mShareIc.setOnLikeListener(new OnLikeListener() {
//            @Override
//            public void liked(LikeButton likeButton) {
//
//                ParseObject shareObject= new ParseObject("Share");
//                shareObject.put("from", ParseUser.getCurrentUser());
//                shareObject.put("fromID",ParseUser.getCurrentUser().getObjectId());
//                shareObject.put("object",mCurrentObject);
//                shareObject.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if (e == null) {
//                            Toast.makeText(getActivity(),"Shared with your friends",Toast.LENGTH_LONG).show();
//
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void unLiked(LikeButton likeButton) {
////                GeneralService.startActionShare(getActivity().getApplicationContext(),mCurrentObject.getObjectId(),mCurrentObject.getClassName());
//            }
//        });



    }


    private void initListener() {
        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnCompletionListener(this);

//        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser && isOffline) {
//                    videoView.seekTo(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                if (!isControllerShown) {
//                    showUI();
//                }
//                if (hideCounter != null) {
//                    hideCounter.cancel();
//                }
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                if (!isOffline) {
//                    videoView.seekTo(seekBar.getProgress());
//                }
//                if (hideCounter != null) {
//                    hideCounter.start();
//                }
//            }
//        });
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll_v1:" + distanceX);
            Log.d(TAG, "onScroll_v2:" + distanceY);
            Log.d(TAG, "onScroll_v2_divide:" + distanceY/screenY);
            isOperating = true;
            if (Math.abs(distanceY) > Math.abs(distanceX)) {
                if (!isScrollingX) {
                    isScrollingY = true;
                    setVolume(distanceY / screenY);
                    return true;
                } else {
                    return false;
                }
            } else {
                if (!isScrollingY && isOffline) {
                    isScrollingX = true;
                    setProgress(distanceX / screenX);
                    if (!isControllerShown) {
                        showUI();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (isControllerShown) {
                hideUI();
            } else {
                showUI();
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (videoView.isPlaying()) {
                videoView.pause();
            } else {
                videoView.start();
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v_x, float v_y) {
            Log.d(TAG, "onFling_v1:" + v_x);
            Log.d(TAG, "onFling_v2:" + v_y);
            if (Math.abs(v_x) > Math.abs(v_y)) {//上下滑动
                if (v_x > 0) {
                    forward();
                } else {
                    backward();
                }
                return true;
            } else {//左右滑动
                return false;
            }
        }
    }

    private void initGestureListinner() {
        detector = new GestureDetectorCompat(getActivity(), new MyGestureListener());
    }

    private  class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_HIDE_UI:
                    hideUI();
                    break;
                case MESSAGE_SHOW_UI:
                    showUI();
                    break;
            }
        }
    }

    private void setProgress() {
        if (lastPosition == videoView.getCurrentPosition()) {
//            Log.d(TAG, "video has paused");
            if ((System.currentTimeMillis() - lastPlayingTime) > timeToShowLoading
                    && videoView.isPlaying()) {
//                pbLoading.setVisibility(View.VISIBLE);
            }
        } else {
//            Log.d(TAG, "video started to play");
//            pbLoading.setVisibility(View.GONE);
            lastPlayingTime = System.currentTimeMillis();
        }
        lastPosition = videoView.getCurrentPosition();
//        sbProgress.setProgress(videoView.getCurrentPosition());
        seekArcComplete.setProgress(videoView.getDuration() * videoView.getBufferPercentage() / 100);
        int milliseconds = videoView.getCurrentPosition();
        int seconds = (milliseconds / 1000) % 60;
        int minutes = milliseconds / (1000 * 60);
        int hours = milliseconds / (1000 * 60 * 60);
        String currentTime =
                (hours < 10 ? "0" + hours : hours) + ":"
                        + (minutes < 10 ? "0" + minutes : minutes) + ":"
                        + (seconds < 10 ? "0" + seconds : seconds);
//        tvTime.setText(currentTime + " / " + totalTime);
    }

    Handler progressHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_UPDATE_PROGRESS:
                    if (videoView != null) {
                        setProgress();
                        progressHandler.sendEmptyMessage(MESSAGE_UPDATE_PROGRESS);
                    }
                    break;
                case MESSAGE_STOP_UPDATE_PROGRESS:
                    break;
            }
        }
    };

    private CountDownTimer hideCounter  = new CountDownTimer(5000, 5000) {

        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            if (isControllerShown && ! isOperating) {
                hideUI();
            }
        }
    };

    private void backward() {
        if (videoView.canSeekBackward()) {
            videoView.pause();
            videoView.seekTo(videoView.getCurrentPosition() - 30000);
            videoView.start();
        }
    }

    private void forward() {
        if (videoView.canSeekForward()) {
            videoView.pause();
            videoView.seekTo(videoView.getCurrentPosition() + 10000);
            videoView.start();
        }
    }

    private void setProgress(float percent) {
        int position = (int)(videoView.getCurrentPosition() - totalTimeMilli * percent);
        if (position < 0) {
            position = 0;
        }else if (position > totalTimeMilli) {
            position = totalTimeMilli;
        }
        videoView.seekTo(position);
    }

    private void setVolume(float percent) {
        volumeInFloat = volumeInFloat + maxVolume * percent;
        if (volumeInFloat < 0) {
            volumeInFloat = 0;
        }else if (volumeInFloat > maxVolume) {
            volumeInFloat = maxVolume;
        }
        Log.d(TAG, "maxVolume:" + maxVolume);
        Log.d(TAG, "deltColume:" + (int)(maxVolume * percent));
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int)volumeInFloat, AudioManager.FLAG_SHOW_UI);
    }

    private void changeProgress(boolean isChangeProgress) {
        if (isChangeProgress) {
            progressHandler.sendEmptyMessage(MESSAGE_UPDATE_PROGRESS);
        } else {
            progressHandler.removeMessages(MESSAGE_UPDATE_PROGRESS);
        }
    }

    private  void showUI() {
        if (!isControllerShown) {
            bottom_bar.startAnimation(showAnimation);
            top_bar.startAnimation(showAnimation);
//            toggleHideyBar();


            isControllerShown = true;
        }
    }

    private  void hideUI() {
        if (isControllerShown) {
            isControllerShown = false;
            bottom_bar.startAnimation(hideAnimation);
            top_bar.startAnimation(hideAnimation);
//            toggleHideyBar();
            if (hideCounter != null) {
                hideCounter.cancel();
            }
        }
    }


    private void createAnimation() {
        showAnimation = new AlphaAnimation(0, 1);
        hideAnimation = new AlphaAnimation(1, 0);
        showAnimation.setDuration(500);
        hideAnimation.setDuration(500);
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bottom_bar.setVisibility(View.VISIBLE);
                top_bar.setVisibility(View.VISIBLE);
                close_btn.setVisibility(View.VISIBLE);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bottom_bar.setVisibility(View.INVISIBLE);
                top_bar.setVisibility(View.INVISIBLE);
                close_btn.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    @Override
    public void onDestroy() {
        videoView.stopPlayback();
        Application.getProxy(getActivity().getApplicationContext()).unregisterCacheListener(this);
        super.onDestroy();
    }

}

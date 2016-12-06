package com.example.madiba.venu_alpha.gallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.example.madiba.venu_alpha.R;
import com.example.madiba.venu_alpha.models.GlobalConstants;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.List;

public class GalleryPagerActivity extends AppCompatActivity {
    private static final String TAG ="PAGERACTIVITY";
    private GalleryPagerAdapter mAdapter;
    private ViewPager mPager;
    private List<ParseObject> mDatas;
    private  ParseQuery<ParseObject> query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 10) {
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;

            getWindow().getDecorView().setSystemUiVisibility(flags);
        } else {
            getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_gallery_pager);


        mPager = (ViewPager)findViewById(R.id.gallerypager_viewpager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override

            public void onPageSelected(int position) {

                if (position == mDatas.size() - 3){
                    Log.i(TAG, "onPageSelected: " +position + " - " + mDatas.size());
//                    Task.callInBackground(new Callable<List<ParseObject>>() {
//                        @Override
//                        public List<ParseObject> call() throws Exception {
//                            List<ParseObject> newData= new ArrayList<ParseObject>();
//                            ParseObject gossip = new ParseObject("Gossip");
//                            gossip.put("name", "gossip ");
//
//                            for (int i = 0; i < 3; i++) {
//                                newData.add(gossip);
//                            }
//                            return  newData;
//                        }
//                    }).continueWith(new Continuation<List<ParseObject>, Void>() {
//                        @Override
//                        public Void then(Task<List<ParseObject>> task) throws Exception {
//                            if (task.isFaulted())
//                                Log.i(TAG, "then: Fail");
//                            else {
//                                Log.i(TAG, "return from serivce :adding new data " + task.getResult().size());
//                                mDatas.addAll(task.getResult());
//                                mAdapter.update(task.getResult());
//
//                            }
//                            return null;
//                        }
//                    },Task.UI_THREAD_EXECUTOR);

                    
                    
                    
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        loadSample();


    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
        }
    };

    private void loadSample() {

        query = ParseQuery.getQuery("Peep");
        query.whereEqualTo("from", ParseUser.createWithoutData(ParseUser.class,getIntent().getStringExtra(GlobalConstants.INTENT_ID)));
        query.include("from");
        query.orderByDescending("createdAt");
        query.findInBackground((objects, e) -> {
            if (e == null){
                Log.i(TAG, "done: got data" + objects.size());
                mDatas = objects;
                mAdapter= new GalleryPagerAdapter(getSupportFragmentManager(),mDatas,1,getApplicationContext());
                mPager.setAdapter(mAdapter);

            }else {
                Log.e(TAG, "done: erro" + e.getMessage() );
            }
        });
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("custom-event-name"));
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}



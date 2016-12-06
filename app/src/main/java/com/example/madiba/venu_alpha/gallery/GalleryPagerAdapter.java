package com.example.madiba.venu_alpha.gallery;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.parse.ParseObject;

import java.util.List;


/**
 * Created by oracleen on 2016/7/4.
 */
public class GalleryPagerAdapter extends FragmentStatePagerAdapter {
    private List<ParseObject> mDatas;
    private int currentItem;
    private Context mContext;

    public GalleryPagerAdapter(FragmentManager fm, List<ParseObject> data, int pos,Context context) {
        super(fm);
        mDatas = data;
        currentItem = pos;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        ParseObject data = mDatas.get(position);

        if (data.getInt("typeV2")==0 ) {
            return PhotoViewerFragment.newInstance(data.getParseUser("from").getUsername(),data.getParseUser("from").getParseFile("avatar").getUrl()
                    ,data.getString("tag"),data.getString("url"),data.getInt("likes"),data.getInt("comments"),
                    data.getObjectId(),data.getClassName(),true);
        }else {
            return VideoPlayerFragment.newInstance(data.getParseUser("from").getUsername(),data.getParseUser("from").getParseFile("avatar").getUrl()
                    ,data.getString("tag"),data.getString("videoUrl"),data.getInt("likes"),data.getInt("comments"),
                    data.getObjectId(),data.getClassName(),true);        }

    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);

//        if (object instanceof VideoPlayerFragment || object instanceof PhotoViewerFragment){
//
//        }

//        MyFragment fragment = (MyFragment)object;
//        String title = fragment.getTitle();
//        int position = titles.indexOf(title);
//        if (position >= 0) {
//            return position;
//        } else {
//            return POSITION_NONE;
//        }

    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    public void update(List<ParseObject> xyzData) {
        this.mDatas.addAll(xyzData);
        notifyDataSetChanged();
    }

}


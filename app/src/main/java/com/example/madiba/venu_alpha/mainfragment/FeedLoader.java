package com.example.madiba.venu_alpha.mainfragment;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Madiba on 12/7/2016.
 */

public class FeedLoader {

    public static Observable<List<ParseObject>> Refresh(int skip){
        return Observable.create(new Observable.OnSubscribe<List<ParseObject>>() {
            @Override
            public void call(Subscriber<? super List<ParseObject>> subscriber) {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Activities");
                query.whereEqualTo("to", ParseUser.getCurrentUser());
                query.include("from");
                query.orderByAscending("Created");
                query.setLimit(20);
                query.setSkip(skip);

                Timber.d("connecting");
                try {
                    subscriber.onNext(query.find());
                    // save to local store
                    subscriber.onCompleted();


                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }





}

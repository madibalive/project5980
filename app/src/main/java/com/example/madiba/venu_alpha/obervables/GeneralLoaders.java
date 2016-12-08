package com.example.madiba.venu_alpha.obervables;

import android.text.TextUtils;

import com.example.madiba.venu_alpha.models.CategoriesModel;
import com.example.madiba.venu_alpha.models.DiscoverModel;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Madiba on 12/7/2016.
 */

public class GeneralLoaders {

    public static Observable<List<ParseObject>> loadOnTap(){
        return Observable.create(new Observable.OnSubscribe<List<ParseObject>>() {
            @Override
            public void call(Subscriber<? super List<ParseObject>> subscriber) {

                // Init query
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Peep");
                query = ParseQuery.getQuery("OnTapRequest");
                query.whereEqualTo("from", ParseUser.getCurrentUser());
                query.orderByAscending("createdAt");
                Timber.d("connecting");
                try {
                    subscriber.onNext(query.find());
                    // save to local store
                    subscriber.onCompleted();


                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }


    public static Observable<List<ParseObject>> loadUsersContacts(String id,int type){

        return Observable.create(new Observable.OnSubscribe<List<ParseObject>>() {
            @Override
            public void call(Subscriber<? super List<ParseObject>> subscriber) {

                String term;

                if (type == 0)
                    term = "to";
                else
                    term = "from";

                ParseQuery<ParseObject> query = ParseQuery.getQuery("FollowVersion3");
                query.whereExists("from");
                query.include("from");

                if (TextUtils.equals(id, ParseUser.getCurrentUser().getObjectId())) {
                    query.whereEqualTo(term, ParseUser.getCurrentUser());

                } else {
                    query.whereEqualTo(term, ParseUser.createWithoutData(ParseUser.class, id));
                }

                query.orderByAscending("createdAt");
                Timber.d("connecting");
                try {
                    subscriber.onNext(query.find());
                    subscriber.onCompleted();

                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }



    public static Observable<List<ParseObject>> loadNotifications(int skip){
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

    public static Observable<List<DiscoverModel>> loadDiscover(){
        return Observable.create(new Observable.OnSubscribe<List<DiscoverModel>>() {
            @Override
            public void call(Subscriber<? super List<DiscoverModel>> subscriber) {
                List<DiscoverModel> mDatas= new ArrayList<>();

                // add categories

                List<CategoriesModel> categoriesList = new ArrayList<>();

                CategoriesModel today = new CategoriesModel("Live \n Today");
                CategoriesModel weekend = new CategoriesModel("Weekend \n Picks");
                CategoriesModel top = new CategoriesModel("Top \nEvent");
                CategoriesModel entertain = new CategoriesModel("Entertain \nEvents");
                CategoriesModel gospel = new CategoriesModel("Gospol \nEvents");
                CategoriesModel social = new CategoriesModel("Social \nEvents");
                CategoriesModel fitness = new CategoriesModel("Fitness \nEvents");

                categoriesList.add(today);
                categoriesList.add(weekend);
                categoriesList.add(top);
                categoriesList.add(entertain);
                categoriesList.add(gospel);
                categoriesList.add(social);
                categoriesList.add(fitness);

                DiscoverModel a = new DiscoverModel();
                a.setMtitle(null);
                a.setType(DiscoverModel.CATEGORIES);
                a.setListCategories(categoriesList);
                mDatas.add(a);


                // creat all queries heres
                ParseQuery<ParseObject> queryEvents= ParseQuery.getQuery("Events");
                queryEvents.orderByAscending("createdAt");
                queryEvents.setLimit(20);

                ParseQuery<ParseUser> queryPeople = ParseUser.getQuery();
                queryPeople.orderByAscending("createdAt");
                queryPeople.setLimit(20);

                ParseQuery<ParseObject> queryMedia= ParseQuery.getQuery("Events");
                queryMedia.orderByAscending("createdAt");
                queryMedia.setLimit(20);

                ParseQuery<ParseObject> queryGossip= ParseQuery.getQuery("Gossip");
                queryGossip.orderByAscending("createdAt");
                queryGossip.setLimit(20);


                Timber.d("connecting");
                try {
                    // add return datas

                    DiscoverModel gossip = new DiscoverModel();
                    gossip.setType(DiscoverModel.TOP_GOSSIP);
                    gossip.setListObject(queryGossip.find());
                    gossip.setMtitle("Top Users");
                    mDatas.add(gossip);

                    DiscoverModel newPeople = new DiscoverModel();
                    newPeople.setType(DiscoverModel.New_PEOPLE);
                    newPeople.setListUsers(queryPeople.find());
                    newPeople.setMtitle("Top new  Users");
                    mDatas.add(newPeople);

                    DiscoverModel media = new DiscoverModel();
                    media.setType(DiscoverModel.DIS_EXPLORE);
                    media.setListObject(queryMedia.find());
                    media.setMtitle("New Peeps");

                    DiscoverModel newEvents = new DiscoverModel();
                    newEvents.setType(DiscoverModel.NEW_EVENT);
                    newEvents.setListObject(queryEvents.find());
                    newEvents.setMtitle("New Peeps");

                    mDatas.add(newPeople);
                    mDatas.add(media);
                    mDatas.add(newEvents);
                    mDatas.add(gossip);
                    subscriber.onNext(mDatas);
                    subscriber.onCompleted();

                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public static Observable<List<ParseObject>> loadComment(String id){
        return Observable.create(new Observable.OnSubscribe<List<ParseObject>>() {
            @Override
            public void call(Subscriber<? super List<ParseObject>> subscriber) {

                // Init query
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Peep");
                query = ParseQuery.getQuery("OnTapRequest");
                query.whereEqualTo("from", ParseUser.getCurrentUser());
                query.orderByAscending("createdAt");
                Timber.d("connecting");
                try {
                    subscriber.onNext(query.find());
                    // save to local store
                    subscriber.onCompleted();


                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}

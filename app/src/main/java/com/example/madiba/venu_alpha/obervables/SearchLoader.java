package com.example.madiba.venu_alpha.obervables;

import com.example.madiba.venu_alpha.models.SearchModel;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Madiba on 12/7/2016.
 */

public class SearchLoader implements Callable<List<SearchModel>> {
    private  String searchTerm;
    public SearchLoader(String searchTerm) {
        this.searchTerm=searchTerm;
    }

    @Override
    public List<SearchModel> call() throws Exception {
        List<SearchModel> mSearchDatas = new ArrayList<>();

        ParseQuery<ParseUser> userQuery= ParseUser.getQuery();
        userQuery.whereStartsWith("username", searchTerm);

        ParseQuery<ParseObject> GossipQuery= ParseQuery.getQuery("EventsTest");
        GossipQuery.whereStartsWith("title", searchTerm);
        GossipQuery.orderByAscending("shares");
        GossipQuery.addAscendingOrder("likes");

        ParseQuery<ParseObject> eventQuery= ParseQuery.getQuery("Events");
        eventQuery.whereStartsWith("title", searchTerm);
        eventQuery.orderByAscending("shares");
        eventQuery.addAscendingOrder("likes");


        SearchModel gossips = new SearchModel("Gossip", SearchModel.GOSSIP);
        gossips.setmData(GossipQuery.find());

        SearchModel peoples = new SearchModel("People", SearchModel.PEOPLE);
        peoples.setUsers(userQuery.find());

        SearchModel events = new SearchModel("Event", SearchModel.EVENT);
        events.setmData(eventQuery.find());

        mSearchDatas.add(gossips);
        mSearchDatas.add(peoples);
        mSearchDatas.add(events);

        return mSearchDatas;
    }
}

package com.example.madiba.venu_alpha.obervables;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.example.madiba.venu_alpha.models.ModelLoadLocal;
import com.example.madiba.venu_alpha.models.PhoneContact;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Madiba on 12/7/2016.
 */

public class LoaderLocalContact {

    public static Observable<List<ModelLoadLocal>> load(Context context){
        List<PhoneContact> alContacts=new ArrayList<>();
        ArrayList<String> phoneNumbers = new ArrayList<>();
        return Observable.create(new Observable.OnSubscribe<List<ModelLoadLocal>>() {
            @Override
            public void call(Subscriber<? super List<ModelLoadLocal>> subscriber) {
                getAll(context,alContacts,phoneNumbers);

                ParseQuery<ParseUser> syncQuery = ParseUser.getQuery();
                syncQuery.whereContainedIn("phone", phoneNumbers);

                try {
                    List<ParseUser> foundUsers = syncQuery.find();
                    //remove from following
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("FollowVersion3");
                    query.whereExists("from");
                    query.include("to");
                    query.whereEqualTo("from", ParseUser.getCurrentUser());
                    List<ParseObject> followingData = query.find();

                    for (int i = 0; i < phoneNumbers.size(); i++) {
                        for (int j = 0; j < foundUsers.size() ; j++) {
                            if (alContacts.get(i).getPhoneNumber().equals(foundUsers.get(j))){
                                alContacts.remove(i);
                            }
                        }
                    }



                    for (int i = 0; i <followingData.size() ; i++) {

                        for (int j = 0; j < foundUsers.size(); j++) {
                            if (followingData.get(i).getParseUser("to").getObjectId().equals(foundUsers.get(j).getObjectId())){
                                foundUsers.remove(j);
                            }
                        }

                    }

                    List<ModelLoadLocal> returnData= new ArrayList<ModelLoadLocal>();

                    for (ParseUser m : foundUsers) {
                        ModelLoadLocal n = new ModelLoadLocal();
                        n.setType(ModelLoadLocal.TYPE_PARSE);
                        n.setParseUser(m);
                    }
                    for (PhoneContact m : alContacts) {
                        ModelLoadLocal n = new ModelLoadLocal();
                        n.setType(ModelLoadLocal.TYPE_LOCAL);
                        n.setPhoneContact(m);
                    }


                    subscriber.onNext(returnData);
                    subscriber.onCompleted();

                } catch (ParseException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io());

    }

    public static Observable<List<ParseUser>> loadOnce(Context context){
        List<PhoneContact> alContacts=new ArrayList<>();
        ArrayList<String> phoneNumbers = new ArrayList<>();
        return Observable.create(new Observable.OnSubscribe<List<ParseUser>>() {
            @Override
            public void call(Subscriber<? super List<ParseUser>> subscriber) {
                getAll(context,alContacts,phoneNumbers);

                ParseQuery<ParseUser> syncQuery = ParseUser.getQuery();
                syncQuery.whereContainedIn("phone", phoneNumbers);

                try {
                    List<ParseUser> foundUsers = syncQuery.find();
                    //remove from following
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("FollowVersion3");
                    query.whereExists("from");
                    query.include("to");
                    query.whereEqualTo("from", ParseUser.getCurrentUser());
                    List<ParseObject> followingData = query.find();

                    subscriber.onNext(foundUsers);
                    subscriber.onCompleted();

                } catch (ParseException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io());

    }

    private static void getAll(Context context, List<PhoneContact> alContacts,ArrayList<String> phoneNumbers) {

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
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
        }


    }

}

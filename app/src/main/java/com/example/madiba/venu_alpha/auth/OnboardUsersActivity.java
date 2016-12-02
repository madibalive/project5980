package com.example.madiba.venu_alpha.auth;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.liuzhuang.rcimageview.RoundCornerImageView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.example.madiba.venu_alpha.R;
import com.example.madiba.venu_alpha.models.GlobalConstants;
import com.example.madiba.venu_alpha.models.PhoneContact;
import com.example.madiba.venu_alpha.utils.DividerItemDecoration;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class OnboardUsersActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String CONTACT_ID = ContactsContract.Contacts._ID;
    private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

    private static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private static final int RC_LOCATION_CONTACTS_PERM = 124;

    private static final int CAPTURE_LOCAL_PHOTO_ACTIVITY_REQUEST_CODE = 300;

    private static final String TAG = "UserContactList";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final int RC_SETTINGS_SCREEN = 125;
    private static final int RC_CAMERA_PERM = 123;
    private RoundCornerImageView avatar;
    private ProgressDialog progress;




    Button mSkip;
    RecyclerView mContactList;
    ProgressBar mProgress;
    List<PhoneContact> alContacts;
    ArrayList<String> phoneNumbers;
    contactAdapter mAdapter;
    List<ParseUser> mDatas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard_users);

        mSkip = (Button) findViewById(R.id.contact_continue);
        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(OnboardUsersActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        mContactList = (RecyclerView) findViewById(R.id.onboard_recycler);
        mContactList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new contactAdapter(R.layout.user_acnt_layout_small, mDatas);
        mContactList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        mContactList.setAdapter(mAdapter);
        mProgress.setVisibility(View.VISIBLE);
        avatar = (RoundCornerImageView) findViewById(R.id.onboard_avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProfilePicture();
            }
        });

        checkPermissionV2();
    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    private void changeProfilePicture(){

        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pickIntent.setType("image/*");
            Intent chooserIntent = Intent.createChooser(pickIntent, "Pick Picture");
            startActivityForResult(chooserIntent, CAPTURE_LOCAL_PHOTO_ACTIVITY_REQUEST_CODE);
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this,  getString(R.string.rationale_camera),
                    RC_CAMERA_PERM, perms);
        }
   }



    public void getAll(Context context) {

        alContacts = new ArrayList<>();
        phoneNumbers = new ArrayList<>();
        Log.i(TAG, "getAll: started getall");
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
            syncContacts();
        } else {
            syncContacts();
        }


    }


    private  void syncContacts() {
        ParseQuery<ParseUser> syncQuery = ParseUser.getQuery();
        syncQuery.whereContainedIn("phone", phoneNumbers);
        syncQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                progress.dismiss();

                if (e == null && objects.size() > 0) {

                    mAdapter.setNewData(objects);
                    mProgress.setVisibility(View.GONE);
                    mAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view, int i) {
                            ParseObject follow = new ParseObject("FollowVersion3");
                            follow.put("from", ParseUser.getCurrentUser());
                            follow.put("to", mAdapter.getItem(i));
                            follow.put("fromId", ParseUser.getCurrentUser().getObjectId());
                            follow.put("toId", mAdapter.getItem(i).getObjectId());
                            follow.put("type", GlobalConstants.TYPE_FOLLOW);
                            follow.saveInBackground();
                            mAdapter.remove(i);
                            mAdapter.notifyItemRemoved(i);
                        }
                    });
                } else {
                    Toast.makeText(OnboardUsersActivity.this,"Import return no user ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private class contactAdapter extends BaseQuickAdapter<ParseUser> {

        contactAdapter(int layoutResId, List<ParseUser> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder Holder, ParseUser phoneContact) {

            Holder.setText(R.id.user_act_username, phoneContact.getUsername());
            if (phoneContact.getString("url") != null)
                Glide.with(mContext).load(phoneContact.getString("url")).crossFade().fitCenter().into((ImageView) Holder.getView(R.id.user_act_avatar));
        }
    }


    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    public void checkPermissionV2() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)) {

                progress = ProgressDialog.show(OnboardUsersActivity.this, null,
                        "Importing Contacts", true);

                getAll(getApplicationContext());
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.rationale_location_contacts),
                        RC_LOCATION_CONTACTS_PERM, Manifest.permission.READ_CONTACTS);
            }
        }else {
            getAll(getApplicationContext());

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }

    private void updateDp(final String mUrl){
        progress = ProgressDialog.show(OnboardUsersActivity.this, null,
                "setting Avatar", true);
        Toast.makeText(OnboardUsersActivity.this,"starting avatar search",Toast.LENGTH_SHORT).show();

        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {

                try{
                    Bitmap image = BitmapFactory.decodeFile(mUrl);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 80, bos);

                    byte[] scaledData = bos.toByteArray();

                    final ParseFile photoFile = new ParseFile("peepy.jpg", scaledData);

                    photoFile.save();


                    ParseUser.getCurrentUser().put("avatar",photoFile);
                    ParseUser.getCurrentUser().put("avatarUrl",photoFile.getUrl());
                    ParseUser.getCurrentUser().save();

                    return ParseUser.getCurrentUser().getString("avatarUrl");

                }catch (Exception e){
                    Log.i(TAG, "call: error" + e.getMessage());
                    return null;
                }
            }
        }).continueWith(new Continuation<String, Void>() {
            @Override
            public Void then(Task<String> task) throws Exception {
                progress.dismiss();

                if (task.isFaulted()){
                    Toast.makeText(OnboardUsersActivity.this,"Cannot Update image",Toast.LENGTH_SHORT).show();

                }else if (task.getResult() != null){

                    Uri uri = Uri.parse("https://raw.githubusercontent.com/facebook/fresco/gh-pages/static/logo.png");
//                    SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
//                    avatar.setImageURI(uri);

//                    Glide.with(OnboardUsersActivity.this)
//                            .load(task.getResult())
//                            .thumbnail(0.1f)
//                            .priority(Priority.NORMAL)
//                            .crossFade()
//                            .centerCrop()
//                            .into(avatar);
//                }else {
                    Toast.makeText(OnboardUsersActivity.this,"Cannot Update image",Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        },Task.UI_THREAD_EXECUTOR);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SETTINGS_SCREEN) {
            // Do something after user returned from app settings screen, like showing a Toast.

        }
        if (requestCode == CAPTURE_LOCAL_PHOTO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(
                            data.getData(), filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        Uri fileUri = Uri.parse(filePath);

                        cursor.close();
                        Log.d(TAG, filePath);
                        Log.i(TAG, data.getData().toString());
                        Toast.makeText(OnboardUsersActivity.this,"got image " + filePath,Toast.LENGTH_SHORT).show();

                        updateDp(filePath);

                    }else {
                        Log.i(TAG, "onActivityResult: curor is null");
                    }

                } else if (resultCode == RESULT_CANCELED) {
                    Log.i("MainActivity", "onActivityResult: failed to get image");

                } else {
                }
            }
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}

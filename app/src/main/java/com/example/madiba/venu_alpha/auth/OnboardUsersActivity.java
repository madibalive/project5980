package com.example.madiba.venu_alpha.auth;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.liuzhuang.rcimageview.RoundCornerImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.example.madiba.venu_alpha.R;
import com.example.madiba.venu_alpha.models.GlobalConstants;
import com.example.madiba.venu_alpha.models.ModelLoadLocal;
import com.example.madiba.venu_alpha.models.PhoneContact;
import com.example.madiba.venu_alpha.obervables.LoaderLocalContact;
import com.example.madiba.venu_alpha.ui.DividerItemDecoration;
import com.example.madiba.venu_alpha.utils.ImageUitls;
import com.example.madiba.venu_alpha.utils.NetUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class OnboardUsersActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "UserContactList";
    private static final int RC_LOCATION_CONTACTS_PERM = 124;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 300;
    private List<PhoneContact> alContacts;
    private ArrayList<String> phoneNumbers;

    private static final int RC_SETTINGS_SCREEN = 125;
    private static final int RC_CAMERA_PERM = 123;
    private RoundCornerImageView avatar;
    private ProgressDialog progress;
    private Button mSkip;
    private RecyclerView mContactList;
    private contactAdapter mAdapter;
    private List<ParseUser> mDatas;

    RxLoaderManager loaderManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard_users);
        loaderManager =  RxLoaderManagerCompat.get(this);

        initViews();
        initAdapter();
        initListener();
        if (NetUtils.hasInternetConnection(getApplicationContext()))
            checkPermissionV2();
    }

    void initViews(){
        mSkip = (Button) findViewById(R.id.contact_continue);
        mContactList = (RecyclerView) findViewById(R.id.onboard_recycler);
        avatar = (RoundCornerImageView) findViewById(R.id.onboard_avatar);

    }
    void initAdapter(){
        mDatas= new ArrayList<>();
        mAdapter = new contactAdapter(R.layout.user_acnt_layout_small, mDatas);
        mContactList.setLayoutManager(new LinearLayoutManager(this));
        mContactList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        mContactList.setAdapter(mAdapter);

    }

    void initListener(){
        mSkip.setOnClickListener(view -> {
//                startActivity(new Intent(OnboardUsersActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProfilePicture();
            }
        });

    }



    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    public void checkPermissionV2() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
                progress = ProgressDialog.show(OnboardUsersActivity.this, null,
                        "Importing Contacts", true);

                initload();
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.permission_contact),
                        RC_LOCATION_CONTACTS_PERM, Manifest.permission.READ_CONTACTS);
            }
        }else {
            initload();
        }

    }
    void initload(){

       loaderManager.create(
                LoaderLocalContact.loadOnce(getApplicationContext()),
                new RxLoaderObserver<List<ParseUser>>() {
                    @Override
                    public void onNext(List<ParseUser> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            mAdapter.setNewData(value);
                            mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {
                                ParseObject follow = new ParseObject("FollowVersion3");
                                follow.put("from", ParseUser.getCurrentUser());
                                follow.put("to", mAdapter.getItem(i));
                                follow.put("fromId", ParseUser.getCurrentUser().getObjectId());
                                follow.put("toId", mAdapter.getItem(i).getObjectId());
                                follow.put("type", GlobalConstants.TYPE_FOLLOW);
                                follow.saveInBackground();
                                mAdapter.remove(i);
                                mAdapter.notifyItemRemoved(i);
                            });
                        },500);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                }

        ).start();
    }

    void reload(){

        final RxLoader<List<ParseUser>> myLoader =loaderManager.create(
                LoaderLocalContact.loadOnce(getApplicationContext()),
                new RxLoaderObserver<List<ParseUser>>() {
                    @Override
                    public void onNext(List<ParseUser> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            mAdapter.setNewData(value);
                            mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {
                                ParseObject follow = new ParseObject("FollowVersion3");
                                follow.put("from", ParseUser.getCurrentUser());
                                follow.put("to", mAdapter.getItem(i));
                                follow.put("fromId", ParseUser.getCurrentUser().getObjectId());
                                follow.put("toId", mAdapter.getItem(i).getObjectId());
                                follow.put("type", GlobalConstants.TYPE_FOLLOW);
                                follow.saveInBackground();
                                mAdapter.remove(i);
                                mAdapter.notifyItemRemoved(i);
                            });
                        },500);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                }

        );

        myLoader.restart();
    }



    private class contactAdapter extends BaseQuickAdapter<ParseUser> {

        contactAdapter(int layoutResId, List<ParseUser> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder Holder, ParseUser phoneContact) {

            Holder.setText(R.id.user_act_username, phoneContact.getUsername());
            if (phoneContact.getString("url") != null){
                Glide.with(mContext)
                        .load(phoneContact.getString("url"))
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.placeholder_error_media)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .thumbnail(0.4f)
                        .fallback(R.drawable.ic_default_avatar)
                        .into((RoundCornerImageView) Holder.getView(R.id.user_act_avatar));
            }
        }
    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    private void changeProfilePicture(){
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            chooser();
        } else {
            EasyPermissions.requestPermissions(this,  getString(R.string.rationale_camera),
                    RC_CAMERA_PERM, perms);
        }
    }
    private void chooser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"},
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent galleryIntent = new Intent();
                            galleryIntent.setType("image/*");
                            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(galleryIntent, "Select File"), GALLERY_REQUEST_CODE);
                            break;
                        case 1:
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                            break;
                        default:
                            break;
                    }
                }).show();
    }

    private void updateDp(final String mUrl){
        progress = ProgressDialog.show(OnboardUsersActivity.this, null,
                "setting Avatar", true);
        Task.callInBackground(() -> {
            try{
                Bitmap image = BitmapFactory.decodeFile(mUrl);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 80, bos);

                byte[] largeAvatarBytes = ImageUitls.bitmap2Bytes(image, Bitmap.CompressFormat.JPEG,80);
                byte[] smallAvatarBytes = ImageUitls.bitmap2Bytes(image, Bitmap.CompressFormat.JPEG,40);
                final ParseFile largeAvatar = new ParseFile("peepy.jpg", largeAvatarBytes);
                final ParseFile smallAvatar = new ParseFile("peepy.jpg", smallAvatarBytes);
                largeAvatar.save();
                smallAvatar.save();
                ParseUser.getCurrentUser().put("avatar",largeAvatar);
                ParseUser.getCurrentUser().put("avatarSmall",smallAvatar);
                ParseUser.getCurrentUser().save();
                return ParseUser.getCurrentUser().getParseFile("avatar").getUrl();

            }catch (Exception e){
                Timber.e("error uploading user avatar %s",e.toString());
                return null;
            }
        }).continueWith(new Continuation<String, Void>() {
            @Override
            public Void then(Task<String> task) throws Exception {
                progress.dismiss();

                if (task.isFaulted()){
                    Toast.makeText(OnboardUsersActivity.this,"Cannot Update image",Toast.LENGTH_SHORT).show();

                }else if (task.getResult() != null){
//                    Uri uri = Uri.parse("https://raw.githubusercontent.com/facebook/fresco/gh-pages/static/logo.png");
//                    SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
//                    avatar.setImageURI(uri);

                    Glide.with(OnboardUsersActivity.this)
                            .load(task.getResult())
                            .thumbnail(0.1f)
                            .priority(Priority.NORMAL)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .crossFade()
                            .centerCrop()
                            .into(avatar);
                }else {
                    Toast.makeText(OnboardUsersActivity.this,"Cannot Update image",Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        },Task.UI_THREAD_EXECUTOR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    try {
                        updateDp(ImageUitls.getPath(data.getData(),getApplicationContext()));
                    }catch (Exception e){
                        Timber.e("erorr getting path for image %s",e.getMessage());
                    }
                } else {

                }
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    try {
                        updateDp(ImageUitls.getPath(data.getData(),getApplicationContext()));
                    }catch (Exception e){
                        Timber.e("erorr getting path for image %s",e.getMessage());
                    }
                } else {
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}

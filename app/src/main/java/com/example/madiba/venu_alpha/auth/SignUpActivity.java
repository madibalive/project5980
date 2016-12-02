package com.example.madiba.venu_alpha.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.example.madiba.venu_alpha.R;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    public static final List<String> mPermissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};
    private static final String TAG = "signup";
    Button mBtnFb;
    Button gotoLogin;
    private ProgressDialog progress;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPhoneView;
    private EditText mUsername;
    private View mProgressView;
    private View mLoginFormView;
    private MobiComUserPreference mobiComUserPreference;
    private UserLoginTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.signup_email);
        mPhoneView = (EditText) findViewById(R.id.signup_phone);
        mUsername = (EditText) findViewById(R.id.signup_username);
        gotoLogin = (Button) findViewById(R.id.signup_goto_login);

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
        mPasswordView = (EditText) findViewById(R.id.signup_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.sign_up || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.signup_email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.signup_sign_up_form);
        mProgressView = findViewById(R.id.signup_sign_up_progress);

//        mBtnFb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                ParseFacebookUtils.logInWithReadPermissionsInBackground(MainActivity.this, mPermissions, new LogInCallback() {
//                    @Override
//                    public void done(ParseUser user, ParseException err) {
//
//                        if (user == null) {
//                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
//                        } else if (user.isNew()) {
//                            Log.d("MyApp", "User signed up and logged in through Facebook!");
//                            getUserDetailsFromFB();
//                        } else {
//                            Log.d("MyApp", "User logged in through Facebook!");
//                        }
//                    }
//                });
//
//            }
//        });
    }

    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPhoneView.setError(null);
        mUsername.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String phone = mPhoneView.getText().toString();
        String username = mUsername.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(username)) {
            mUsername.setError("Username is empty");
            focusView = mUsername;
            cancel = true;
        } else if (!isUserNameValid(username)) {
            mUsername.setError("min lenght is 5");
            focusView = mUsername;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Email is empty");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Password is empty");
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError("Min lenght is 8 ");
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError("Phone Contact is empty");
            focusView = mPhoneView;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            mPhoneView.setError("Must be more 10 number");
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
//            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progress = ProgressDialog.show(SignUpActivity.this, null,
                    "setting Avatar", true);
            userSignUp(username, email, password, phone);
        }


    }


    private void userSignUp(String username, String email, String password, String phone) {

        Log.i(TAG, "UserLoginTask: " + username + " : " + email + " :" + password + " : " + phone + " :");

        final ParseUser user = new ParseUser();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.put("phone", phone);

        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    progress.dismiss();
                    Log.i(TAG, "done: " + e.getMessage());
                    // Show the error message
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Log.i(TAG, "done: " + user.getObjectId());
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("user", ParseUser.getCurrentUser());
                    installation.put("user_id", ParseUser.getCurrentUser().getObjectId());
                    installation.saveInBackground();

                    ParseObject userRelations = new ParseObject("UserRelations");
                    userRelations.put("user", ParseUser.getCurrentUser());
                    userRelations.saveInBackground();

                    mobiComUserPreference = MobiComUserPreference.getInstance(getApplicationContext());
                    UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {
                        @Override
                        public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                            ApplozicSetting.getInstance(context).setSentMessageBackgroundColor(R.color.venu_flat_color); // accepts the R.color.name
                            ApplozicSetting.getInstance(context).setReceivedMessageBackgroundColor(R.color.venu_orange); // accepts the R.color.name
                            ApplozicSetting.getInstance(context).disableLocationSharingViaMap();
                            PushNotificationTask.TaskListener pushNotificationTaskListener = new PushNotificationTask.TaskListener() {
                                @Override
                                public void onSuccess(RegistrationResponse registrationResponse) {

                                }

                                @Override
                                public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

                                }
                            };
                            PushNotificationTask pushNotificationTask = new PushNotificationTask(Applozic.getInstance(context).getDeviceRegistrationId(), pushNotificationTaskListener, context);
                            pushNotificationTask.execute((Void) null);
                            progress.dismiss();
                            startActivity(new Intent(SignUpActivity.this, OnboardUsersActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();

                        }

                        @Override
                        public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                            //If any failure in registration the callback  will come here

                            mAuthTask = null;
                            progress.dismiss();

                            AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                            alertDialog.setTitle("");
                            alertDialog.setMessage(exception.toString());
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(android.R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            if (!isFinishing()) {
                                alertDialog.show();
                            }
                        }
                    };

                    User applozicUser = new User();
                    applozicUser.setUserId(ParseUser.getCurrentUser().getObjectId()); //applozicUserId it can be any unique applozicUser identifier
                    applozicUser.setDisplayName(ParseUser.getCurrentUser().getUsername()); //displayName is the name of the applozicUser which will be shown in chat messages
                    applozicUser.setEmail(ParseUser.getCurrentUser().getEmail()); //optional
                    applozicUser.setImageLink("");//optional,pass your image link


                    mAuthTask = new UserLoginTask(applozicUser, listener, getApplicationContext());
                    mAuthTask.execute((Void) null);


                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

//    private void getUserDetailsFromFB() {
//
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id,email,name,picture");
//
//        GraphRequest request = GraphRequest.newMeRequest(
//                AccessToken.getCurrentAccessToken(),
//                new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        try {
//                            Log.d("Response", response.getRawResponse());
//                            String email = response.getJSONObject().getString("email");
//                            mEmailView.setText(email);
//                            String name = response.getJSONObject().getString("name");
//                            mUsername.setText(name);
//                            JSONObject picture = response.getJSONObject().getJSONObject("picture");
//                            JSONObject data = picture.getJSONObject("data");
//                            String pictureUrl = data.getString("url");
//
//                            new ProfilePhotoAsync(pictureUrl).execute();
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                    }
//                });
//
//        request.setParameters(parameters);
//        request.executeAsync();
//
//    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 7;
    }

    private boolean isPhoneValid(String number) {
        return number.length() > 9;
    }

    private boolean isUserNameValid(String username) {
        return username.length() > 4;
    }


}

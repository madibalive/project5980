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
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    public static final List<String> mPermissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};
    private static final String TAG = "LOGIN";
    Button mBtnFb;
    Button signUp;
    private ProgressDialog progress;
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private MobiComUserPreference mobiComUserPreference;
    private UserLoginTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (EditText) findViewById(R.id.login_username);
        signUp = (Button) findViewById(R.id.login_goto_signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        mPasswordView = (EditText) findViewById(R.id.login_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.login_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_login_form);
        mProgressView = findViewById(R.id.login_login_progress);

//        mBtnFb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showProgress(true);
//                ParseFacebookUtils.logInWithReadPermissionsInBackground(MainActivity.this, mPermissions, new LogInCallback() {
//                    @Override
//                    public void done(ParseUser user, ParseException err) {
//                        showProgress(false);
//                        if (user == null) {
//                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
//                        } else if (user.isNew()) {
//                            Log.d("MyApp", "User signed up and logged in through Facebook!");
//                        } else {
//                            Log.d("MyApp", "User logged in through Facebook!");
//                            Intent intent = new Intent(LoginActivity.this, LaunchActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
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

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Email is empty");
            focusView = mEmailView;
            cancel = true;
        }
//         if (!isEmailValid(email)) {
//            mEmailView.setError("Valid Email Required");
//            focusView = mEmailView;
//            cancel = true;
//        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Password is empty");
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError("min Lenght of 8 char");
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progress = ProgressDialog.show(LoginActivity.this, null,
                    "setting Avatar", true);
            userLogin(email, password);
        }


    }


    private void userLogin(String email, String password) {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    // Show the error message
                    progress.dismiss();
                    Log.i(TAG, "done: " + e.getMessage());
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {

                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("user", ParseUser.getCurrentUser());
                    installation.put("user_id", ParseUser.getCurrentUser().getObjectId());
                    installation.saveInBackground();


                    mobiComUserPreference = MobiComUserPreference.getInstance(getApplicationContext());
                    UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {
                        @Override
                        public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                            ApplozicSetting.getInstance(context).setSentMessageBackgroundColor(R.color.venu_green); // accepts the R.color.name
                            ApplozicSetting.getInstance(context).setReceivedMessageBackgroundColor(R.color.venu_flat_color); // accepts the R.color.name
                            ApplozicSetting.getInstance(context).setSentMessageBorderColor(R.color.venu_flat_color); // accepts the R.color.name
                            ApplozicSetting.getInstance(context).setReceivedMessageBorderColor(R.color.venu_flat_color); // accepts the R.color.name
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


                        }

                        @Override
                        public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                            //If any failure in registration the callback  will come here

                            mAuthTask = null;

                            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                            alertDialog.setTitle("");
                            alertDialog.setMessage(exception.toString());
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(android.R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            progress.dismiss();
                                            startActivity(new Intent(LoginActivity.this, OnboardUsersActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                            finish();
                                        }
                                    });
                            if (!isFinishing()) {
                                alertDialog.show();
                            }


                        }
                    };

                    User applozicUser = new User();
                    applozicUser.setUserId(user.getObjectId()); //applozicUserId it can be any unique applozicUser identifier
                    applozicUser.setDisplayName(user.getUsername()); //displayName is the name of the applozicUser which will be shown in chat messages
                    applozicUser.setEmail(user.getEmail()); //optional
                    applozicUser.setImageLink("");//optional,pass your image link


                    mAuthTask = new UserLoginTask(applozicUser, listener, getApplicationContext());
                    mAuthTask.execute((Void) null);


                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        Log.i(TAG, "isPasswordValid: " + password.length());
        return password.length() > 4;
    }

}

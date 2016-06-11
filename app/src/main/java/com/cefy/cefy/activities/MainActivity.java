package com.cefy.cefy.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cefy.cefy.Constants;
import com.cefy.cefy.R;
import com.cefy.cefy.models.FBFriends;
import com.cefy.cefy.models.FBUser;
import com.cefy.cefy.models.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Anurag
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.checklist) LinearLayout checkList;
    @BindView(R.id.fb_container) LinearLayout fbContainer;
    @BindView(R.id.in_container) LinearLayout inContainer;
    @BindView(R.id.sms_container) LinearLayout smsContainer;
    @BindView(R.id.fb_image) ImageView fbImage;
    @BindView(R.id.in_image) ImageView inImage;
    @BindView(R.id.sms_image) ImageView smsImage;
    @BindView(R.id.fb_text) TextView fbText;
    @BindView(R.id.in_text) TextView inText;
    @BindView(R.id.sms_text) TextView smsText;
    @BindView(R.id.fb_check) ImageView fbCheck;
    @BindView(R.id.in_check) ImageView inCheck;
    @BindView(R.id.sms_check) ImageView smsCheck;
    @BindView(R.id.fb_cross) ImageView fbCross;
    @BindView(R.id.in_cross) ImageView inCross;
    @BindView(R.id.sms_cross) ImageView smsCross;

    CallbackManager callbackManager;
    Firebase firebase;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        setUpFirebase();
        setUpClickListeners();
        checkFacebookLoginStatus();
    }

    private void setUpFirebase() {
        User user = (User) getIntent().getSerializableExtra(Constants.General.USER_DATA);
        Firebase.setAndroidContext(this);
        if (null != user && StringUtils.isNotEmpty(user.email)) {
            firebase = new Firebase("https://dazzling-fire-8056.firebaseio.com/android/data/users/" + user.email.replace(".", "(dot)"));
        }
    }

    private void setUpClickListeners() {
        fbContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpFacebook();
            }
        });
    }

    private void checkFacebookLoginStatus() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            setCompleted(fbCheck);
        }
    }

    private void setUpFacebook() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d("fb:data", loginResult.toString());
                    if (loginResult.getAccessToken() != null) {
                        Set<String> deniedPermissions = loginResult.getRecentlyDeniedPermissions();
                        if (deniedPermissions.contains("user_friends") || deniedPermissions.contains("email")) {
                            fbContainer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("user_friends", "email"));
                                }
                            });
                            fbCheck.setVisibility(View.GONE);
                            fbCross.setVisibility(View.VISIBLE);
                            fbText.setText(getString(R.string.fb_error_text));
                        } else {
                            fbCheck.setVisibility(View.VISIBLE);
                            fbCross.setVisibility(View.GONE);
                            fbText.setText(getString(R.string.fb_text));
                            setCompleted(fbCheck);
                            getFacebookData(loginResult.getAccessToken());
                        }
                    }
                }

                @Override
                public void onCancel() {
                    Log.d("fb:data", "Cancelled");
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.d("fb:data", "Errored");
                }
            });
        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList(
                "public_profile","email","user_friends"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == callbackManager) {
            callbackManager = CallbackManager.Factory.create();
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setCompleted(ImageView icon) {
        ColorFilter filter = new PorterDuffColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
        icon.setColorFilter(filter);
    }

    private void getFacebookData(AccessToken accessToken) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        final Gson gson = new GsonBuilder().create();
        GraphRequest meRequest = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject jsonObject,
                            GraphResponse response) {
                        FBUser fbUser = gson.fromJson(jsonObject.toString(), FBUser.class);
                        saveToFirebase(Constants.General.USER_PROFILE, fbUser);
                    }
                });
        GraphRequest friendsRequest = GraphRequest.newMyFriendsRequest(
                accessToken,
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(
                            JSONArray jsonArray,
                            GraphResponse response) {
                        String resp = response.getRawResponse();
                        FBFriends fbFriends = gson.fromJson(resp, FBFriends.class);
                        saveToFirebase(Constants.General.USER_FRIENDS, fbFriends);
                        //Friends list wont work:
                        //http://stackoverflow.com/questions/23850807/get-all-user-friends-using-facebook-graph-api-android
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        meRequest.setParameters(parameters);
        GraphRequestBatch batch = new GraphRequestBatch(meRequest, friendsRequest);
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch graphRequests) {
                progressDialog.hide();
            }
        });
        batch.executeAsync();
    }

    private void saveToFirebase(String key, Object object) {
        if (null != firebase) {
            firebase.child(key).setValue(object);
        }
    }

    private boolean isLinkedinAppInstalled() {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo("com.linkedin.android", PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}

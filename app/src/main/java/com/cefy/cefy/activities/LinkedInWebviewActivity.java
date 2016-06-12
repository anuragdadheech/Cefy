package com.cefy.cefy.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cefy.cefy.Constants;
import com.cefy.cefy.R;
import com.cefy.cefy.Utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

/**
 * @author Anurag
 */
public class LinkedInWebviewActivity extends Activity {

    private static final String API_KEY = "75xtaugid39lq6";
    private static final String SECRET_KEY = "M1iCmx9l1Er9oKPw";
    private static final String STATE = "E6YCqeFmHvHkt";
    //Made up url that we will intercept when redirecting.
    private static final String REDIRECT_URI = "http://www.cefy.in/linkedin/redirect";

    private static final String AUTHORIZATION_URL = "https://www.linkedin.com/uas/oauth2/authorization";
    private static final String ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/accessToken";
    private static final String SECRET_KEY_PARAM = "client_secret";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE_VALUE ="code";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String STATE_PARAM = "state";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";

    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    private WebView webView;
    private ProgressDialog pd;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkedin_webview);
        webView = (WebView) findViewById(R.id.main_activity_web_view);
        webView.requestFocus(View.FOCUS_DOWN);
        pd = new ProgressDialog(this);
        pd.setMessage("Connecting to LinkedIn");
        pd.setTitle("Please wait..");
        pd.show();

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                if(pd != null && pd.isShowing()){
                    pd.hide();
                }
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
                if(authorizationUrl.startsWith(REDIRECT_URI)){
                    Log.i("Authorize", "");
                    Uri uri = Uri.parse(authorizationUrl);
                    String stateToken = uri.getQueryParameter(STATE_PARAM);
                    if(stateToken == null || !stateToken.equals(STATE)){
                        Log.e("Authorize", "State token doesn't match");
                        return true;
                    }

                    String authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE);
                    if(authorizationToken == null){
                        Log.i("Authorize", "The user doesn't allow authorization.");
                        return true;
                    }
                    Log.i("Authorize", "Auth token received: " + authorizationToken);

                    String accessTokenUrl = getAccessTokenUrl(authorizationToken);
                    new PostRequestAsyncTask().execute(accessTokenUrl);

                } else{
                    Log.i("Authorize","Redirecting to: " + authorizationUrl);
                    webView.loadUrl(authorizationUrl);
                }
                return true;
            }
        });

        String authUrl = getAuthorizationUrl();
        Log.i("Authorize","Loading Auth Url: " + authUrl);
        webView.loadUrl(authUrl);
    }

    /**
     * Method that generates the url for get the access token from the Service
     * @return Url
     */
    private static String getAccessTokenUrl(String authorizationToken){
        return ACCESS_TOKEN_URL
                +QUESTION_MARK
                +GRANT_TYPE_PARAM+EQUALS+GRANT_TYPE
                +AMPERSAND
                +RESPONSE_TYPE_VALUE+EQUALS+authorizationToken
                +AMPERSAND
                +CLIENT_ID_PARAM+EQUALS+API_KEY
                +AMPERSAND
                +REDIRECT_URI_PARAM+EQUALS+REDIRECT_URI
                +AMPERSAND
                +SECRET_KEY_PARAM+EQUALS+SECRET_KEY;
    }
    /**
     * Method that generates the url for get the authorization token from the Service
     * @return Url
     */
    private static String getAuthorizationUrl(){
        return AUTHORIZATION_URL
                +QUESTION_MARK+RESPONSE_TYPE_PARAM+EQUALS+RESPONSE_TYPE_VALUE
                +AMPERSAND+CLIENT_ID_PARAM+EQUALS+API_KEY
                +AMPERSAND+STATE_PARAM+EQUALS+STATE
                +AMPERSAND+REDIRECT_URI_PARAM+EQUALS+REDIRECT_URI;
    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute(){
            pd.setMessage("Authorizing");
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            if(urls.length>0){
                String url = urls[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpost = new HttpPost(url);
                try{
                    HttpResponse response = httpClient.execute(httpost);
                    if(response!=null){
                        if(response.getStatusLine().getStatusCode() == 200){
                            String result = EntityUtils.toString(response.getEntity());
                            JSONObject resultJson = new JSONObject(result);
                            int expiresIn = resultJson.has("expires_in") ? resultJson.getInt("expires_in") : 0;

                            String accessToken = resultJson.has("access_token") ? resultJson.getString("access_token") : null;
                            Log.e("Tokenm", ""+accessToken);
                            if(expiresIn > 0 && accessToken != null){
                                Log.i("Authorize", "This is the access Token: "+accessToken+". It will expires in "+expiresIn+" secs");

                                //Calculate date of expiration
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.SECOND, expiresIn);
                                long expireDate = calendar.getTimeInMillis();

                                LinkedInWebviewActivity.this.accessToken = accessToken;
                                Utils.saveSharedSetting(LinkedInWebviewActivity.this, Constants.SharedPrefs.LINKEDIN_EXPIRES, String.valueOf(expireDate));
                                Utils.saveSharedSetting(LinkedInWebviewActivity.this, Constants.SharedPrefs.LINKEDIN_ACCESS_TOKEN, accessToken);
                                return true;
                            }
                        }
                    }
                }catch(IOException e){
                    Log.e("Authorize","Error Http response "+e.getLocalizedMessage());
                }
                catch (ParseException|JSONException e) {
                    Log.e("Authorize","Error Parsing Http response "+e.getLocalizedMessage());
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status){
            if(pd != null && pd.isShowing()){
                pd.hide();
            }
            if(status){
                Intent intent = new Intent();
                if (StringUtils.isNotEmpty(accessToken)) {
                    intent.putExtra(Constants.IntentExtras.LINKEDIN_ACCESS_TOKEN, accessToken);
                    intent.putExtra(Constants.IntentExtras.LINKEDIN_STATUS, true);
                } else {
                    intent.putExtra(Constants.IntentExtras.LINKEDIN_STATUS, false);
                }
                setResult(Constants.IntentExtras.RESULT_LINKEDIN_LOGIN, intent);
                finish();
            }
        }

    };
}

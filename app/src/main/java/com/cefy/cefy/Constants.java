package com.cefy.cefy;

/**
 * @author Anurag
 */
public class Constants {
    public interface General {
        String USER_DATA = "user_data";
        String FB_USER_PROFILE = "fb_user_profile";
        String FB_USER_FRIENDS = "fb_user_friends";
        String LINKEDIN_DATA = "linkedin_data";
        String SMS_DATA = "sms_data";
    }

    public interface SharedPrefs {
        String MY_PREFS_NAME = "com.cefy.android";
        String LINKEDIN_EXPIRES = "linkedin_expires";
        String LINKEDIN_ACCESS_TOKEN = "linkedin_token";
    }

    public interface IntentExtras {
        String LINKEDIN_ACCESS_TOKEN = "linkedin_access_token";
        String LINKEDIN_STATUS = "linkedin_status";
        int REQUEST_LINKEDIN_LOGIN = 100;
        int RESULT_LINKEDIN_LOGIN = 101;
        int REQUEST_READ_SMS = 120;
    }
}

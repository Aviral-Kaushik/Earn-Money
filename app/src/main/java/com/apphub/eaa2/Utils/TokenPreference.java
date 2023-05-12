package com.apphub.eaa2.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenPreference {

    private static final String SHARED_PREF_NAME = "TokenPreference";
    private static final String TAG_TOKEN = "tag_token";

    private static TokenPreference mInstance = null;

    private static Context mCtx;

    private TokenPreference(Context context){
        mCtx = context;
    }

    public static synchronized TokenPreference getInstance(Context context){
        if (mInstance == null){
            mInstance = new TokenPreference(context);
        }
        return mInstance;
    }
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_TOKEN, "-");
    }

}

package com.example.dell.xunfei;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by ${hujiqiang} on 2017/09/06.
 */

public class MyApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=59afcfd0");
    }
}

package com.transsnet.autoupdate;

import android.app.Application;
import android.content.Context;

import static com.transsnet.autoupdate.Comm.logE;

/**
 * Created by Jiangxuewu on 2018/8/14.
 */
public class AppTinker extends Application {

    public static final String TAG = AppTinker.class.getSimpleName();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        logE("attachBaseContext");
        //加壳原理测试DEMO
//        new Shell().shell(base);
        //Tinker热更新原理测试DEMO
        new Tinker().hotFix(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logE("onCreate");
    }

}

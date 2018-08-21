package com.transsnet.autoupdate;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.ArrayMap;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

import static com.transsnet.autoupdate.Comm.getDexFilePath;
import static com.transsnet.autoupdate.Comm.getOutputPath;
import static com.transsnet.autoupdate.Comm.log;
import static com.transsnet.autoupdate.Comm.logE;

/**
 * Created by Jiangxuewu on 2018/8/14.
 */
public class Shell {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void shell(Context base) {
        logE("shell, start");
        //获取需要升级的dex文件路径
        String sourcePathName = getDexFilePath(base);
        log("sourcePathName = " + sourcePathName);

        //获取dex优化后生产的odex的文件地址。 【文件地址】
        String outputPathName = new File(getOutputPath(base)).getParentFile().getAbsolutePath();
        log("outputPathName = " + outputPathName);

        File dir = new File(getOutputPath(base));
        if (!dir.exists()) {
            boolean res = dir.getParentFile().mkdir();
            log("res = " + res);
        }
        ClassLoader classLoader = null;
        if (!TextUtils.isEmpty(sourcePathName) && new File(sourcePathName).exists()) {
            //生产odex文件
            try {
                classLoader = new DexClassLoader(sourcePathName, outputPathName, null, Shell.class.getClassLoader());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        outputPathName = outputPathName + File.separator + "auto_update.dex";
        log("last outputPathName = " + outputPathName);
        //
        try {

            Class<?> cls = Class.forName("android.app.ActivityThread");

            //获取 sCurrentActivityThread
            Method method = cls.getMethod("currentActivityThread");
            method.setAccessible(true);
            Object sCurrentActivityThread = method.invoke(null);

            //获取 mPackages
            Field field = cls.getDeclaredField("mPackages");
            field.setAccessible(true);
            ArrayMap mPackages = (ArrayMap) field.get(sCurrentActivityThread);

            //获取 包名
            String pkg = base.getPackageName();

            //获取 WeakReference<LoadedApk>
            WeakReference value = (WeakReference) mPackages.get(pkg);

            //LoadedApk
            Class<?> loadedApkCls = Class.forName("android.app.LoadedApk");

            //获取 mClassLoader
            Field loadedApkFiled = loadedApkCls.getDeclaredField("mClassLoader");
            loadedApkFiled.setAccessible(true);

            if (null != classLoader){
                loadedApkFiled.set(value.get(), classLoader);

                logE("Success");

                Object actObj = classLoader.loadClass("com.transsnet.autoupdate.MainActivity");
                logE("actObj" + actObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logE("Failed NoSuchFieldException " + e.getLocalizedMessage());
        }

        logE("shell, end");
    }
}

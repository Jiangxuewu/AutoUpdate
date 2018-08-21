package com.transsnet.autoupdate;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.transsnet.autoupdate.AppTinker.TAG;

/**
 * Created by Jiangxuewu on 2018/8/14.
 */
class Comm {

    static void log(String s) {
        Log.i(TAG, "" + s);
    }

    static void logE(String s) {
        Log.e(TAG, "" + s);
    }

    static String getOutputPath(Context base) {
        return "/data/data/" + base.getPackageName() + "/mytinker/update.apk";

    }

    static String getOptimizedDirectory(Context base) {
        return "/data/data/" + base.getPackageName() + "/mytinker/";

    }

    static String getDexFilePath(Context base) {
        String file = "/data/data/" + base.getPackageName() + "/auto_update.apk";

        File src = new File("/sdcard/TM/auto_update.apk");
        File dst = new File(file);
        if (src.exists() && !dst.exists()) {
            try {
                copyFile(src, new File(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
//            src.delete();
        }


        if (dst.exists()) {
            return file;
        } else {
            return null;
        }
    }

    private static void copyFile(File fromFile, File toFile) throws IOException {
        FileInputStream ins = new FileInputStream(fromFile);
        FileOutputStream out = new FileOutputStream(toFile);
        byte[] b = new byte[1024];
        int n;
        while ((n = ins.read(b)) != -1) {
            out.write(b, 0, n);
        }

        ins.close();
        out.close();
    }

}

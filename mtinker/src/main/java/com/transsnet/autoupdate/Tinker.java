package com.transsnet.autoupdate;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.transsnet.autoupdate.Comm.getDexFilePath;
import static com.transsnet.autoupdate.Comm.getOptimizedDirectory;
import static com.transsnet.autoupdate.Comm.log;
import static com.transsnet.autoupdate.Comm.logE;

/**
 * Created by Jiangxuewu on 2018/8/14.
 */
public class Tinker {

    /**
     * 原理:
     * <p>1, 从服务器下载修复后的dex[可以是新apk中的dex之外的东西全部删除即可，然后下发给客户端]</p>
     * <p>2, 把下载的包含新dex的apk通过反射转换成Element对象，并且放在数组前端</p>
     *
     * @param base Context
     */
    public void hotFix(Context base) {
        logE("hotFix, start");
        //获取需要升级的dex文件路径
        String sourcePathName = getDexFilePath(base);
        log("sourcePathName = " + sourcePathName);

        if (TextUtils.isEmpty(sourcePathName)) {
            return;
        }

        File dir = new File(sourcePathName);
        if (!dir.exists()) {
            boolean res = dir.getParentFile().mkdir();
            log("res = " + res);
        }

//        if (!TextUtils.isEmpty(sourcePathName) && new File(sourcePathName).exists()) {
        //生产odex文件
//            try {
//                DexFile dexFile = DexFile.loadDex(sourcePathName, outputPathName, 0);

//                boolean delRes = new File(sourcePathName).delete();
//                log("delRes = " + delRes);

//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        try {

            if (!new File(sourcePathName).exists()) {
                logE("Not exists File -> sourcePathName = " + sourcePathName);
                return;
            }

            log("start reflex.................." + new File(sourcePathName).length());

            /*1,  反射获取class ClassLoader 中对象 pathList 的值*/
            ClassLoader loader = AppTinker.class.getClassLoader();

            log("loader class name is " + loader.getClass().getName());
            Field pathListField = ShareReflectUtil.findField(loader, "pathList");
            Object dexPathList = pathListField.get(loader);

            /*2, 反射修改 class DexPathList (pathList) 中的 对象 dexElements 的值*/
            Field jlrField = ShareReflectUtil.findField(dexPathList, "dexElements");

            //原始的dexFile
            Object[] original = (Object[]) jlrField.get(dexPathList);
            //新的dexFile
            Object[] myNewElements = createMyElements(base, dexPathList, sourcePathName);

            //合并后的dexFile， 新的dexFile在前面
            Object[] combined = (Object[]) Array.newInstance(original.getClass().getComponentType(), original.length + myNewElements.length);
            System.arraycopy(myNewElements, 0, combined, 0, myNewElements.length);
            System.arraycopy(original, 0, combined, myNewElements.length, original.length);

            //修改
            jlrField.set(dexPathList, combined);

            logE("Success");

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            logE("Failed NoSuchFieldException " + e.getLocalizedMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            logE("Failed IllegalAccessException " + e.getLocalizedMessage());
        }

        logE("hotFix, end");
    }

    private Object[] createMyElements(Context base, Object dexPathList, String outputPathName) {
        /*创建Elements不同的操作系统有不同的方法，需要一一适配*/
        //test for 23
        //makePathElements
        Method makePathElements = null;

        try {
            makePathElements = ShareReflectUtil.findMethod(dexPathList, "makePathElements", List.class, File.class, List.class);
        } catch (NoSuchMethodException e) {
            try {
                makePathElements = ShareReflectUtil.findMethod(dexPathList, "makePathElements", ArrayList.class, File.class, ArrayList.class);

            } catch (NoSuchMethodException ignore) {
            }
        }

        if (null == makePathElements) {
            return new Object[0];
        }

        File dexFile = new File(outputPathName);
        ArrayList<File> list = new ArrayList<>();
        list.add(dexFile);

        File optimizedDirectory = new File(getOptimizedDirectory(base));

        ArrayList<IOException> suppressedExceptions = new ArrayList<>();

        try {
            return (Object[]) makePathElements.invoke(dexPathList, list, optimizedDirectory, suppressedExceptions);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return new Object[0];
    }
}

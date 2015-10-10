package com.app.oliver.remember;

import android.os.Environment;
import android.os.SystemClock;

import java.io.File;

/**
 * Created by Oliver on 2015/10/5.
 */
public class RememberDataBase {

    public static final String DB_NAME = "remember";

    public static final String ID = "_id";
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String DETAIL = "detail";

    public static String path = Environment.getExternalStorageDirectory().getPath() + File.separator +
            RememberDataBase.class.getPackage().getName() + File.separator;

    static{
        while(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            SystemClock.sleep(500);
        }

        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }

    }

}

package com.zhan.kykp.entity.persistobject;

import android.util.Log;

import com.zhan.kykp.base.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by wuyue on 2015/10/14.
 */
public abstract class BasePersistObject implements Serializable {

    private static String getObjectPersistPath(String className) {
        return App.ctx.getFilesDir().getAbsolutePath()+"/"+className+".ser";
    }

    public static void persisObject(BasePersistObject object){
        if(object!=null){
            String className=object.getClass().getSimpleName();
            Log.i("BasePersistObject","Save object "+className);
            ObjectOutputStream out=null;
            try {
                out = new ObjectOutputStream(new FileOutputStream(getObjectPersistPath(className)));
                out.writeObject(object);
            } catch (IOException e) {
                Log.e("BasePersistObject", "Save object IOException:"+e.getMessage());
            }
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static <T> T getPersisObject(Class<T> objectClass){
        String className=objectClass.getSimpleName();
        String persistFilePath=getObjectPersistPath(className);
        File persistFile=new File(persistFilePath);
        if(!persistFile.exists()){
           return null;
        }

        T object=null;
        ObjectInputStream in=null;
        try {
            in = new ObjectInputStream(new FileInputStream(persistFile));
            object = objectClass.cast(in.readObject());
        } catch (IOException e) {
            Log.e("BasePersistObject", "get object("+className+") IOException:" + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("BasePersistObject", "get object(" + className + ") ClassNotFoundException:" + e.getMessage());
        }
        if(in!=null){
            try {
                in.close();
            } catch (IOException e) {

            }
        }
        return object;
    }

    public static void deletePersistObject(Class objectClass){
        String className=objectClass.getSimpleName();
        String path=getObjectPersistPath(className);
        File persistFile=new File(path);
        if(persistFile.exists()){
            persistFile.delete();
        }
    }
}

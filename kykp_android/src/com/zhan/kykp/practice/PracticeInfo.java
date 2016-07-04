package com.zhan.kykp.practice;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WuYue on 2015/10/21.
 */
public class PracticeInfo implements Parcelable {
    private String objectId;
    private String zipFile;
    private String title;
    private String subTitle;
    private int index;

    public PracticeInfo() {

    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getZipFile() {
        return zipFile;
    }

    public void setZipFile(String zipFile) {
        this.zipFile = zipFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    protected PracticeInfo(Parcel in) {
        objectId = in.readString();
        zipFile = in.readString();
        title = in.readString();
        subTitle = in.readString();
        index = in.readInt();
    }

    public static final Creator<PracticeInfo> CREATOR = new Creator<PracticeInfo>() {
        @Override
        public PracticeInfo createFromParcel(Parcel in) {
            return new PracticeInfo(in);
        }

        @Override
        public PracticeInfo[] newArray(int size) {
            return new PracticeInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectId);
        dest.writeString(zipFile);
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeInt(index);
    }
}

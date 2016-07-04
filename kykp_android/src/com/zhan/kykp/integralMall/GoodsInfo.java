package com.zhan.kykp.integralMall;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WuYue on 2015/11/27.
 */
public class GoodsInfo implements Parcelable {
    private String id;
    private String title;
    private String image;
    private int numberRemaining;
    private int scholarship;

    public GoodsInfo(){}

    protected GoodsInfo(Parcel in) {
        id = in.readString();
        title = in.readString();
        image = in.readString();
        numberRemaining = in.readInt();
        scholarship = in.readInt();
    }

    public static final Creator<GoodsInfo> CREATOR = new Creator<GoodsInfo>() {
        @Override
        public GoodsInfo createFromParcel(Parcel in) {
            return new GoodsInfo(in);
        }

        @Override
        public GoodsInfo[] newArray(int size) {
            return new GoodsInfo[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getNumberRemaining() {
        return numberRemaining;
    }

    public void setNumberRemaining(int numberRemaining) {
        this.numberRemaining = numberRemaining;
    }

    public int getScholarship() {
        return scholarship;
    }

    public void setScholarship(int scholarship) {
        this.scholarship = scholarship;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(image);
        dest.writeInt(numberRemaining);
        dest.writeInt(scholarship);
    }
}

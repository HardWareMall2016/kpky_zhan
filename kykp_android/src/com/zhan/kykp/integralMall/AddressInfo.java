package com.zhan.kykp.integralMall;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WuYue on 2015/11/27.
 */
public class AddressInfo implements Parcelable {
    private String id;
    private String consignee;
    private String phone;
    private String detailsAddr;
    private String postCode;
    private String remark;

    public AddressInfo(){}

    protected AddressInfo(Parcel in) {
        id = in.readString();
        consignee = in.readString();
        phone = in.readString();
        detailsAddr = in.readString();
        postCode = in.readString();
        remark = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDetailsAddr() {
        return detailsAddr;
    }

    public void setDetailsAddr(String detailsAddr) {
        this.detailsAddr = detailsAddr;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static final Creator<AddressInfo> CREATOR = new Creator<AddressInfo>() {
        @Override
        public AddressInfo createFromParcel(Parcel in) {
            return new AddressInfo(in);
        }

        @Override
        public AddressInfo[] newArray(int size) {
            return new AddressInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(consignee);
        dest.writeString(phone);
        dest.writeString(detailsAddr);
        dest.writeString(postCode);
        dest.writeString(remark);
    }
}

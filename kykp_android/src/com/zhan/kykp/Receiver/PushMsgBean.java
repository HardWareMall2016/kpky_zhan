package com.zhan.kykp.Receiver;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WuYue on 2015/11/30.
 */
public class PushMsgBean implements Parcelable{

    /**
     * updatedAt : 1448840509
     * content : 收到了吗？
     * fromUser : 55deaedc00b0afd40405ddcb
     * type : 3
     * nickname : 可可
     * formUserUrl : http://reference.tpooo.net/upload/avatar/2015/10/21/562757c13dec2.png
     */

    private int updatedAt;
    private String content;
    private String fromUser;
    private int type;
    private String nickname;
    private String formUserUrl;

    public PushMsgBean(){

    }

    protected PushMsgBean(Parcel in) {
        updatedAt = in.readInt();
        content = in.readString();
        fromUser = in.readString();
        type = in.readInt();
        nickname = in.readString();
        formUserUrl = in.readString();
    }

    public static final Creator<PushMsgBean> CREATOR = new Creator<PushMsgBean>() {
        @Override
        public PushMsgBean createFromParcel(Parcel in) {
            return new PushMsgBean(in);
        }

        @Override
        public PushMsgBean[] newArray(int size) {
            return new PushMsgBean[size];
        }
    };

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFormUserUrl(String formUserUrl) {
        this.formUserUrl = formUserUrl;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public String getContent() {
        return content;
    }

    public String getFromUser() {
        return fromUser;
    }

    public int getType() {
        return type;
    }

    public String getNickname() {
        return nickname;
    }

    public String getFormUserUrl() {
        return formUserUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(updatedAt);
        dest.writeString(content);
        dest.writeString(fromUser);
        dest.writeInt(type);
        dest.writeString(nickname);
        dest.writeString(formUserUrl);
    }
}

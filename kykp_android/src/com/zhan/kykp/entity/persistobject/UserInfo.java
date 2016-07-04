package com.zhan.kykp.entity.persistobject;

/**
 * Created by Wuyue on 2015/10/14.
 */
public class UserInfo extends BasePersistObject {
    protected String objectId;
    protected int follower;//粉丝数
    protected String nickname;
    protected String username;
    protected int applicationLimit;
    protected boolean emailVerified;
    protected String mobilePhoneNumber;
    protected String avatar;
    protected int followee;//关注数
    protected String authData;
    protected boolean mobilePhoneVerified;
    protected int userType;
    protected int credit;//奖学金
    protected String level;//等级
    protected int currentIntegral;//当前积分值
    protected int maximumIntegral;//当前等级最大积分值
    protected String age;//年龄
    protected String sex; //性别


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getApplicationLimit() {
        return applicationLimit;
    }

    public void setApplicationLimit(int applicationLimit) {
        this.applicationLimit = applicationLimit;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getFollowee() {
        return followee;
    }

    public void setFollowee(int followee) {
        this.followee = followee;
    }

    public String getAuthData() {
        return authData;
    }

    public void setAuthData(String authData) {
        this.authData = authData;
    }

    public boolean isMobilePhoneVerified() {
        return mobilePhoneVerified;
    }

    public void setMobilePhoneVerified(boolean mobilePhoneVerified) {
        this.mobilePhoneVerified = mobilePhoneVerified;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getCurrentIntegral() {
        return currentIntegral;
    }

    public void setCurrentIntegral(int currentIntegral) {
        this.currentIntegral = currentIntegral;
    }

    public int getMaximumIntegral() {
        return maximumIntegral;
    }

    public void setMaximumIntegral(int maximumIntegral) {
        this.maximumIntegral = maximumIntegral;
    }

    public static UserInfo getCurrentUser() {
        return getPersisObject(UserInfo.class);
    }

    public static void saveLoginUserInfo(UserInfo user) {
        persisObject(user);
    }

    public static void logout() {
        deletePersistObject(UserInfo.class);
    }
}

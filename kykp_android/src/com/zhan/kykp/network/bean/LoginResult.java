package com.zhan.kykp.network.bean;

/**
 * Created by WuYue on 2015/10/20.
 */
public class LoginResult extends BaseBean{

    private DatasEntity datas;

    public void setDatas(DatasEntity datas) {
        this.datas = datas;
    }

    public DatasEntity getDatas() {
        return datas;
    }

    public static class DatasEntity {

        private UserInfoEntity userInfo;

        public void setUserInfo(UserInfoEntity userInfo) {
            this.userInfo = userInfo;
        }

        public UserInfoEntity getUserInfo() {
            return userInfo;
        }

        public static class UserInfoEntity {

            private String objectId;
            private String updatedAt;
            private int follower;
            private String nickname;
            private String username;
            private int applicationLimit;
            private String createdAt;
            private boolean emailVerified;
            private String mobilePhoneNumber;
            private String avatar;
            private int followee;
            private String authData;
            private boolean mobilePhoneVerified;
            private int isBBSUser;
            private int credit;
            private String level;
            private int current_Integral;
            private int maximum_Integral;
            private int age ;
            private String sex ;

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }



            public int getAge() {
                return age;
            }

            public void setAge(int age) {
                this.age = age;
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

            public int getIsBBSUser() {
                return isBBSUser;
            }

            public void setIsBBSUser(int isBBSUser) {
                this.isBBSUser = isBBSUser;
            }

            public void setObjectId(String objectId) {
                this.objectId = objectId;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

            public void setFollower(int follower) {
                this.follower = follower;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public void setApplicationLimit(int applicationLimit) {
                this.applicationLimit = applicationLimit;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public void setEmailVerified(boolean emailVerified) {
                this.emailVerified = emailVerified;
            }

            public void setMobilePhoneNumber(String mobilePhoneNumber) {
                this.mobilePhoneNumber = mobilePhoneNumber;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public void setFollowee(int followee) {
                this.followee = followee;
            }

            public void setAuthData(String authData) {
                this.authData = authData;
            }

            public void setMobilePhoneVerified(boolean mobilePhoneVerified) {
                this.mobilePhoneVerified = mobilePhoneVerified;
            }

            public String getObjectId() {
                return objectId;
            }

            public String getUpdatedAt() {
                return updatedAt;
            }

            public int getFollower() {
                return follower;
            }

            public String getNickname() {
                return nickname;
            }

            public String getUsername() {
                return username;
            }

            public int getApplicationLimit() {
                return applicationLimit;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public boolean getEmailVerified() {
                return emailVerified;
            }

            public String getMobilePhoneNumber() {
                return mobilePhoneNumber;
            }

            public String getAvatar() {
                return avatar;
            }

            public int getFollowee() {
                return followee;
            }

            public String getAuthData() {
                return authData;
            }

            public boolean getMobilePhoneVerified() {
                return mobilePhoneVerified;
            }

            public int getCurrent_Integral() {
                return current_Integral;
            }

            public void setCurrent_Integral(int current_Integral) {
                this.current_Integral = current_Integral;
            }

            public int getMaximum_Integral() {
                return maximum_Integral;
            }

            public void setMaximum_Integral(int maximum_Integral) {
                this.maximum_Integral = maximum_Integral;
            }
        }
    }
}

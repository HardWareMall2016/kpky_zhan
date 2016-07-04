package com.zhan.kykp.network.bean;


/**
 * Created by Administrator on 2015/9/17.
 */
public class UserInfoBean extends BaseBean{

    /**
     * datas : {"_id":"55e5629600b04a63ffff630f","updatedAt":"2015-10-19T02:55:52.512Z","follower":10,"nickname":"五月","username":"18616556160","applicationLimit":2,"createdAt":"2015-09-01T08:32:22.751Z","emailVerified":false,"mobilePhoneNumber":"18616556160","avatar":"http://ac-jizsrvn7.clouddn.com/lvoKHsFTEcy51lGtJymIcYkE7mcpIyFqE2jheZC2.jpg","followee":3,"mobilePhoneVerified":false,"credit":20,"level":"高中生","objectId":"55e5629600b04a63ffff630f","isBBSUser":0}
     */

    private DatasEntity datas;

    public void setDatas(DatasEntity datas) {
        this.datas = datas;
    }

    public DatasEntity getDatas() {
        return datas;
    }

    public static class DatasEntity {
        /**
         * _id : 55e5629600b04a63ffff630f
         * updatedAt : 2015-10-19T02:55:52.512Z
         * follower : 10
         * nickname : 五月
         * username : 18616556160
         * applicationLimit : 2
         * createdAt : 2015-09-01T08:32:22.751Z
         * emailVerified : false
         * mobilePhoneNumber : 18616556160
         * avatar : http://ac-jizsrvn7.clouddn.com/lvoKHsFTEcy51lGtJymIcYkE7mcpIyFqE2jheZC2.jpg
         * followee : 3
         * mobilePhoneVerified : false
         * credit : 20
         * level : 高中生
         * objectId : 55e5629600b04a63ffff630f
         * isBBSUser : 0
         */

        private String _id;
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
        private boolean mobilePhoneVerified;
        private int credit;
        private String level;
        private String objectId;
        private int isBBSUser;
        private int current_Integral;
        private int maximum_Integral;

        public void set_id(String _id) {
            this._id = _id;
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

        public void setMobilePhoneVerified(boolean mobilePhoneVerified) {
            this.mobilePhoneVerified = mobilePhoneVerified;
        }

        public void setCredit(int credit) {
            this.credit = credit;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setIsBBSUser(int isBBSUser) {
            this.isBBSUser = isBBSUser;
        }

        public String get_id() {
            return _id;
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

        public boolean getMobilePhoneVerified() {
            return mobilePhoneVerified;
        }

        public int getCredit() {
            return credit;
        }

        public String getLevel() {
            return level;
        }

        public String getObjectId() {
            return objectId;
        }

        public int getIsBBSUser() {
            return isBBSUser;
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
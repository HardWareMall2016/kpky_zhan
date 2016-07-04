package com.zhan.kykp.network.bean;

/**
 * Created by Administrator on 2015/9/16.
 */
public class MarkResultBean extends BaseBean{

    private DatasEntity datas;

    public void setDatas(DatasEntity datas) {
        this.datas = datas;
    }

    public DatasEntity getDatas() {
        return datas;
    }

    public static class DatasEntity {
        /**
         * praise : 0
         * updatedAt : 2015-09-16T06:56:59.190Z
         * mark : 5
         * objectId : 55f90c7b60b232fc2a92ca26
         * markerCount : 1
         * createdAt : 1442356219
         * recordTime : 37
         * ispraise : 0
         * audio : http:\/\/reference.tpooo.net\/upload\/rec\/2015\/09\/16\/55f90c7ba032f.amr
         * nickname : \u4e00\u751f\u4f55\u6c42
         * avatar : http:\/\/ac-jizsrvn7.clouddn.com\/55JarINWMxB7tL62ZgflCSB
         * userId : 55e534b700b0ded31805dd79
         * isteacher : 0
         */

        private int praise;
        private String updatedAt;
        private float mark;
        private String objectId;
        private int markerCount;
        private int createdAt;
        private int recordTime;
        private int ispraise;
        private String audio;
        private String nickname;
        private String avatar;
        private String userId;
        private int isteacher;

        public void setPraise(int praise) {
            this.praise = praise;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setMark(float mark) {
            this.mark = mark;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setMarkerCount(int markerCount) {
            this.markerCount = markerCount;
        }

        public void setCreatedAt(int createdAt) {
            this.createdAt = createdAt;
        }

        public void setRecordTime(int recordTime) {
            this.recordTime = recordTime;
        }

        public void setIspraise(int ispraise) {
            this.ispraise = ispraise;
        }

        public void setAudio(String audio) {
            this.audio = audio;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setIsteacher(int isteacher) {
            this.isteacher = isteacher;
        }

        public int getPraise() {
            return praise;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public float getMark() {
            return mark;
        }

        public String getObjectId() {
            return objectId;
        }

        public int getMarkerCount() {
            return markerCount;
        }

        public int getCreatedAt() {
            return createdAt;
        }

        public int getRecordTime() {
            return recordTime;
        }

        public int getIspraise() {
            return ispraise;
        }

        public String getAudio() {
            return audio;
        }

        public String getNickname() {
            return nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getUserId() {
            return userId;
        }

        public int getIsteacher() {
            return isteacher;
        }
    }
}

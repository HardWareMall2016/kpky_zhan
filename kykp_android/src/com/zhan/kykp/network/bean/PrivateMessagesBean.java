package com.zhan.kykp.network.bean;

import java.util.List;

/**
 * Created by Administrator on 2015/9/16.
 */
public class PrivateMessagesBean extends BaseBean{

    private List<DatasEntity> datas;

    public void setDatas(List<DatasEntity> datas) {
        this.datas = datas;
    }

    public List<DatasEntity> getDatas() {
        return datas;
    }

    public static class DatasEntity {
        /**
         * content : 你好😊
         * createdAt : 2015-09-17T06:18:18.915Z
         * updatedAt : 2015-09-17T06:18:18.915Z
         * objectId : 55fa5b2a60b2f3a980ac0d65
         * status : 1
         * toUserInfoId : 55e50781ddb2e44a9b7c886e
         * formUserInfoId : 55e5629600b04a63ffff630f
         * type : me
         * formUserInfo : http://ac-jizsrvn7.clouddn.com/877UkB4zlJWQHx5yzGoWeRpS809mMQ5BefqbKej7.jpg
         */

        private String content;
        private long createdAt;
        private String updatedAt;
        private String objectId;
        private String status;
        private String toUserInfoId;
        private String formUserInfoId;
        private String type;
        private String formUserInfo;

        public void setContent(String content) {
            this.content = content;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setToUserInfoId(String toUserInfoId) {
            this.toUserInfoId = toUserInfoId;
        }

        public void setFormUserInfoId(String formUserInfoId) {
            this.formUserInfoId = formUserInfoId;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setFormUserInfo(String formUserInfo) {
            this.formUserInfo = formUserInfo;
        }

        public String getContent() {
            return content;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public String getObjectId() {
            return objectId;
        }

        public String getStatus() {
            return status;
        }

        public String getToUserInfoId() {
            return toUserInfoId;
        }

        public String getFormUserInfoId() {
            return formUserInfoId;
        }

        public String getType() {
            return type;
        }

        public String getFormUserInfo() {
            return formUserInfo;
        }
    }
}

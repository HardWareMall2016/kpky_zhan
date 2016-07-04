package com.zhan.kykp.network.bean;

import java.util.List;

/**
 * Created by Administrator on 2015/9/16.
 */
public class AllMessageListBean extends BaseBean{

    private DatasEntity datas;

    public void setDatas(DatasEntity datas) {
        this.datas = datas;
    }

    public DatasEntity getDatas() {
        return datas;
    }

    public static class DatasEntity {
        /**
         * userId : 55e5629600b04a63ffff630f
         * system : {"status":1,"nickname":"系统消息","content":"您于9月14日参与的口语广场答题被选为\"精彩回答\"!获得100积分，感谢你的积极！","createdAt":1442185072}
         * praise : {"status":2,"nickname":"赞","content":"","createdAt":1442546208}
         * private : [{"content":"对了吧","createdAt":1442451593,"updatedAt":1442451593,"objectId":"55fa810960b232fc2acdc7cf","status":"1","othersId":"55e50781ddb2e44a9b7c886e","nickname":"rock.zeng","formUserUrl":"http://ac-jizsrvn7.clouddn.com/ub4AZ9BsJ7a7sDZl2lryJ5B"}]
         */

        private String userId;
        private SystemEntity system;
        private PraiseEntity praise;
        private FeedbackEntity feedback;
        @com.google.gson.annotations.SerializedName("private")
        private List<PrivateEntity> privateX;

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setSystem(SystemEntity system) {
            this.system = system;
        }

        public void setPraise(PraiseEntity praise) {
            this.praise = praise;
        }

        public void setPrivateMsg(List<PrivateEntity> privateX) {
            this.privateX = privateX;
        }

        public String getUserId() {
            return userId;
        }

        public SystemEntity getSystem() {
            return system;
        }

        public PraiseEntity getPraise() {
            return praise;
        }

        public FeedbackEntity getFeedback() {
            return feedback;
        }

        public void setFeedback(FeedbackEntity feedback) {
            this.feedback = feedback;
        }

        public List<PrivateEntity> getPrivateMsg() {
            return privateX;
        }

        public static class SystemEntity {
            /**
             * status : 1
             * nickname : 系统消息
             * content : 您于9月14日参与的口语广场答题被选为"精彩回答"!获得100积分，感谢你的积极！
             * createdAt : 1442185072
             */

            private int status;
            private String nickname;
            private String content;
            private long createdAt;
            private long updatedAt;

            public void setStatus(int status) {
                this.status = status;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public void setCreatedAt(long createdAt) {
                this.createdAt = createdAt;
            }

            public int getStatus() {
                return status;
            }

            public String getNickname() {
                return nickname;
            }

            public String getContent() {
                return content;
            }

            public long getCreatedAt() {
                return createdAt;
            }
            
            public long getUpdatedAt() {
                return updatedAt;
            }
            
            public void setUpdatedAt(long updatedAt) {
                this.updatedAt = updatedAt;
            }
        }

        public static class PraiseEntity {
            /**
             * status : 2
             * nickname : 赞
             * content :
             * createdAt : 1442546208
             */

            private int status;
            private String nickname;
            private String content;
            private long createdAt;
            private long updatedAt;

            public void setStatus(int status) {
                this.status = status;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public void setCreatedAt(long createdAt) {
                this.createdAt = createdAt;
            }

            public int getStatus() {
                return status;
            }

            public String getNickname() {
                return nickname;
            }

            public String getContent() {
                return content;
            }

            public long getCreatedAt() {
                return createdAt;
            }
            
            public long getUpdatedAt() {
                return updatedAt;
            }
            
            public void setUpdatedAt(long updatedAt) {
                this.updatedAt = updatedAt;
            }
        }

        public static class FeedbackEntity {
            private String content;
            private long createdAt;
            private long updatedAt;
            private String objectId;
            private String status;
            private String othersId;
            private String nickname;
            private String formUserUrl;

            public void setContent(String content) {
                this.content = content;
            }

            public void setCreatedAt(long createdAt) {
                this.createdAt = createdAt;
            }

            public void setUpdatedAt(long updatedAt) {
                this.updatedAt = updatedAt;
            }

            public void setObjectId(String objectId) {
                this.objectId = objectId;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public void setOthersId(String othersId) {
                this.othersId = othersId;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public void setFormUserUrl(String formUserUrl) {
                this.formUserUrl = formUserUrl;
            }

            public String getContent() {
                return content;
            }

            public long getCreatedAt() {
                return createdAt;
            }

            public long getUpdatedAt() {
                return updatedAt;
            }

            public String getObjectId() {
                return objectId;
            }

            public String getStatus() {
                return status;
            }

            public String getOthersId() {
                return othersId;
            }

            public String getNickname() {
                return nickname;
            }

            public String getFormUserUrl() {
                return formUserUrl;
            }
        }

        public static class PrivateEntity {
            /**
             * content : 对了吧
             * createdAt : 1442451593
             * updatedAt : 1442451593
             * objectId : 55fa810960b232fc2acdc7cf
             * status : 1
             * othersId : 55e50781ddb2e44a9b7c886e
             * nickname : rock.zeng
             * formUserUrl : http://ac-jizsrvn7.clouddn.com/ub4AZ9BsJ7a7sDZl2lryJ5B
             */

            private String content;
            private long createdAt;
            private long updatedAt;
            private String objectId;
            private String status;
            private String othersId;
            private String nickname;
            private String formUserUrl;

            public void setContent(String content) {
                this.content = content;
            }

            public void setCreatedAt(long createdAt) {
                this.createdAt = createdAt;
            }

            public void setUpdatedAt(long updatedAt) {
                this.updatedAt = updatedAt;
            }

            public void setObjectId(String objectId) {
                this.objectId = objectId;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public void setOthersId(String othersId) {
                this.othersId = othersId;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public void setFormUserUrl(String formUserUrl) {
                this.formUserUrl = formUserUrl;
            }

            public String getContent() {
                return content;
            }

            public long getCreatedAt() {
                return createdAt;
            }

            public long getUpdatedAt() {
                return updatedAt;
            }

            public String getObjectId() {
                return objectId;
            }

            public String getStatus() {
                return status;
            }

            public String getOthersId() {
                return othersId;
            }

            public String getNickname() {
                return nickname;
            }

            public String getFormUserUrl() {
                return formUserUrl;
            }
        }
    }
}

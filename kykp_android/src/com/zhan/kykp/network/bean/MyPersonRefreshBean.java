package com.zhan.kykp.network.bean;


import java.util.List;

public class MyPersonRefreshBean {
 /**
 * Created by Administrator on 2015/9/17.
 */
    /**
     * status : 1
     * message :
     * datas : [{"createdAt":1442283766,"updatedAt":1442283766,"objectId":"55f7f17660b28e6a6f1e30c8","userInfo":{"objectId":"55e50781ddb2e44a9b7c886e","nickname":"rock.zeng","url":"http://ac-jizsrvn7.clouddn.com/ub4AZ9BsJ7a7sDZl2lryJ5B"}},{"createdAt":1442283765,"updatedAt":1442283765,"objectId":"55f7f175ddb2e44a0ce55cfe","userInfo":{"objectId":"55e50781ddb2e44a9b7c886e","nickname":"rock.zeng","url":"http://ac-jizsrvn7.clouddn.com/ub4AZ9BsJ7a7sDZl2lryJ5B"}},{"createdAt":1442283765,"updatedAt":1442283765,"objectId":"55f7f17560b227b717e2a6cf","userInfo":{"objectId":"55e50781ddb2e44a9b7c886e","nickname":"rock.zeng","url":"http://ac-jizsrvn7.clouddn.com/ub4AZ9BsJ7a7sDZl2lryJ5B"}},{"createdAt":1442283765,"updatedAt":1442283765,"objectId":"55f7f17560b28e6a6f1e309e","userInfo":{"objectId":"55e50781ddb2e44a9b7c886e","nickname":"rock.zeng","url":"http://ac-jizsrvn7.clouddn.com/ub4AZ9BsJ7a7sDZl2lryJ5B"}},{"createdAt":1442283760,"updatedAt":1442283760,"objectId":"55f7f17060b227b717e2a4fa","userInfo":{"objectId":"55e50781ddb2e44a9b7c886e","nickname":"rock.zeng","url":"http://ac-jizsrvn7.clouddn.com/ub4AZ9BsJ7a7sDZl2lryJ5B"}},{"createdAt":1442283760,"updatedAt":1442283760,"objectId":"55f7f17060b27e6c93328557","userInfo":{"objectId":"55e50781ddb2e44a9b7c886e","nickname":"rock.zeng","url":"http://ac-jizsrvn7.clouddn.com/ub4AZ9BsJ7a7sDZl2lryJ5B"}},{"createdAt":1442283760,"updatedAt":1442283760,"objectId":"55f7f17060b21fbf5a2a95d2","userInfo":{"objectId":"55e50781ddb2e44a9b7c886e","nickname":"rock.zeng","url":"http://ac-jizsrvn7.clouddn.com/ub4AZ9BsJ7a7sDZl2lryJ5B"}}]
     */

    private int status;
    private String message;
    private List<DatasEntity> datas;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDatas(List<DatasEntity> datas) {
        this.datas = datas;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<DatasEntity> getDatas() {
        return datas;
    }

    public static class DatasEntity {
        /**
         * createdAt : 1442283766
         * updatedAt : 1442283766
         * objectId : 55f7f17660b28e6a6f1e30c8
         * userInfo : {"objectId":"55e50781ddb2e44a9b7c886e","nickname":"rock.zeng","url":"http://ac-jizsrvn7.clouddn.com/ub4AZ9BsJ7a7sDZl2lryJ5B"}
         */

        private int createdAt;
        private int updatedAt;
        private String objectId;
        private UserInfoEntity userInfo;

        public void setCreatedAt(int createdAt) {
            this.createdAt = createdAt;
        }

        public void setUpdatedAt(int updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setUserInfo(UserInfoEntity userInfo) {
            this.userInfo = userInfo;
        }

        public int getCreatedAt() {
            return createdAt;
        }

        public int getUpdatedAt() {
            return updatedAt;
        }

        public String getObjectId() {
            return objectId;
        }

        public UserInfoEntity getUserInfo() {
            return userInfo;
        }

        public static class UserInfoEntity {
            /**
             * objectId : 55e50781ddb2e44a9b7c886e
             * nickname : rock.zeng
             * url : http://ac-jizsrvn7.clouddn.com/ub4AZ9BsJ7a7sDZl2lryJ5B
             */

            private String objectId;
            private String nickname;
            private String url;

            public void setObjectId(String objectId) {
                this.objectId = objectId;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getObjectId() {
                return objectId;
            }

            public String getNickname() {
                return nickname;
            }

            public String getUrl() {
                return url;
            }
        }
    }
}

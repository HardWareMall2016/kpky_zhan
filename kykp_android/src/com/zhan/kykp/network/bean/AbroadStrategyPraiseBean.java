package com.zhan.kykp.network.bean;

import java.util.List;

/**
 * Created by Administrator on 2015/10/30.
 */
public class AbroadStrategyPraiseBean {
    /**
     * status : 1
     * message : Success
     * datas : [{"title":"The moment you think about giving up,think of the reason why","image":"http://bbsapp.static.tpooo.net/2015-10-28/56309dd8e352e.png","content":"The moment you think about giving up,think of the reason why","updatedAt":1446072186,"createdAt":1446072186,"praise":1,"objectId":"56318503b0e265b05bcae6eb","status":1}]
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
         * title : The moment you think about giving up,think of the reason why
         * image : http://bbsapp.static.tpooo.net/2015-10-28/56309dd8e352e.png
         * content : The moment you think about giving up,think of the reason why
         * updatedAt : 1446072186
         * createdAt : 1446072186
         * praise : 1
         * objectId : 56318503b0e265b05bcae6eb
         * status : 1
         */

        private String title;
        private String image;
        private String content;
        private int updatedAt;
        private int createdAt;
        private int praise;
        private String objectId;
        private int status;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setUpdatedAt(int updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setCreatedAt(int createdAt) {
            this.createdAt = createdAt;
        }

        public void setPraise(int praise) {
            this.praise = praise;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTitle() {
            return title;
        }

        public String getImage() {
            return image;
        }

        public String getContent() {
            return content;
        }

        public int getUpdatedAt() {
            return updatedAt;
        }

        public int getCreatedAt() {
            return createdAt;
        }

        public int getPraise() {
            return praise;
        }

        public String getObjectId() {
            return objectId;
        }

        public int getStatus() {
            return status;
        }
    }
}

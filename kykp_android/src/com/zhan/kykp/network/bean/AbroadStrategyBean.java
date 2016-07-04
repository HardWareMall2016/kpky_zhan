package com.zhan.kykp.network.bean;

import java.util.List;

/**
 * Created by Administrator on 2015/10/29.
 */
public class AbroadStrategyBean {
    /**
     * status : 1
     * message : Success
     * datas : [{"title":"The moment you think about giving up,think of the reason why","image":"http://bbsapp.static.tpooo.net/2015-10-28/56309dd8e352e.png","content":"The moment you think about giving up,think of the reason why","updatedAt":1446072186,"createdAt":1446072186,"praise":0,"objectId":"56318503b0e265b05bcae6eb"},{"title":"In my dual profession as an educator and health care provider","image":"http://bbsapp.static.tpooo.net/2015-07-02/5594e0d9b0b90.png","content":"In my dual profession as an educator and health care provider, I have worked with numerous children infected with the virus that causes AIDS. The relationships that I have had with these special kids have been gifts in my life. They have taught me so many things, but I have especially learned that great courage can be found in the smallest of packages. Let me tell you about Tyler.","updatedAt":1446072173,"createdAt":1446072173,"praise":0,"objectId":"56318582b0e265b15bcae6ea"},{"title":"The consequences of today are determined by the actions of the past","image":"http://bbsapp.static.tpooo.net/2015-10-28/563090588f4d5.png","content":"The consequences of today are determined by the actions of the past. To change your future, alter your decisions today. ","updatedAt":1446072164,"createdAt":1446072164,"praise":0,"objectId":"5631870db0e265ad5bcae6f0"}]
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
         * praise : 0
         * objectId : 56318503b0e265b05bcae6eb
         */

        private String title;
        private String image;
        private String content;
        private int updatedAt;
        private int createdAt;
        private int praise;
        private String objectId;

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
    }
}

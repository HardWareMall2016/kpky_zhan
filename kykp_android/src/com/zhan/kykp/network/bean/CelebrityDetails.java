package com.zhan.kykp.network.bean;

/**
 * Created by WuYue on 2015/10/29.
 */
public class CelebrityDetails extends BaseBean {

    /**
     * datas : {"_id":"5631f086b0e265257a286cde","title":"I feel that ","content":"I feel that this award was not made to me as a man, but to my work -- a life's work in the agony and  he  one of the props17, the pillars to help him endure and prevail.","tags":"幽默","image":"http://bbsapp.static.tpooo.net/2015-10-29/5631f07ae9ec1.jpg","status":1,"planDate":1446048000,"updatedAt":1446113414,"audio":{"url":"http://bbsapp.static.tpooo.net/2015-10-29/5631e9c31a931.mp3","size":"520.00","time":176},"praise":0,"createdAt":1446113414,"ispraise":0,"objectId":"5631f086b0e265257a286cde"}
     */

    private DatasEntity datas;

    public void setDatas(DatasEntity datas) {
        this.datas = datas;
    }

    public DatasEntity getDatas() {
        return datas;
    }

    public static class DatasEntity {

        private String _id;
        private String title;
        private String content;
        private String tags;
        private String image;
        private int status;
        private int planDate;
        private int updatedAt;
        private AudioEntity audio;
        private int praise;
        private int createdAt;
        private int ispraise;
        private String objectId;

        public void set_id(String _id) {
            this._id = _id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setPlanDate(int planDate) {
            this.planDate = planDate;
        }

        public void setUpdatedAt(int updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setAudio(AudioEntity audio) {
            this.audio = audio;
        }

        public void setPraise(int praise) {
            this.praise = praise;
        }

        public void setCreatedAt(int createdAt) {
            this.createdAt = createdAt;
        }

        public void setIspraise(int ispraise) {
            this.ispraise = ispraise;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public String get_id() {
            return _id;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getTags() {
            return tags;
        }

        public String getImage() {
            return image;
        }

        public int getStatus() {
            return status;
        }

        public int getPlanDate() {
            return planDate;
        }

        public int getUpdatedAt() {
            return updatedAt;
        }

        public AudioEntity getAudio() {
            return audio;
        }

        public int getPraise() {
            return praise;
        }

        public int getCreatedAt() {
            return createdAt;
        }

        public int getIspraise() {
            return ispraise;
        }

        public String getObjectId() {
            return objectId;
        }

        public static class AudioEntity {
            /**
             * url : http://bbsapp.static.tpooo.net/2015-10-29/5631e9c31a931.mp3
             * size : 520.00
             * time : 176
             */

            private String url;
            private String size;
            private int time;

            public void setUrl(String url) {
                this.url = url;
            }

            public void setSize(String size) {
                this.size = size;
            }

            public void setTime(int time) {
                this.time = time;
            }

            public String getUrl() {
                return url;
            }

            public String getSize() {
                return size;
            }

            public int getTime() {
                return time;
            }
        }
    }
}

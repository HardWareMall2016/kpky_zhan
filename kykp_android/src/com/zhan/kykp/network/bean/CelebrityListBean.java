package com.zhan.kykp.network.bean;

import java.util.List;

/**
 * Created by WuYue on 2015/10/29.
 */
public class CelebrityListBean extends BaseBean {

    /**
     * datas : [{"title":"测试测试标题标题标题 ...","content":"文字内容文字内容文字内容文字内容文字内容 ...","audioTime":"52","planDate":1446048000,"objectId":"56319305b0e265ad5bcae6f2"}]
     */

    private List<DatasEntity> datas;

    public void setDatas(List<DatasEntity> datas) {
        this.datas = datas;
    }

    public List<DatasEntity> getDatas() {
        return datas;
    }

    public static class DatasEntity {
        /**
         * title : 测试测试标题标题标题 ...
         * content : 文字内容文字内容文字内容文字内容文字内容 ...
         * audioTime : 52
         * planDate : 1446048000
         * objectId : 56319305b0e265ad5bcae6f2
         */

        private String title;
        private String content;
        private String audioTime;
        private int planDate;
        private String objectId;
        private String image;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setAudioTime(String audioTime) {
            this.audioTime = audioTime;
        }

        public void setPlanDate(int planDate) {
            this.planDate = planDate;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getAudioTime() {
            return audioTime;
        }

        public int getPlanDate() {
            return planDate;
        }

        public String getObjectId() {
            return objectId;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}

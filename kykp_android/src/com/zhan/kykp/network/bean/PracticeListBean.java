package com.zhan.kykp.network.bean;

import java.util.List;

/**
 * Created by WuYue on 2015/10/21.
 */
public class PracticeListBean extends BaseBean {

    private List<DatasEntity> datas;

    public void setDatas(List<DatasEntity> datas) {
        this.datas = datas;
    }

    public List<DatasEntity> getDatas() {
        return datas;
    }

    public static class DatasEntity {
        /**
         * index : 1
         * zipFile : http://ac-jizsrvn7.clouddn.com/Jp31Ir1ms1Ov7IkefYjYkFIeh9I1ROyH1t33QjXb.zip
         * title : 学习习惯
         * subTitle : Study habit
         * createdAt : 2015-06-17T06:02:23.409Z
         * updatedAt : 2015-06-17T06:18:12.038Z
         * objectId : 55810d6fe4b035745ad39d79
         */

        private int index;
        private String zipFile;
        private String title;
        private String subTitle;
        private String createdAt;
        private String updatedAt;
        private String objectId;

        public void setIndex(int index) {
            this.index = index;
        }

        public void setZipFile(String zipFile) {
            this.zipFile = zipFile;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public int getIndex() {
            return index;
        }

        public String getZipFile() {
            return zipFile;
        }

        public String getTitle() {
            return title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public String getObjectId() {
            return objectId;
        }
    }
}

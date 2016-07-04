package com.zhan.kykp.network.bean;

import java.util.List;

/**
 * Created by WuYue on 2015/10/22.
 */
public class TpoListBean extends BaseBean {

    private List<DatasEntity> datas;

    public void setDatas(List<DatasEntity> datas) {
        this.datas = datas;
    }

    public List<DatasEntity> getDatas() {
        return datas;
    }

    public static class DatasEntity {
        private String objectId;
        private int index;
        private String zipFile;
        private String name;
        private String createdAt;
        private String updatedAt;

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setZipFile(String zipFile) {
            this.zipFile = zipFile;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getObjectId() {
            return objectId;
        }

        public int getIndex() {
            return index;
        }

        public String getZipFile() {
            return zipFile;
        }

        public String getName() {
            return name;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}

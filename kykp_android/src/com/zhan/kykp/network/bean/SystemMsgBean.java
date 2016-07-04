package com.zhan.kykp.network.bean;

import java.util.List;

public class SystemMsgBean extends BaseBean{

    private List<DatasEntity> datas;

    public void setDatas(List<DatasEntity> datas) {
        this.datas = datas;
    }

    public List<DatasEntity> getDatas() {
        return datas;
    }

    public static class DatasEntity {

        private String message;
        private int createdAt;

        public void setMessage(String message) {
            this.message = message;
        }

        public void setCreatedAt(int createdAt) {
            this.createdAt = createdAt;
        }

        public String getMessage() {
            return message;
        }

        public int getCreatedAt() {
            return createdAt;
        }
    }
}

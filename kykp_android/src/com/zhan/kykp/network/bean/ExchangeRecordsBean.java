package com.zhan.kykp.network.bean;

import java.util.List;

/**
 * Created by Administrator on 2015/11/27.
 */
public class ExchangeRecordsBean {
    /**
     * status : 1
     * message :
     * datas : [{"_id":"5657c818af85aed01d00002b","title":"无敌170号，宇宙空间通，没有做不到只有想不到","image":"http://bbsapp.static.tpooo.net/2015-10-28/563090588f4d5.png","status":0,"exchange":1,"createdAt":1448564632,"updatedAt":1448564632}]
     */

    private int status;
    private String message;
    /**
     * _id : 5657c818af85aed01d00002b
     * title : 无敌170号，宇宙空间通，没有做不到只有想不到
     * image : http://bbsapp.static.tpooo.net/2015-10-28/563090588f4d5.png
     * status : 0
     * exchange : 1
     * createdAt : 1448564632
     * updatedAt : 1448564632
     */

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
        private String _id;
        private String title;
        private String image;
        private String remark;
        private int status;
        private int exchange;
        private int createdAt;
        private int updatedAt;

        public void set_id(String _id) {
            this._id = _id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setExchange(int exchange) {
            this.exchange = exchange;
        }

        public void setCreatedAt(int createdAt) {
            this.createdAt = createdAt;
        }

        public void setUpdatedAt(int updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String get_id() {
            return _id;
        }

        public String getTitle() {
            return title;
        }

        public String getImage() {
            return image;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getStatus() {
            return status;
        }

        public int getExchange() {
            return exchange;
        }

        public int getCreatedAt() {
            return createdAt;
        }

        public int getUpdatedAt() {
            return updatedAt;
        }
    }
}

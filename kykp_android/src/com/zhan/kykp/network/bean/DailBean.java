package com.zhan.kykp.network.bean;

/**
 * Created by Administrator on 2015/10/22.
 */
public class DailBean {

    private int status;
    private String message;
    private DatasEntity datas;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDatas(DatasEntity datas) {
        this.datas = datas;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public DatasEntity getDatas() {
        return datas;
    }

    public static class DatasEntity {

        private String _id;
        private String ShowDate;
        private String From;
        private String ContentCN;
        private String Content;
        private String createdAt;
        private String updatedAt;

        public void set_id(String _id) {
            this._id = _id;
        }

        public void setShowDate(String ShowDate) {
            this.ShowDate = ShowDate;
        }

        public void setFrom(String From) {
            this.From = From;
        }

        public void setContentCN(String ContentCN) {
            this.ContentCN = ContentCN;
        }

        public void setContent(String Content) {
            this.Content = Content;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String get_id() {
            return _id;
        }

        public String getShowDate() {
            return ShowDate;
        }

        public String getFrom() {
            return From;
        }

        public String getContentCN() {
            return ContentCN;
        }

        public String getContent() {
            return Content;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}

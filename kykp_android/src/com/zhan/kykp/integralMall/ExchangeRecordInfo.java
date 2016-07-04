package com.zhan.kykp.integralMall;

/**
 * Created by Administrator on 2015/11/27.
 */
public class ExchangeRecordInfo {
     String _id;
     String title;
     String image;
     String remark;
     int status;
     int exchange;
     int createdAt;
     int updatedAt;


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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

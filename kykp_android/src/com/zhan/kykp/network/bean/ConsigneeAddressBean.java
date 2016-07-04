package com.zhan.kykp.network.bean;

/**
 * Created by Administrator on 2015/11/27.
 */
public class ConsigneeAddressBean {
    /**
     * status : 1
     * message :
     * datas : {"_id":"5653beccb0e265ec524c80ca","user":"553f45a1e4b05b80583bea3f","username":"张三","mobile":"17017017017","city":"Shanghai","address":"静安寺101室","code":200120,"remark":"给我整一张宇宙通的电话卡，我要星际迷航","createdAt":1448300108,"updatedAt":1448300108}
     */

    private int status;
    private String message;
    /**
     * _id : 5653beccb0e265ec524c80ca
     * user : 553f45a1e4b05b80583bea3f
     * username : 张三
     * mobile : 17017017017
     * city : Shanghai
     * address : 静安寺101室
     * code : 200120
     * remark : 给我整一张宇宙通的电话卡，我要星际迷航
     * createdAt : 1448300108
     * updatedAt : 1448300108
     */

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
        private String user;
        private String username;
        private String mobile;
        private String city;
        private String address;
        private String code;
        private String remark;
        private int createdAt;
        private int updatedAt;

        public void set_id(String _id) {
            this._id = _id;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setRemark(String remark) {
            this.remark = remark;
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

        public String getUser() {
            return user;
        }

        public String getUsername() {
            return username;
        }

        public String getMobile() {
            return mobile;
        }

        public String getCity() {
            return city;
        }

        public String getAddress() {
            return address;
        }

        public String getCode() {
            return code;
        }

        public String getRemark() {
            return remark;
        }

        public int getCreatedAt() {
            return createdAt;
        }

        public int getUpdatedAt() {
            return updatedAt;
        }
    }
}

package com.zhan.kykp.network.bean;

/**
 * Created by Administrator on 2015/11/27.
 */
public class ConsigneeAddressResultBean {
    /**
     * status : 1
     * message : Success
     * datas : {"_id":"565819a3b0e26504664c80d2"}
     */

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
        /**
         * _id : 565819a3b0e26504664c80d2
         */

        private String _id;

        public void set_id(String _id) {
            this._id = _id;
        }

        public String get_id() {
            return _id;
        }
    }
}

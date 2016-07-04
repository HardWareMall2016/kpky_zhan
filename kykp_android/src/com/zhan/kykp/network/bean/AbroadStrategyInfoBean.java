package com.zhan.kykp.network.bean;

/**
 * Created by Administrator on 2015/10/29.
 */
public class AbroadStrategyInfoBean {

    /**
     * status : 1
     * message : Success
     * datas : {"praise":1}
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
         * praise : 1
         */

        private int praise;

        public void setPraise(int praise) {
            this.praise = praise;
        }

        public int getPraise() {
            return praise;
        }
    }
}

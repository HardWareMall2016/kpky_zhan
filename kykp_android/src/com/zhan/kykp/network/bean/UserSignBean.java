package com.zhan.kykp.network.bean;

/**
 * Created by Administrator on 2015/11/2.
 */
public class UserSignBean {
    /**
     * status : 1
     * message : 签到成功
     * datas : {"days":0,"credit":20}
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
         * days : 0
         * credit : 20
         */

        private int days;
        private int credit;

        public void setDays(int days) {
            this.days = days;
        }

        public void setCredit(int credit) {
            this.credit = credit;
        }

        public int getDays() {
            return days;
        }

        public int getCredit() {
            return credit;
        }
    }
}

package com.zhan.kykp.network.bean;

/**
 * Created by Qs on 15/10/19.
 */
public class JijingTitleBean {
    /**
     * status : 1
     * message :
     * datas : {"title":"2015年9月17日、19日机经预测"}
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
         * title : 2015年9月17日、19日机经预测
         */

        private String title;

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}

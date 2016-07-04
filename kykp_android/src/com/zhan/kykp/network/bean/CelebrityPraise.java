package com.zhan.kykp.network.bean;

/**
 * Created by WuYue on 2015/10/29.
 */
public class CelebrityPraise extends BaseBean {
    /**
     * datas : {"praise":5}
     */

    private DatasEntity datas;

    public void setDatas(DatasEntity datas) {
        this.datas = datas;
    }

    public DatasEntity getDatas() {
        return datas;
    }

    public static class DatasEntity {
        /**
         * praise : 5
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

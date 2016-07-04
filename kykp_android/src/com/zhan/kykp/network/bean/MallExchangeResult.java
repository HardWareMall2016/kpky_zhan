package com.zhan.kykp.network.bean;

/**
 * Created by WuYue on 2015/11/30.
 */
public class MallExchangeResult extends BaseBean {

    /**
     * datas : {"scholarship":100230,"realnum":993}
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
         * scholarship : 100230
         * realnum : 993
         */

        private int scholarship;
        private int realnum;

        public void setScholarship(int scholarship) {
            this.scholarship = scholarship;
        }

        public void setRealnum(int realnum) {
            this.realnum = realnum;
        }

        public int getScholarship() {
            return scholarship;
        }

        public int getRealnum() {
            return realnum;
        }
    }
}

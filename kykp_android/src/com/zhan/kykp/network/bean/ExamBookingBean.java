package com.zhan.kykp.network.bean;

/**
 * Created by WuYue on 2015/10/21.
 */
public class ExamBookingBean extends BaseBean {

    /**
     * datas : {"isOpen":0}
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
         * isOpen : 0
         */

        private int isOpen;

        public void setIsOpen(int isOpen) {
            this.isOpen = isOpen;
        }

        public int getIsOpen() {
            return isOpen;
        }
    }
}

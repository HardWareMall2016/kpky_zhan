package com.zhan.kykp.network.bean;

/**
 * Created by WuYue on 2015/10/29.
 */
public class UserIsSignBean extends BaseBean {

    /**
     * datas : {"isSign":1,"exam":{"type":"1","exam":"2015-10-27"}}
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
         * isSign : 1
         * exam : {"type":"1","exam":"2015-10-27"}
         */

        private int isSign;
        private ExamEntity exam;

        public void setIsSign(int isSign) {
            this.isSign = isSign;
        }

        public void setExam(ExamEntity exam) {
            this.exam = exam;
        }

        public int getIsSign() {
            return isSign;
        }

        public ExamEntity getExam() {
            return exam;
        }

        public static class ExamEntity {
            /**
             * type : 1
             * exam : 2015-10-27
             */

            private String type;
            private String exam;

            public void setType(String type) {
                this.type = type;
            }

            public void setExam(String exam) {
                this.exam = exam;
            }

            public String getType() {
                return type;
            }

            public String getExam() {
                return exam;
            }
        }
    }
}

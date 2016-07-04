package com.zhan.kykp.network.bean;

import java.util.List;

/**
 * Created by WuYue on 2015/10/21.
 */
public class ExamTimeListBean extends BaseBean {
    /**
     * datas : {"toefl":{"type":"1","name":"托福","exam":[{"time":"2015-10-30"},{"time":"2015-10-31"},{"time":"2015-11-04"},{"time":"2015-11-19"}]},"ielts":{"type":"2","name":"雅思","exam":[{"time":"2015-10-30"},{"time":"2015-10-31"},{"time":"2015-11-07"}]}}
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
         * toefl : {"type":"1","name":"托福","exam":[{"time":"2015-10-30"},{"time":"2015-10-31"},{"time":"2015-11-04"},{"time":"2015-11-19"}]}
         * ielts : {"type":"2","name":"雅思","exam":[{"time":"2015-10-30"},{"time":"2015-10-31"},{"time":"2015-11-07"}]}
         */

        private ToeflEntity toefl;
        private IeltsEntity ielts;

        public void setToefl(ToeflEntity toefl) {
            this.toefl = toefl;
        }

        public void setIelts(IeltsEntity ielts) {
            this.ielts = ielts;
        }

        public ToeflEntity getToefl() {
            return toefl;
        }

        public IeltsEntity getIelts() {
            return ielts;
        }

        public static class ToeflEntity {
            /**
             * type : 1
             * name : 托福
             * exam : [{"time":"2015-10-30"},{"time":"2015-10-31"},{"time":"2015-11-04"},{"time":"2015-11-19"}]
             */

            private String type;
            private String name;
            private List<ExamEntity> exam;

            public void setType(String type) {
                this.type = type;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setExam(List<ExamEntity> exam) {
                this.exam = exam;
            }

            public String getType() {
                return type;
            }

            public String getName() {
                return name;
            }

            public List<ExamEntity> getExam() {
                return exam;
            }

            public static class ExamEntity {
                /**
                 * time : 2015-10-30
                 */

                private String time;

                public void setTime(String time) {
                    this.time = time;
                }

                public String getTime() {
                    return time;
                }
            }
        }

        public static class IeltsEntity {
            /**
             * type : 2
             * name : 雅思
             * exam : [{"time":"2015-10-30"},{"time":"2015-10-31"},{"time":"2015-11-07"}]
             */

            private String type;
            private String name;
            private List<ExamEntity> exam;

            public void setType(String type) {
                this.type = type;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setExam(List<ExamEntity> exam) {
                this.exam = exam;
            }

            public String getType() {
                return type;
            }

            public String getName() {
                return name;
            }

            public List<ExamEntity> getExam() {
                return exam;
            }

            public static class ExamEntity {
                /**
                 * time : 2015-10-30
                 */

                private String time;

                public void setTime(String time) {
                    this.time = time;
                }

                public String getTime() {
                    return time;
                }
            }
        }
    }
}

package com.zhan.kykp.network.bean;

import java.util.List;

/**
 * Created by Administrator on 2015/11/5.
 */
public class TaskBean {

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

        private TopicEntity topic;
        private BeikaoEntity beikao;
        private OtherEntity other;

        public void setTopic(TopicEntity topic) {
            this.topic = topic;
        }

        public void setBeikao(BeikaoEntity beikao) {
            this.beikao = beikao;
        }

        public void setOther(OtherEntity other) {
            this.other = other;
        }

        public TopicEntity getTopic() {
            return topic;
        }

        public BeikaoEntity getBeikao() {
            return beikao;
        }

        public OtherEntity getOther() {
            return other;
        }

        public static class TopicEntity {
            private String name;
            /**
             * name : 口语广场答题
             * type : 口语广场
             * countNumber : 1
             * needNumber : 1
             * credit : 20
             * period : 1
             * status : 0
             * nowNumber : 0
             */

            private List<DataEntity> data;

            public void setName(String name) {
                this.name = name;
            }

            public void setData(List<DataEntity> data) {
                this.data = data;
            }

            public String getName() {
                return name;
            }

            public List<DataEntity> getData() {
                return data;
            }

            public static class DataEntity {
                private String name;
                private String type;
                private int countNumber;
                private int needNumber;
                private int credit;
                private int period;
                private int status;
                private int nowNumber;

                public void setName(String name) {
                    this.name = name;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public void setCountNumber(int countNumber) {
                    this.countNumber = countNumber;
                }

                public void setNeedNumber(int needNumber) {
                    this.needNumber = needNumber;
                }

                public void setCredit(int credit) {
                    this.credit = credit;
                }

                public void setPeriod(int period) {
                    this.period = period;
                }

                public void setStatus(int status) {
                    this.status = status;
                }

                public void setNowNumber(int nowNumber) {
                    this.nowNumber = nowNumber;
                }

                public String getName() {
                    return name;
                }

                public String getType() {
                    return type;
                }

                public int getCountNumber() {
                    return countNumber;
                }

                public int getNeedNumber() {
                    return needNumber;
                }

                public int getCredit() {
                    return credit;
                }

                public int getPeriod() {
                    return period;
                }

                public int getStatus() {
                    return status;
                }

                public int getNowNumber() {
                    return nowNumber;
                }
            }
        }

        public static class BeikaoEntity {
            private String name;
            /**
             * name : 口语练习
             * type : 备考任务
             * countNumber : 1
             * needNumber : 1
             * credit : 20
             * period : 1
             * status : 0
             * nowNumber : 0
             */

            private List<DataEntity> data;

            public void setName(String name) {
                this.name = name;
            }

            public void setData(List<DataEntity> data) {
                this.data = data;
            }

            public String getName() {
                return name;
            }

            public List<DataEntity> getData() {
                return data;
            }

            public static class DataEntity {
                private String name;
                private String type;
                private int countNumber;
                private int needNumber;
                private int credit;
                private int period;
                private int status;
                private int nowNumber;

                public void setName(String name) {
                    this.name = name;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public void setCountNumber(int countNumber) {
                    this.countNumber = countNumber;
                }

                public void setNeedNumber(int needNumber) {
                    this.needNumber = needNumber;
                }

                public void setCredit(int credit) {
                    this.credit = credit;
                }

                public void setPeriod(int period) {
                    this.period = period;
                }

                public void setStatus(int status) {
                    this.status = status;
                }

                public void setNowNumber(int nowNumber) {
                    this.nowNumber = nowNumber;
                }

                public String getName() {
                    return name;
                }

                public String getType() {
                    return type;
                }

                public int getCountNumber() {
                    return countNumber;
                }

                public int getNeedNumber() {
                    return needNumber;
                }

                public int getCredit() {
                    return credit;
                }

                public int getPeriod() {
                    return period;
                }

                public int getStatus() {
                    return status;
                }

                public int getNowNumber() {
                    return nowNumber;
                }
            }
        }

        public static class OtherEntity {
            private String name;
            /**
             * name : 累积5个好友
             * type : 其他任务
             * countNumber : 5
             * needNumber : 5
             * credit : 50
             * period : 4
             * status : 0
             * nowNumber : 1
             * stage : 1
             * lastTime : 1446715878
             */

            private List<DataEntity> data;

            public void setName(String name) {
                this.name = name;
            }

            public void setData(List<DataEntity> data) {
                this.data = data;
            }

            public String getName() {
                return name;
            }

            public List<DataEntity> getData() {
                return data;
            }

            public static class DataEntity {
                private String name;
                private String type;
                private int countNumber;
                private int needNumber;
                private int credit;
                private int period;
                private int status;
                private int nowNumber;
                private int stage;
                private int lastTime;

                public void setName(String name) {
                    this.name = name;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public void setCountNumber(int countNumber) {
                    this.countNumber = countNumber;
                }

                public void setNeedNumber(int needNumber) {
                    this.needNumber = needNumber;
                }

                public void setCredit(int credit) {
                    this.credit = credit;
                }

                public void setPeriod(int period) {
                    this.period = period;
                }

                public void setStatus(int status) {
                    this.status = status;
                }

                public void setNowNumber(int nowNumber) {
                    this.nowNumber = nowNumber;
                }

                public void setStage(int stage) {
                    this.stage = stage;
                }

                public void setLastTime(int lastTime) {
                    this.lastTime = lastTime;
                }

                public String getName() {
                    return name;
                }

                public String getType() {
                    return type;
                }

                public int getCountNumber() {
                    return countNumber;
                }

                public int getNeedNumber() {
                    return needNumber;
                }

                public int getCredit() {
                    return credit;
                }

                public int getPeriod() {
                    return period;
                }

                public int getStatus() {
                    return status;
                }

                public int getNowNumber() {
                    return nowNumber;
                }

                public int getStage() {
                    return stage;
                }

                public int getLastTime() {
                    return lastTime;
                }
            }
        }
    }
}

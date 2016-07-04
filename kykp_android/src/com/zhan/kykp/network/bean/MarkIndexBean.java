package com.zhan.kykp.network.bean;


public class MarkIndexBean extends BaseBean{

    private DatasEntity datas;

    public void setDatas(DatasEntity datas) {
        this.datas = datas;
    }

    public DatasEntity getDatas() {
        return datas;
    }

    public static class DatasEntity {
        /**
         * topicDate : 2015\/09\/16
         * updatedAt : 2015-09-16T01:55:16.851Z
         * createdAt : 2015-09-06T12:18:24.009Z
         * answer : {"praise":0,"updatedAt":"2015-09-16T01:55:16.673Z","mark":0,"objectId":"55f8cc0460b2f07ab3d55d32","markerCount":0,"createdAt":1442339716,"recordTime":38,"ispraise":0,"audio":"http:\\/\\/reference.tpooo.net\\/upload\\/rec\\/2015\\/09\\/16\\/55f8cc043c9e2.amr","nickname":"\\u4e94\\u6708","avatar":"http:\\/\\/acjizsrvn7.clouddn.co\\/Yk21UTje0j8mRiJ3leROSydPDfPFDPQ2eNfAquR1.jpg","userId":"55e5629600b04a63ffff630f","isteacher":0}
         * question : "Some people prefer to spend spare time by themselves, while others prefer to spend it with family members. Which do you prefer?"
         * objectId : 55ec2f10e4b0152a6111eccc
         */

        private String topicDate;
        private String updatedAt;
        private String createdAt;
        private AnswerEntity answer;
        private String question;
        private String objectId;

        public void setTopicDate(String topicDate) {
            this.topicDate = topicDate;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public void setAnswer(AnswerEntity answer) {
            this.answer = answer;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public String getTopicDate() {
            return topicDate;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public AnswerEntity getAnswer() {
            return answer;
        }

        public String getQuestion() {
            return question;
        }

        public String getObjectId() {
            return objectId;
        }

        public static class AnswerEntity {
            /**
             * praise : 0
             * updatedAt : 2015-09-16T01:55:16.673Z
             * mark : 0
             * objectId : 55f8cc0460b2f07ab3d55d32
             * markerCount : 0
             * createdAt : 1442339716
             * recordTime : 38
             * ispraise : 0
             * audio : http:\/\/reference.tpooo.net\/upload\/rec\/2015\/09\/16\/55f8cc043c9e2.amr
             * nickname : \u4e94\u6708
             * avatar : http:\/\/acjizsrvn7.clouddn.co\/Yk21UTje0j8mRiJ3leROSydPDfPFDPQ2eNfAquR1.jpg
             * userId : 55e5629600b04a63ffff630f
             * isteacher : 0
             */

            private int praise;
            private String updatedAt;
            private float mark;
            private String objectId;
            private int markerCount;
            private int createdAt;
            private int recordTime;
            private int ispraise;
            private String audio;
            private String nickname;
            private String avatar;
            private String userId;
            private int isteacher;

            public void setPraise(int praise) {
                this.praise = praise;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

            public void setMark(float mark) {
                this.mark = mark;
            }

            public void setObjectId(String objectId) {
                this.objectId = objectId;
            }

            public void setMarkerCount(int markerCount) {
                this.markerCount = markerCount;
            }

            public void setCreatedAt(int createdAt) {
                this.createdAt = createdAt;
            }

            public void setRecordTime(int recordTime) {
                this.recordTime = recordTime;
            }

            public void setIspraise(int ispraise) {
                this.ispraise = ispraise;
            }

            public void setAudio(String audio) {
                this.audio = audio;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public void setIsteacher(int isteacher) {
                this.isteacher = isteacher;
            }

            public int getPraise() {
                return praise;
            }

            public String getUpdatedAt() {
                return updatedAt;
            }

            public float getMark() {
                return mark;
            }

            public String getObjectId() {
                return objectId;
            }

            public int getMarkerCount() {
                return markerCount;
            }

            public int getCreatedAt() {
                return createdAt;
            }

            public int getRecordTime() {
                return recordTime;
            }

            public int getIspraise() {
                return ispraise;
            }

            public String getAudio() {
                return audio;
            }

            public String getNickname() {
                return nickname;
            }

            public String getAvatar() {
                return avatar;
            }

            public String getUserId() {
                return userId;
            }

            public int getIsteacher() {
                return isteacher;
            }
        }
    }
}

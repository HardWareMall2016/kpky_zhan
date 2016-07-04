package com.zhan.kykp.network.bean;

import java.util.List;

public class TopicIndexBean {


    /**
     * status : 1
     * message :
     * datas : {"updatedAt":"2015-09-14T08:01:54.621Z","uid":9,"createdAt":"2015-09-06T12:18:24.001Z","answer":"\"I'd like to talk about reading club. There are some reasons for my like. Firstly, it's an activity to increase my knowledge base. I like literature, especially those related to history. Therefore, when I'm focusing on these books, I'm not only working on literature, but history, which is necessary in better comprehension of our nation. Moreover, it's an easy access to new friends. Take my best friend Tim for example. We met in reading club, and as we share the same interest, we soon became good friends.\"","question":"Talk about an after class activity you enjoy doing the most.","objectId":"55ec2f10e4b0152a6111eccb","spAnswer":[{"praise":0,"mark":0,"objectId":"55f150a460b27db439f14948","markerCount":0,"createdAt":"2015-09-10T09:43:00.214Z","ismake":0,"ispraise":0,"isfollowe":0,"audio":"http://ac-apv0scfc.clouddn.com/S2Tgr8A0h0RqKJ9BfDtcRpA.amr","nickname":"Cailing","avatar":"","userId":"55406df5e4b0c518389de584","isteacher":1}]}
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
         * updatedAt : 2015-09-14T08:01:54.621Z
         * uid : 9
         * createdAt : 2015-09-06T12:18:24.001Z
         * answer : "I'd like to talk about reading club. There are some reasons for my like. Firstly, it's an activity to increase my knowledge base. I like literature, especially those related to history. Therefore, when I'm focusing on these books, I'm not only working on literature, but history, which is necessary in better comprehension of our nation. Moreover, it's an easy access to new friends. Take my best friend Tim for example. We met in reading club, and as we share the same interest, we soon became good friends."
         * question : Talk about an after class activity you enjoy doing the most.
         * objectId : 55ec2f10e4b0152a6111eccb
         * spAnswer : [{"praise":0,"mark":0,"objectId":"55f150a460b27db439f14948","markerCount":0,"createdAt":"2015-09-10T09:43:00.214Z","ismake":0,"ispraise":0,"isfollowe":0,"audio":"http://ac-apv0scfc.clouddn.com/S2Tgr8A0h0RqKJ9BfDtcRpA.amr","nickname":"Cailing","avatar":"","userId":"55406df5e4b0c518389de584","isteacher":1}]
         */

        private String updatedAt;
        private int uid;
        private String createdAt;
        private String answer;
        private String question;
        private String objectId;
        private List<SpAnswerEntity> spAnswer;

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setSpAnswer(List<SpAnswerEntity> spAnswer) {
            this.spAnswer = spAnswer;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public int getUid() {
            return uid;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getAnswer() {
            return answer;
        }

        public String getQuestion() {
            return question;
        }

        public String getObjectId() {
            return objectId;
        }

        public List<SpAnswerEntity> getSpAnswer() {
            return spAnswer;
        }

        public static class SpAnswerEntity {
            /**
             * praise : 0
             * mark : 0
             * objectId : 55f150a460b27db439f14948
             * markerCount : 0
             * createdAt : 2015-09-10T09:43:00.214Z
             * ismake : 0
             * ispraise : 0
             * isfollowe : 0
             * audio : http://ac-apv0scfc.clouddn.com/S2Tgr8A0h0RqKJ9BfDtcRpA.amr
             * nickname : Cailing
             * avatar :
             * userId : 55406df5e4b0c518389de584
             * isteacher : 1
             */

            private int praise;
            private float mark;
            private String objectId;
            private int markerCount;
            private long createdAt;
            private int ismake;
            private int ispraise;
            private int isfollowe;
            private String audio;
            private String nickname;
            private String avatar;
            private String userId;
            private int isteacher;
            private int recordTime;
            private String level;

            public int getRecordTime() {
				return recordTime;
			}

			public void setRecordTime(int recordTime) {
				this.recordTime = recordTime;
			}

			public void setPraise(int praise) {
                this.praise = praise;
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

            public void setCreatedAt(long createdAt) {
                this.createdAt = createdAt;
            }

            public void setIsmake(int ismake) {
                this.ismake = ismake;
            }

            public void setIspraise(int ispraise) {
                this.ispraise = ispraise;
            }

            public void setIsfollowe(int isfollowe) {
                this.isfollowe = isfollowe;
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

            public float getMark() {
                return mark;
            }

            public String getObjectId() {
                return objectId;
            }

            public int getMarkerCount() {
                return markerCount;
            }

            public long getCreatedAt() {
                return createdAt;
            }

            public int getIsmake() {
                return ismake;
            }

            public int getIspraise() {
                return ispraise;
            }

            public int getIsfollowe() {
                return isfollowe;
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

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }
        }
    }
}

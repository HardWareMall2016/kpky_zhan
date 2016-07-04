package com.zhan.kykp.network.bean;

import java.util.List;

/**
 * Created by Administrator on 2015/9/17.
 */
public class PersonCenterBean {
    /**
     * status : 1
     * message : 
     * datas : {"nickname":"可可","avatar":"http://ac-jizsrvn7.clouddn.com/l66CiWHDk5IPh4V6sFrKcWsBTgvCkvX5eqsHhrYt.jpg","follower":0,"followee":7,"objectId":"55deaedc00b0afd40405ddcb","spAnswer":[{"praise":1,"updatedAt":"2015-09-09T12:37:21.028Z","mark":2,"objectId":"55f0164060b2b52c4e474249","markerCount":1,"createdAt":1441768896,"topic":"Talk about a study habit of yours that's different from others.","recordTime":0,"ispraise":0,"audio":"http://reference.tpooo.net/upload/rec/2015/09/09/55f01640478a8.mp3"}]}
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
         * nickname : 可可
         * avatar : http://ac-jizsrvn7.clouddn.com/l66CiWHDk5IPh4V6sFrKcWsBTgvCkvX5eqsHhrYt.jpg
         * follower : 0
         * followee : 7
         * objectId : 55deaedc00b0afd40405ddcb
         * spAnswer : [{"praise":1,"updatedAt":"2015-09-09T12:37:21.028Z","mark":2,"objectId":"55f0164060b2b52c4e474249","markerCount":1,"createdAt":1441768896,"topic":"Talk about a study habit of yours that's different from others.","recordTime":0,"ispraise":0,"audio":"http://reference.tpooo.net/upload/rec/2015/09/09/55f01640478a8.mp3"}]
         */

        private String nickname;
        private String avatar;
        private int follower;
        private int followee;
        private String objectId;
        private int isfollowe ;
        private List<AllAnswerEntity> allAnswer;
        
        public List<AllAnswerEntity> getAllAnswer() {
			return allAnswer;
		}

		public void setAllAnswer(List<AllAnswerEntity> allAnswer) {
			this.allAnswer = allAnswer;
		}

		public int getIsfollowe() {
			return isfollowe;
		}

		public void setIsfollowe(int isfollowe) {
			this.isfollowe = isfollowe;
		}

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public void setFollower(int follower) {
            this.follower = follower;
        }

        public void setFollowee(int followee) {
            this.followee = followee;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }


        public String getNickname() {
            return nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public int getFollower() {
            return follower;
        }

        public int getFollowee() {
            return followee;
        }

        public String getObjectId() {
            return objectId;
        }


        public static class AllAnswerEntity {
            /**
             * praise : 1
             * updatedAt : 2015-09-09T12:37:21.028Z
             * mark : 2
             * objectId : 55f0164060b2b52c4e474249
             * markerCount : 1
             * createdAt : 1441768896
             * topic : Talk about a study habit of yours that's different from others.
             * recordTime : 0
             * ispraise : 0
             * audio : http://reference.tpooo.net/upload/rec/2015/09/09/55f01640478a8.mp3
             */

            private int praise;
            private String updatedAt;
            private float mark;
            private String objectId;
            private int markerCount;
            private int createdAt;
            private String topic;
            private int recordTime;
            private int ispraise;
            private String audio;

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

            public void setTopic(String topic) {
                this.topic = topic;
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

            public String getTopic() {
                return topic;
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
        }
    }
}

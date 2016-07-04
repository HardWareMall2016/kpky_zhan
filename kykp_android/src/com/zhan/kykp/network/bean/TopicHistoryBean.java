package com.zhan.kykp.network.bean;

import java.util.List;



public class TopicHistoryBean {

    /**
     * status : 1
     * message :
     * datas : [{"topicDate":"2015/09/14","updatedAt":"2015-09-14T08:01:54.621Z","createdAt":1441513104,"question":"Talk about an after class activity you enjoy doing the most.","objectId":"55ec2f10e4b0152a6111eccb","answerCount":0},{"topicDate":"2015/09/11","updatedAt":"2015-09-11T03:16:13.238Z","createdAt":1441513103,"question":"Talk about a type of clothing that is popular in your country.","objectId":"55ec2f0fe4b0152a6111ecc9","answerCount":0},{"topicDate":"2015/09/10","updatedAt":"2015-09-14T11:06:48.043Z","createdAt":1441513103,"question":"Talk about a study habit of yours that's different from others.","objectId":"55ec2f0fe4b0152a6111ecc3","answerCount":9},{"topicDate":"2015/09/09","updatedAt":"2015-09-10T12:18:05.502Z","createdAt":1441513103,"question":"\"Which of the following transportation methods do you think is the most enjoyable, bicycle, automobile or train?\"","objectId":"55ec2f0fe4b0152a6111ecc7","answerCount":5},{"topicDate":"2015/09/08","updatedAt":"2015-09-10T12:18:00.091Z","createdAt":1441513103,"question":"Some teachers prefer students to send questions about course work or assignments by email. Others prefer students to ask the questions in person. Which do you prefer?","objectId":"55ec2f0fe4b0152a6111ecc6","answerCount":4},{"topicDate":"2015/09/07","updatedAt":"2015-09-10T12:17:56.353Z","createdAt":1441513103,"question":"Your friend is concerned about a presentation that he has to give to the class. What advice would you give to help your friend to prepare for the presentation?","objectId":"55ec2f0fe4b0152a6111ecc5","answerCount":3},{"topicDate":"2015/09/06","updatedAt":"2015-09-10T12:17:52.377Z","createdAt":1441513103,"question":"\"Some people prefer to shop in big shopping malls, while other prefer smaller shops. Which do you prefer?                  \"","objectId":"55ec2f0fe4b0152a6111ecc4","answerCount":2},{"topicDate":"2015/09/05","updatedAt":"2015-09-10T12:20:06.936Z","createdAt":1441513103,"question":"Do you agree or disagree that people will read less in the future?","objectId":"55ec2f0fe4b0152a6111ecc8","answerCount":6}]
     */

    private int status;
    private String message;
    private List<DatasEntity> datas;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDatas(List<DatasEntity> datas) {
        this.datas = datas;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<DatasEntity> getDatas() {
        return datas;
    }

    public static class DatasEntity {
        /**
         * topicDate : 2015/09/14
         * updatedAt : 2015-09-14T08:01:54.621Z
         * createdAt : 1441513104
         * question : Talk about an after class activity you enjoy doing the most.
         * objectId : 55ec2f10e4b0152a6111eccb
         * answerCount : 0
         */

        private String topicDate;
        private String updatedAt;
        private int createdAt;
        private String question;
        private String objectId;
        private int answerCount;
        private String topic ;

        public String getTopic() {
			return topic;
		}

		public void setTopic(String topic) {
			this.topic = topic;
		}

		public void setTopicDate(String topicDate) {
            this.topicDate = topicDate;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setCreatedAt(int createdAt) {
            this.createdAt = createdAt;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setAnswerCount(int answerCount) {
            this.answerCount = answerCount;
        }

        public String getTopicDate() {
            return topicDate;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public int getCreatedAt() {
            return createdAt;
        }

        public String getQuestion() {
            return question;
        }

        public String getObjectId() {
            return objectId;
        }

        public int getAnswerCount() {
            return answerCount;
        }
    }
}

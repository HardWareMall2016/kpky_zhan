package com.zhan.kykp.network.bean;


import java.util.List;
/**
 * Created by Administrator on 2015/9/17.
 */
public class PersonCenterRefreshBean {
    /**
     * status : 1
     * message : 
     * datas : [{"praise":1,"updatedAt":"2015-09-17T09:56:35.509Z","mark":5,"objectId":"55fa7c9e60b20bbff6231112","markerCount":1,"createdAt":1442450462,"topic":"Talk about a volunteer work you enjoy doing.","recordTime":38,"ispraise":0,"audio":"http://reference.tpooo.net/upload/rec/2015/09/17/55fa7c9e742d3.amr"},{"praise":0,"updatedAt":"2015-09-16T08:31:47.964Z","mark":4,"objectId":"55f92830ddb2dd0026719dc1","markerCount":1,"createdAt":1442363312,"topic":"\"Some people prefer to spend spare time by themselves, while others prefer to spend it with family members. Which do you prefer?\"","recordTime":38,"ispraise":0,"audio":"http://reference.tpooo.net/upload/rec/2015/09/16/55f9283024e43.amr"},{"praise":1,"updatedAt":"2015-09-16T10:18:36.571Z","mark":4,"objectId":"55f919c4ddb2e44a48062c39","markerCount":3,"createdAt":1442359620,"topic":"\"Some people prefer to spend spare time by themselves, while others prefer to spend it with family members. Which do you prefer?\"","recordTime":45,"ispraise":0,"audio":"http://reference.tpooo.net/upload/rec/2015/09/16/55f919c3bf0f8.amr"},{"praise":2,"updatedAt":"2015-09-16T10:20:15.976Z","mark":4,"objectId":"55f8cc0460b2f07ab3d55d32","markerCount":3,"createdAt":1442339716,"topic":"\"Some people prefer to spend spare time by themselves, while others prefer to spend it with family members. Which do you prefer?\"","recordTime":38,"ispraise":0,"audio":"http://reference.tpooo.net/upload/rec/2015/09/16/55f8cc043c9e2.amr"},{"praise":3,"updatedAt":"2015-09-17T08:49:53.506Z","mark":4,"objectId":"55fa1dd860b2f07ab407e3df","markerCount":2,"createdAt":1442426200,"topic":"Talk about a volunteer work you enjoy doing.","recordTime":45,"ispraise":0,"audio":"http://reference.tpooo.net/upload/rec/2015/09/17/55fa1dd7c5958.amr"},{"praise":9,"updatedAt":"2015-09-15T11:27:58.868Z","mark":3,"objectId":"55f0163360b2b52c4e473fa3","markerCount":2,"createdAt":1441768883,"topic":"Talk about a study habit of yours that's different from others.","recordTime":0,"ispraise":0,"audio":"http://reference.tpooo.net/upload/rec/2015/09/09/55f0163356fc6.mp3"},{"praise":1,"updatedAt":"2015-09-15T09:31:13.429Z","mark":0,"objectId":"55f7d0a000b09b53314de2be","markerCount":0,"createdAt":1442275360,"topic":"Do you agree or disagree that university students should take part-time jobs?","recordTime":36,"ispraise":0,"audio":"http://reference.tpooo.net/upload/rec/2015/09/15/55f7d0a0166e9.amr"},{"praise":1,"updatedAt":"2015-09-15T10:48:58.634Z","mark":0,"objectId":"55f7f69e60b27db44c1aa035","markerCount":0,"createdAt":1442285086,"topic":"Do you agree or disagree that university students should take part-time jobs?","recordTime":40,"ispraise":0,"audio":"http://reference.tpooo.net/upload/rec/2015/09/15/55f7f69e71b48.amr"},{"praise":0,"updatedAt":"2015-09-16T09:38:47.351Z","mark":0,"objectId":"55f938a760b2f07ab3e5d748","markerCount":0,"createdAt":1442367527,"topic":"\"Some people prefer to spend spare time by themselves, while others prefer to spend it with family members. Which do you prefer?\"","recordTime":45,"ispraise":0,"audio":"http://reference.tpooo.net/upload/rec/2015/09/16/55f938a705d98.amr"},{"praise":0,"updatedAt":"2015-09-16T10:39:50.991Z","mark":0,"objectId":"55f946f660b2aea21832af57","markerCount":0,"createdAt":1442371190,"topic":"\"Some people prefer to spend spare time by themselves, while others prefer to spend it with family members. Which do you prefer?\"","recordTime":36,"ispraise":0,"audio":"http://reference.tpooo.net/upload/rec/2015/09/16/55f946f6933ba.amr"}]
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
         * praise : 1
         * updatedAt : 2015-09-17T09:56:35.509Z
         * mark : 5
         * objectId : 55fa7c9e60b20bbff6231112
         * markerCount : 1
         * createdAt : 1442450462
         * topic : Talk about a volunteer work you enjoy doing.
         * recordTime : 38
         * ispraise : 0
         * audio : http://reference.tpooo.net/upload/rec/2015/09/17/55fa7c9e742d3.amr
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

        public void setMark(int mark) {
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

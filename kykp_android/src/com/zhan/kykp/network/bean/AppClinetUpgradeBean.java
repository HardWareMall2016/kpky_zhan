package com.zhan.kykp.network.bean;

/**
 * Created by WuYue on 2015/10/28.
 */
public class AppClinetUpgradeBean extends BaseBean {

    /**
     * datas : {"_id":"562f574016c07bc0af00002d","name":"小站备考","version":2.6,"type":"ios","url":"2.6url","introduce":"2.6版本介绍","forcedUpdate":1}
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
         * _id : 562f574016c07bc0af00002d
         * name : 小站备考
         * version : 2.6
         * type : ios
         * url : 2.6url
         * introduce : 2.6版本介绍
         * forcedUpdate : 1
         */

        private String _id;
        private String name;
        private double version;
        private String type;
        private String url;
        private String introduce;
        private int forcedUpdate;

        public void set_id(String _id) {
            this._id = _id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setVersion(double version) {
            this.version = version;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public void setForcedUpdate(int forcedUpdate) {
            this.forcedUpdate = forcedUpdate;
        }

        public String get_id() {
            return _id;
        }

        public String getName() {
            return name;
        }

        public double getVersion() {
            return version;
        }

        public String getType() {
            return type;
        }

        public String getUrl() {
            return url;
        }

        public String getIntroduce() {
            return introduce;
        }

        public int getForcedUpdate() {
            return forcedUpdate;
        }
    }
}

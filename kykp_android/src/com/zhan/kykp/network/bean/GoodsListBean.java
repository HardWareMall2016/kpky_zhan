package com.zhan.kykp.network.bean;

import java.util.List;

/**
 * Created by WuYue on 2015/10/29.
 */
public class GoodsListBean extends BaseBean {

    /**
     * datas : [{"_id":"5653beccb0e265ec524c80ca","title":"测试商品地球通电话卡","image":"http://bbsapp.static.tpooo.net/2015-11-24/5653bec416223.jpg","realnum":89,"scholarship":"30000","createdAt":1448300108,"updatedAt":1448300108},{"_id":"5652bb1bb0e265ec524c80c9","title":"第三个发动反攻","image":"http://bbsapp.static.tpooo.net/2015-10-28/563090588f4d5.png","realnum":2,"scholarship":"2","createdAt":1448233627,"updatedAt":1448233638}]
     */

    private List<DatasEntity> datas;

    public void setDatas(List<DatasEntity> datas) {
        this.datas = datas;
    }

    public List<DatasEntity> getDatas() {
        return datas;
    }

    public static class DatasEntity {
        /**
         * _id : 5653beccb0e265ec524c80ca
         * title : 测试商品地球通电话卡
         * image : http://bbsapp.static.tpooo.net/2015-11-24/5653bec416223.jpg
         * realnum : 89
         * scholarship : 30000
         * createdAt : 1448300108
         * updatedAt : 1448300108
         */

        private String _id;
        private String title;
        private String image;
        private int realnum;
        private String scholarship;
        private int createdAt;
        private int updatedAt;

        public void set_id(String _id) {
            this._id = _id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setRealnum(int realnum) {
            this.realnum = realnum;
        }

        public void setScholarship(String scholarship) {
            this.scholarship = scholarship;
        }

        public void setCreatedAt(int createdAt) {
            this.createdAt = createdAt;
        }

        public void setUpdatedAt(int updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String get_id() {
            return _id;
        }

        public String getTitle() {
            return title;
        }

        public String getImage() {
            return image;
        }

        public int getRealnum() {
            return realnum;
        }

        public String getScholarship() {
            return scholarship;
        }

        public int getCreatedAt() {
            return createdAt;
        }

        public int getUpdatedAt() {
            return updatedAt;
        }
    }
}

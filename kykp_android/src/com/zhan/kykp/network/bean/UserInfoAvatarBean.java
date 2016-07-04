package com.zhan.kykp.network.bean;

/**
 * Created by Administrator on 2015/10/21.
 */
public class UserInfoAvatarBean {
    /**
     * status : 1
     * message : 头像修改成功
     * datas : {"avatar":"http://reference.tpooo.net/upload/avatar/2015/10/21/562755450952b.png"}
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
         * avatar : http://reference.tpooo.net/upload/avatar/2015/10/21/562755450952b.png
         */

        private String avatar;

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAvatar() {
            return avatar;
        }
    }
}

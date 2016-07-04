package com.zhan.kykp.network.bean;

public class BaseBean {
    private int status;
    private String message;
    
    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

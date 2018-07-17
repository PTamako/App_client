package com.example.lenovo.test.model;

import java.io.Serializable;

public class BaseResponse implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 5686586181106869281L;

    /**
     * 返回状态码
     */
    private int status;

    /**
     * 返回消息
     */
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

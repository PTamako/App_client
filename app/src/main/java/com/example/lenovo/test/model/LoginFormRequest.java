package com.example.lenovo.test.model;

/**
 * 前端(手机)传递给服务器端的请求
 */
public class LoginFormRequest extends BaseRequest {

    /**
     *
     */
    private static final long serialVersionUID = -5572150229327282617L;

    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
package com.example.lenovo.test.model;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable{
    private int id;
    private String storeName;
    private String userName;
    private int grade;
    private String review;
    private Date  currentDate;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getStoreName() {
        return storeName;
    }
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public int getGrade() {
        return grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }
    public String getReview() {
        return review;
    }
    public void setReview(String review) {
        this.review = review;
    }
    public Date getCurrentDate() {
        return currentDate;
    }
    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

}

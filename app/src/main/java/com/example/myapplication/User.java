package com.example.myapplication;

public class User {
    private String buyerseller;
    private String email;
    private String gender;
    private String id;
    private String password;
    private String phone;
    private String username;

    public User() {}

    public User(String buyerseller, String email, String gender, String id, String password, String phone, String username) {
        this.buyerseller = buyerseller;
        this.email = email;
        this.gender = gender;
        this.id = id;
        this.password = password;
        this.phone = phone;
        this.username = username;
    }

    public String getBuyerseller() {
        return buyerseller;
    }

    public void setBuyerseller(String buyerseller) {
        this.buyerseller = buyerseller;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
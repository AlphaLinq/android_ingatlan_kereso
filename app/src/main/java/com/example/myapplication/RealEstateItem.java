package com.example.myapplication;

public class RealEstateItem {
    private String baseArea;
    private String description;
    private String price;
    private String rooms;

    private String phone;
    private int imageUrl;

    public RealEstateItem(String title, String description, String price, String rooms,String phone, int imageUrl) {
        this.baseArea = title;
        this.description = description;
        this.price = price;
        this.rooms = rooms;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }

    public RealEstateItem(){}

    public String getPhoneNumber() {
        return phone;
    }

    public String getBaseArea() {
        return baseArea;
    }

    public void setSqr_meter(String sqr_meter) {
        this.baseArea = sqr_meter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public int getImageUrl() {
        return imageUrl;
    }

}

package com.example.myapplication;

public class RealEstateItem {

    private String id;
    private String address;
    private String baseArea;
    private String description;
    private String price;
    private String rooms;
    private String phoneNumber;
    private String imageUrl;

    public RealEstateItem(String title,String baseArea, String description, String price, String rooms,String phone, String imageUrl) {
        this.address = title;
        this.baseArea = baseArea;
        this.description = description;
        this.price = price;
        this.rooms = rooms;
        this.phoneNumber = phone;
        this.imageUrl = imageUrl;
    }
    public String getAddress() {
        return address;
    }

    public RealEstateItem(){}

    public String getPhoneNumber() {
        return phoneNumber;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public String _getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

}

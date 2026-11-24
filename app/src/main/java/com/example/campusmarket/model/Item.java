package com.example.campusmarket.model;

import java.io.Serializable;

public class Item implements Serializable {

    private String id;
    private String title;
    private String category;
    private String condition;
    private String description;
    private String sellerEmail;
    private String sellerPhone;
    private String imageUrl;
    private double price;
    private boolean sold;
    private long createdAt;

    // Required empty constructor
    public Item() {
    }

    public Item(String title, String category, String condition, String description,
                String sellerEmail, String sellerPhone, double price) {
        this(title, category, condition, description, sellerEmail, sellerPhone, null, price);
    }

    public Item(String title, String category, String condition, String description,
                String sellerEmail, String sellerPhone, String imageUrl, double price) {
        this.title = title;
        this.category = category;
        this.condition = condition;
        this.description = description;
        this.sellerEmail = sellerEmail;
        this.sellerPhone = sellerPhone;
        this.imageUrl = imageUrl;
        this.price = price;
        this.sold = false;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}

package com.example.appsriv_02.loginwithfacebook;

public class Child {

    private String Brand;
    private String price;
    public String minPrice;

    public void setBrand(String brand) {
        Brand = brand;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBrand() {
        return Brand;
    }

    public String getPrice() {
        return price;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMinPrice() {
        return minPrice;
    }
}
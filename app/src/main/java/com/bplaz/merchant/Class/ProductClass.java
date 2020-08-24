package com.bplaz.merchant.Class;

public class ProductClass {

    private String id;
    private String brand;
    private String product_name;
    private String description;
    private String image_product;
    private String manufacturer;
    private String category;
    private String category_id;
    private String service;
    private String availability;
    private String price;
    private String status;
    private String rsp_price;

    public ProductClass(String id, String brand, String product_name,String description,String image_product,
                        String manufacturer, String category, String category_id,String service,String availability,
                        String price, String status, String rsp_price){
        this.id = id;
        this.brand = brand;
        this.product_name = product_name;
        this.description = description;
        this.image_product = image_product;
        this.manufacturer = manufacturer;
        this.category = category;
        this.category_id = category_id;
        this.service = service;
        this.availability = availability;
        this.price = price;
        this.status = status;
        this.rsp_price = rsp_price;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getImage_product() {
        return image_product;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getStatus() {
        return status;
    }

    public String getPrice() {
        return price;
    }

    public String getAvailability() {
        return availability;
    }

    public String getRsp_price() {
        return rsp_price;
    }

    public String getService() {
        return service;
    }
}

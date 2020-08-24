package com.bplaz.merchant.Class;

public class ToAcceptClass {

    private String id;
    private String tenant_id;
    private String job_date;
    private String latitude;
    private String longitude;
    private String geo_location;
    private String category_id;
    private String status;
    private String price;
    private String rider_name;
    private String sale_id;
    private String sale_partner_item_id;

    public ToAcceptClass(String id,String tenant_id, String job_date, String latitude, String longitude, String geo_location, String category_id
            , String status,String price,String rider_name,String sale_id, String sale_partner_item_id){
        this.id = id;
        this.tenant_id = tenant_id;
        this.job_date = job_date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geo_location = geo_location;
        this.category_id = category_id;
        this.status = status;
        this.price = price;
        this.rider_name = rider_name;
        this.sale_id = sale_id;
        this.sale_partner_item_id = sale_partner_item_id;
    }

    public String getSale_partner_item_id() {
        return sale_partner_item_id;
    }

    public String getSale_id() {
        return sale_id;
    }

    public String getRider_name() {
        return rider_name;
    }

    public String getPrice() {
        return price;
    }

    public String getTenant_id() {
        return tenant_id;
    }

    public String getId() {
        return id;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getStatus() {
        return status;
    }

    public String getGeo_location() {
        return geo_location;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getJob_date() {
        return job_date;
    }

}

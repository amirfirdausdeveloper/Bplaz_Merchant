package com.bplaz.merchant.Class;

public class RiderAssignClass {

    private String id;
    private String name;
    private String email;
    private String telephone_number;
    private String distance;

    public RiderAssignClass(String id, String name, String email, String telephone_number, String distance){
        this.id = id;
        this.name = name;
        this.email = email;
        this.telephone_number = telephone_number;
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone_number() {
        return telephone_number;
    }

}

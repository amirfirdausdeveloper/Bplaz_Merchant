package com.bplaz.merchant.Class;

public class RiderClass {

    private String id;
    private String name;
    private String email;
    private String telephone_number;

    public RiderClass(String id, String name, String email, String telephone_number){
        this.id = id;
        this.name = name;
        this.email = email;
        this.telephone_number = telephone_number;
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

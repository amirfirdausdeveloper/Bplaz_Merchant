package com.bateriku.bplazmerchant.Class;

public class RiderClass {

    private String id;
    private String name;
    private String email;
    private String telephone_number;
    private String staff_status;
    private String date_of_birth;
    private String current_location;

    public RiderClass(String id, String name, String email, String telephone_number, String staff_status
            , String date_of_birth, String current_location){
        this.id = id;
        this.name = name;
        this.email = email;
        this.telephone_number = telephone_number;
        this.staff_status = staff_status;
        this.date_of_birth = date_of_birth;
        this.current_location = current_location;
    }

    public String getCurrent_location() {
        return current_location;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public String getStaff_status() {
        return staff_status;
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

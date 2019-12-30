package com.example.wonkaworkers;

public class OompaBasicInfo {

    private String first_name, last_name, profession, gender, image;
    private int id;

    public OompaBasicInfo(int id, String first_name,String last_name, String profession, String gender, String image) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.profession = profession;
        this.gender = gender;
        this.image = image;
    }


    public int getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getProfession() {
        return profession;
    }

    public String getGender() {
        return gender;
    }

    public String getImage() {
        return image;
    }

}

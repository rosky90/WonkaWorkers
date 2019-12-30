package com.example.wonkaworkers;

public class Favorite {

    private String color, food, random_string, song;

    public Favorite(String color, String food, String random_string, String song) {
        this.color = color;
        this.food = food;
        this.random_string = random_string;
        this.song = song;
    }


    public String getColor() {
        return color;
    }

    public String getFood() {
        return food;
    }

    public String getRandom_string() {
        return random_string;
    }

    public String getSong() {
        return song;
    }

}

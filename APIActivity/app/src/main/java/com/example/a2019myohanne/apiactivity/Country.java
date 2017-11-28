package com.example.a2019myohanne.apiactivity;

public class Country {
    private String name;
    private double lat;
    private double lon;
    private String initial;
    public Country(String i, String n, double la, double lo){
        name=n;
        lat=la;
        lon=lo;
        initial=i;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitial() {
        return initial;
    }

    public String getName() {
        return name;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}

package com.example.tur_manager;

public class State {

    public static Double all;
    private String origin;
    private String destination;
    private double cost;
    private String link = "https://www.aviasales.ru";

    public State(String origin, String destination, double cost, String link){
        this.origin = origin;
        this.destination = destination;
        this.cost = cost;
        this.link += link;
    }
    public String getLink() {
        return this.link;
    }

    public String getOrigin() {
        return this.origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getCost() {
        return this.cost;
    }
}
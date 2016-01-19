package net.sightwalk.Models;

public class Activities {

    public int id;
    public String name;
    public int distance;
    public String starttijd;
    public String eindtijd;
    public String json;

    public Activities(int id, String name, int distance, String starttijd, String eindtijd, String json) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.starttijd = starttijd;
        this.eindtijd = eindtijd;
        this.json = json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getStarttijd() {
        return starttijd;
    }

    public void setStarttijd(String starttijd) {
        this.starttijd = starttijd;
    }

    public String getEindtijd() {
        return eindtijd;
    }

    public void setEindtijd(String eindtijd) {
        this.eindtijd = eindtijd;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
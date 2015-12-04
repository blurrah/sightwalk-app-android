package net.sightwalk.Models;

public class Steps {

    public Steps() {}

    private String html_instructions;
    private String duration;
    private String distance;
    //private String end_location;
    //private String polyline;
    //private String start_location;
    //private String travel_mode;

    public String getHtml_instructions() {
        return html_instructions;
    }

    public void setHtml_instructions(String html_instructions) {
        this.html_instructions = html_instructions;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
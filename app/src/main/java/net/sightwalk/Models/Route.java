package net.sightwalk.Models;

import java.util.Date;

/**
 * Created by Ruben on 07/01/2016.
 */
public class Route {

    public String name;
    public Integer distance;
    public Date startDate;
    public Date endDate;
    public String routeJson;

    public Route(String name, Integer distance, Date startDate, Date endDate, String routeJson){
        this.name = name;
        this.distance = distance;
        this.startDate = startDate;
        this.endDate = endDate;
        this.routeJson = routeJson;
    }

    public String calculateTimeDifference(Date startDate, Date endDate){

        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;

        return elapsedHours+" uur"+ elapsedMinutes +" min.";
    }
}

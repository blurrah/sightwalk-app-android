package net.sightwalk.Models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ruben on 07/01/2016.
 */
public class Route {

    public Integer id;
    public String name;
    public Integer distance;
    public Date startDate;
    public Date endDate;
    public String routeJson;
    public ArrayList<Sight> sights;

    public Route(String name, Integer distance, Date startDate, Date endDate, String routeJson, ArrayList<Sight> sights){
        this.name = name;
        this.distance = distance;
        this.startDate = startDate;
        this.endDate = endDate;
        this.routeJson = routeJson;
        this.sights = sights;
    }

    public Route(Integer id, String name, Integer distance, Date startDate, Date endDate, String routeJson, ArrayList<Sight> sights){
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.startDate = startDate;
        this.endDate = endDate;
        this.routeJson = routeJson;
        this.sights = sights;
    }
}

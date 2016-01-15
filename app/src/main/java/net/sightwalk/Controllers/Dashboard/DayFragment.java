package net.sightwalk.Controllers.Dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import net.danlew.android.joda.JodaTimeAndroid;
import net.sightwalk.Models.Activities;
import net.sightwalk.R;
import net.sightwalk.Stores.ActivitiesStore;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DayFragment extends Fragment {

    private BarChart chart;

    private ActivitiesStore store;

    private int valuesOne;
    private int valuesTwo;
    private int valuesThree;
    private int valuesFour;
    private int valuesFive;
    private int valuesSix;
    private int valuesSeven;

    private ArrayList<BarEntry> entries;
    private ArrayList<String> labels;

    private ArrayList<Integer> valueOne = new ArrayList<>();
    private ArrayList<Integer> valueTwo = new ArrayList<>();
    private ArrayList<Integer> valueThree = new ArrayList<>();
    private ArrayList<Integer> valueFour = new ArrayList<>();
    private ArrayList<Integer> valueFive = new ArrayList<>();
    private ArrayList<Integer> valueSix = new ArrayList<>();
    private ArrayList<Integer> valueSeven = new ArrayList<>();

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_day, container, false);

        JodaTimeAndroid.init(getActivity());

        store = new ActivitiesStore(getContext());

        for(Activities activities : store.getAll()) {
            checkDate(activities);
        }

        setValues();

        chart = (BarChart) view.findViewById(R.id.dayChart);

        setupChart();

        return view;
    }

    private void checkDate(Activities activities){
        LocalDate localDate = new LocalDate();

        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date day = null;

        try {
            day = dtf.parse(activities.getStarttijd());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String date = format.format(day);


        String dateOne = localDate.toString("dd/MM/yyyy");
        String dateTwo = localDate.minusDays(1).toString("dd/MM/yyyy");
        String dateThree = localDate.minusDays(2).toString("dd/MM/yyyy");
        String dateFour = localDate.minusDays(3).toString("dd/MM/yyyy");
        String dateFive = localDate.minusDays(4).toString("dd/MM/yyyy");
        String dateSix = localDate.minusDays(5).toString("dd/MM/yyyy");
        String dateSeven = localDate.minusDays(6).toString("dd/MM/yyyy");


        if(date.equals(dateOne)) {
            valueOne.add(getDistance(activities.getJson()));
        }
        else if(date.equals(dateTwo)) {
            valueTwo.add(getDistance(activities.getJson()));
        }
        else if(date.equals(dateThree)) {
            valueThree.add(getDistance(activities.getJson()));
        }
        else if(date.equals(dateFour)) {
            valueFour.add(getDistance(activities.getJson()));
        }
        else if(date.equals(dateFive)) {
            valueFive.add(getDistance(activities.getJson()));
        }
        else if(date.equals(dateSix)) {
            valueSix.add(getDistance(activities.getJson()));
        }
        else if(date.equals(dateSeven)) {
            valueSeven.add(getDistance(activities.getJson()));
        }
    }

    private int getDistance(String json) {
        int routedis = 0;

        try {
            JSONObject directionsObject = new JSONObject(json);

            JSONArray routesObject = directionsObject.getJSONArray("routes");

            for (int i = 0; i < routesObject.length(); i++) {
                JSONObject overview = routesObject.getJSONObject(i);

                JSONArray legsObject = overview.getJSONArray("legs");

                for (int l = 0; l < legsObject.length(); l++) {
                    JSONObject stepsobject = legsObject.getJSONObject(l);

                    JSONObject routeDistance = stepsobject.getJSONObject("distance");

                    routedis += routeDistance.getInt("value");
                }
            }
        } catch (JSONException ex) {
            Log.e("ERROR_", ex.getLocalizedMessage());
        }
        return routedis/1000;
    }

    private void setValues() {
        for(Integer value : valueOne) {
            valuesOne += value;
        }
        for(Integer value : valueTwo) {
            valuesTwo += value;
        }
        for(Integer value : valueThree) {
            valuesThree += value;
        }
        for(Integer value : valueFour) {
            valuesFour += value;
        }
        for(Integer value : valueFive) {
            valuesFive += value;
        }
        for(Integer value : valueSix) {
            valuesSix += value;
        }
        for(Integer value : valueSeven) {
            valuesSeven += value;
        }
    }

    private void setupChart() {
        chart.setDescription("");
        chart.setNoDataText("Er zijn helaas nog geen gegevens beschikbaar!");
        chart.setNoDataTextDescription("...");
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.setTouchEnabled(false);

        XAxis label = chart.getXAxis();
        label.setPosition(XAxis.XAxisPosition.BOTTOM);

        BarDataSet dataSet = new BarDataSet(getDataSet(), "Gelopen afstand in kilometers per dag");

        dataSet.setColors(new int[]{Color.parseColor("#27b764"), Color.parseColor("#a8e2c1")});
        dataSet.setDrawValues(false);
        dataSet.setHighlightEnabled(false);

        BarData data = new BarData(getLabels(), dataSet);
        chart.setData(data);
    }

    private ArrayList<BarEntry> getDataSet() {
        entries = new ArrayList<>();

        entries.add(new BarEntry(valuesSeven, 0));
        entries.add(new BarEntry(valuesSix, 1));
        entries.add(new BarEntry(valuesFive, 2));
        entries.add(new BarEntry(valuesFour, 3));
        entries.add(new BarEntry(valuesThree, 4));
        entries.add(new BarEntry(valuesTwo, 5));
        entries.add(new BarEntry(valuesOne, 6));

        return entries;
    }

    private ArrayList<String> getLabels() {
        labels = new ArrayList<>();

        labels.add(getDayName(DateTime.now().minusDays(6).getDayOfWeek()));
        labels.add(getDayName(DateTime.now().minusDays(5).getDayOfWeek()));
        labels.add(getDayName(DateTime.now().minusDays(4).getDayOfWeek()));
        labels.add(getDayName(DateTime.now().minusDays(3).getDayOfWeek()));
        labels.add(getDayName(DateTime.now().minusDays(2).getDayOfWeek()));
        labels.add(getDayName(DateTime.now().minusDays(1).getDayOfWeek()));
        labels.add(getDayName(DateTime.now().getDayOfWeek()));

        return labels;
    }

    private String getDayName(int month) {
        switch (month) {
            case 1:
                return "Ma";
            case 2:
                return "Di";
            case 3:
                return "Wo";
            case 4:
                return "Do";
            case 5:
                return "Vr";
            case 6:
                return "Za";
            case 7:
                return "Zo";
            default:
                return "";
        }
    }
}
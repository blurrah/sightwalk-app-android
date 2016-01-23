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

import java.util.ArrayList;

public class MonthFragment extends Fragment {

    private BarChart chart;

    private ActivitiesStore store;

    private int valuesOne;
    private int valuesTwo;
    private int valuesThree;
    private int valuesFour;
    private int valuesFive;
    private int valuesSix;

    private ArrayList<BarEntry> entries;
    private ArrayList<String> labels;

    private ArrayList<Integer> valueOne = new ArrayList<>();
    private ArrayList<Integer> valueTwo = new ArrayList<>();
    private ArrayList<Integer> valueThree = new ArrayList<>();
    private ArrayList<Integer> valueFour = new ArrayList<>();
    private ArrayList<Integer> valueFive = new ArrayList<>();
    private ArrayList<Integer> valueSix = new ArrayList<>();

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_month, container, false);

        JodaTimeAndroid.init(getActivity());

        store = new ActivitiesStore(getContext());

        for(Activities activities : store.getAll()) {
            checkDate(activities);
        }

        setValues();

        chart = (BarChart) view.findViewById(R.id.monthChart);

        setupChart();

        return view;
    }

    private void checkDate(Activities activities) {
        if(!activities.getStarttijd().equals(activities.eindtijd)) {
            DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
            LocalDate date = format.parseLocalDate(activities.getStarttijd());

            int month = date.getMonthOfYear();

            int dateOne = DateTime.now().getMonthOfYear();
            int dateTwo = DateTime.now().minusMonths(1).getMonthOfYear();
            int dateThree = DateTime.now().minusMonths(2).getMonthOfYear();
            int dateFour = DateTime.now().minusMonths(3).getMonthOfYear();
            int dateFive = DateTime.now().minusMonths(4).getMonthOfYear();
            int dateSix = DateTime.now().minusMonths(5).getMonthOfYear();

            if (month == dateOne) {
                valueOne.add(getDistance(activities.getJson()));
            } else if (month == dateTwo) {
                valueTwo.add(getDistance(activities.getJson()));
            } else if (month == dateThree) {
                valueThree.add(getDistance(activities.getJson()));
            } else if (month == dateFour) {
                valueFour.add(getDistance(activities.getJson()));
            } else if (month == dateFive) {
                valueFive.add(getDistance(activities.getJson()));
            } else if (month == dateSix) {
                valueSix.add(getDistance(activities.getJson()));
            }
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

        BarDataSet dataSet = new BarDataSet(getDataSet(), "Gelopen afstand in kilometers per maand");

        dataSet.setColors(new int[]{Color.parseColor("#27b764"), Color.parseColor("#a8e2c1")});
        dataSet.setDrawValues(false);
        dataSet.setHighlightEnabled(false);

        BarData data = new BarData(getLabels(), dataSet);
        chart.setData(data);
    }

    private ArrayList<BarEntry> getDataSet() {
        entries = new ArrayList<>();

        entries.add(new BarEntry(valuesSix, 0));
        entries.add(new BarEntry(valuesFive, 1));
        entries.add(new BarEntry(valuesFour, 2));
        entries.add(new BarEntry(valuesThree, 3));
        entries.add(new BarEntry(valuesTwo, 4));
        entries.add(new BarEntry(valuesOne, 5));

        return entries;
    }

    private ArrayList<String> getLabels() {
        labels = new ArrayList<>();

        labels.add(getMonthName(DateTime.now().minusMonths(5).getMonthOfYear()));
        labels.add(getMonthName(DateTime.now().minusMonths(4).getMonthOfYear()));
        labels.add(getMonthName(DateTime.now().minusMonths(3).getMonthOfYear()));
        labels.add(getMonthName(DateTime.now().minusMonths(2).getMonthOfYear()));
        labels.add(getMonthName(DateTime.now().minusMonths(1).getMonthOfYear()));
        labels.add(getMonthName(DateTime.now().getMonthOfYear()));

        return labels;
    }

    private String getMonthName(int month) {
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mrt";
            case 4:
                return "Apr";
            case 5:
                return "Mei";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Okt";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "";
        }
    }
}
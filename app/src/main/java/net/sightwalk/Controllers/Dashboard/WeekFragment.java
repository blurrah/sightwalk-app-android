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

public class WeekFragment extends Fragment {

    private BarChart chart;

    private ActivitiesStore store;

    private int valuesOne;
    private int valuesTwo;
    private int valuesThree;
    private int valuesFour;

    private ArrayList<BarEntry> entries;
    private ArrayList<String> labels;

    private ArrayList<Integer> valueOne = new ArrayList<>();
    private ArrayList<Integer> valueTwo = new ArrayList<>();
    private ArrayList<Integer> valueThree = new ArrayList<>();
    private ArrayList<Integer> valueFour = new ArrayList<>();

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_week, container, false);

        JodaTimeAndroid.init(getActivity());

        store = new ActivitiesStore(getContext());

        for(Activities activities : store.getAll()) {
            checkDate(activities);
        }

        setValues();

        chart = (BarChart) view.findViewById(R.id.weekChart);

        setupChart();

        return view;
    }

    private void checkDate(Activities activities) {
        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        LocalDate date = format.parseLocalDate(activities.getStarttijd());

        int week = date.getWeekOfWeekyear();

        int dateOne = DateTime.now().getWeekOfWeekyear();
        int dateTwo = DateTime.now().minusMonths(1).getWeekOfWeekyear();
        int dateThree = DateTime.now().minusMonths(2).getWeekOfWeekyear();
        int dateFour = DateTime.now().minusMonths(3).getWeekOfWeekyear();

        if(week == dateOne) {
            valueOne.add(getDistance(activities.getJson()));
        }
        else if(week == dateTwo) {
            valueTwo.add(getDistance(activities.getJson()));
        }
        else if(week == dateThree) {
            valueThree.add(getDistance(activities.getJson()));
        }
        else if(week == dateFour) {
            valueFour.add(getDistance(activities.getJson()));
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

        BarDataSet dataSet = new BarDataSet(getDataSet(), "Gelopen afstand in kilometers per week");

        dataSet.setColors(new int[]{Color.parseColor("#27b764"), Color.parseColor("#a8e2c1")});
        dataSet.setDrawValues(false);
        dataSet.setHighlightEnabled(false);

        BarData data = new BarData(getLabels(), dataSet);
        chart.setData(data);
    }

    private ArrayList<BarEntry> getDataSet() {
        entries = new ArrayList<>();

        entries.add(new BarEntry(valuesFour, 0));
        entries.add(new BarEntry(valuesThree, 1));
        entries.add(new BarEntry(valuesTwo, 2));
        entries.add(new BarEntry(valuesOne, 3));

        return entries;
    }

    private ArrayList<String> getLabels() {
        labels = new ArrayList<>();

        labels.add("Week " + Integer.toString(DateTime.now().minusWeeks(3).getWeekOfWeekyear()));
        labels.add("Week " + Integer.toString(DateTime.now().minusWeeks(2).getWeekOfWeekyear()));
        labels.add("Week " + Integer.toString(DateTime.now().minusWeeks(1).getWeekOfWeekyear()));
        labels.add("Week " + Integer.toString(DateTime.now().getWeekOfWeekyear()));

        return labels;
    }
}
package net.sightwalk.Controllers.Dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import net.danlew.android.joda.JodaTimeAndroid;
import net.sightwalk.R;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class MonthFragment extends Fragment {

    private BarChart chart;
    private ArrayList<BarEntry> entries;
    private ArrayList<String> labels;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_month, container, false);

        JodaTimeAndroid.init(getActivity());

        chart = (BarChart) view.findViewById(R.id.monthChart);

        setupChart();

        return view;
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

        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        BarDataSet dataSet = new BarDataSet(getDataSet(), "Test");

        dataSet.setColors(new int[]{Color.parseColor("#27b764"), Color.parseColor("#a8e2c1")});
        dataSet.setDrawValues(false);
        dataSet.setHighlightEnabled(false);

        BarData data = new BarData(getLabels(), dataSet);
        chart.setData(data);
    }

    private ArrayList<BarEntry> getDataSet() {
        entries = new ArrayList<>();

        entries.add(new BarEntry(4f, 0));
        entries.add(new BarEntry(8f, 1));
        entries.add(new BarEntry(6f, 2));
        entries.add(new BarEntry(14f, 3));
        entries.add(new BarEntry(10f, 4));
        entries.add(new BarEntry(18f, 5));

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
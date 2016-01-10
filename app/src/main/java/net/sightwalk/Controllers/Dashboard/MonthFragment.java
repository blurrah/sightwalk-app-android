package net.sightwalk.Controllers.Dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;

import net.danlew.android.joda.JodaTimeAndroid;
import net.sightwalk.R;

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

        XAxis label = chart.getXAxis();
        label.setPosition(XAxis.XAxisPosition.BOTTOM);

        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        //BarDataSet dataSet = new BarDataSet(getDataSet(), "Test");

        //dataSet.setDrawValues(false);
        //dataSet.setHighlightEnabled(false);

        //BarData data = new BarData(getLabels(), dataSet);
        //chart.setData(data);
    }
}
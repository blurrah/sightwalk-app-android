package net.sightwalk.Helpers;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.sightwalk.Models.Steps;
import net.sightwalk.R;

import java.util.ArrayList;

public class RouteStepsAdapter extends ArrayAdapter<Steps> {

    private ImageView maneuverImageView;
    private TextView routeTextView;

    public RouteStepsAdapter(Context context, ArrayList<Steps> steps) {
        super(context, 0, steps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Steps step = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_route_list_item, parent, false);
        }

        maneuverImageView = (ImageView) convertView.findViewById(R.id.maneuverImageView);
        routeTextView = (TextView) convertView.findViewById(R.id.routeTextView);

        if (step.getHtml_instructions().contains("linksaf")) {
            maneuverImageView.setImageResource(R.drawable.ic_left);
        } else if (step.getHtml_instructions().contains("rechtsaf")) {
            maneuverImageView.setImageResource(R.drawable.ic_right);
        } else if (step.getHtml_instructions().contains("rechtdoor") || step.getHtml_instructions().contains("vervolgen")) {
            maneuverImageView.setImageResource(R.drawable.ic_arrow);
        } else {
            maneuverImageView.setImageResource(R.drawable.ic_walking);
        }

        routeTextView.setText(Html.fromHtml(step.getHtml_instructions()));

        return convertView;
    }
}
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

public class RouteAdapter extends ArrayAdapter {

    public RouteAdapter(Context context, ArrayList<Steps> stepsArrayList) {
        super(context, R.layout.custom_row_route, stepsArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_row_route, parent, false);

        Steps stepItem = (Steps) getItem(position);
        TextView steps = (TextView) customView.findViewById(R.id.routeTextView);
        ImageView imageView = (ImageView) customView.findViewById(R.id.maneuverImageView);

        steps.setText(Html.fromHtml(stepItem.getHtml_instructions()));

        if(stepItem.getHtml_instructions().contains("linksaf")) {
            imageView.setImageResource(R.drawable.ic_left);
        }
        else if(stepItem.getHtml_instructions().contains("rechtsaf")) {
            imageView.setImageResource(R.drawable.ic_right);
        }
        else if(stepItem.getHtml_instructions().contains("rechtdoor") || stepItem.getHtml_instructions().contains("vervolgen")) {
            imageView.setImageResource(R.drawable.ic_arrow);
        }
        else {
            imageView.setImageResource(R.drawable.ic_walking);
        }

        return customView;
    }
}
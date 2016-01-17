package net.sightwalk.Controllers.Route;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.sightwalk.Models.Sight;
import net.sightwalk.R;

import java.io.InputStream;
import java.net.URL;

public class SightDialogFragment extends Fragment {

    private ImageView sightImg;
    private Bitmap bitmap;
    private TextView sightTitle;
    private TextView sightDesc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sight_dialog, container, false);

        sightTitle = (TextView) view.findViewById(R.id.sightTitle);
        sightDesc = (TextView) view.findViewById(R.id.sightText);
        sightImg = (ImageView) view.findViewById(R.id.sightImage);

        return view;
    }

    public void show() {
        getView().setVisibility(View.VISIBLE);
    }

    public void hide() {
        getView().setVisibility(View.INVISIBLE);
    }

    public void setScope(Sight sight) {
        sightTitle.setText(sight.name);
        sightDesc.setText(sight.shortdesc);

        if (sight.image.length() > 1) {
            Picasso.with(getContext()).load(sight.image).into(sightImg);
        } else {
            sightImg.setImageResource(R.drawable._splash_logo);
        }


    }
}
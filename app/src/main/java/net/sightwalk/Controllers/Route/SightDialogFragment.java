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
        new LoadImage().execute(sight.image);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                sightImg.setImageBitmap(image);

            } else {
                Toast.makeText(getActivity(), "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
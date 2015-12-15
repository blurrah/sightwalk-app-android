package net.sightwalk.Controllers.Route;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.sightwalk.Models.Sights;
import net.sightwalk.R;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Ruben on 10/12/2015.
 */
public class SightDialogFragment extends Fragment {

    View view;
    ImageView sightImg;
    Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sight_dialog, container, false);

        return view;
    }

    public void refreshFragment(){
        Cursor active = Sights.activeSight;

        TextView sightTitle = (TextView) view.findViewById(R.id.sightTitle);
        TextView sightDesc = (TextView) view.findViewById(R.id.sightText);
        sightImg = (ImageView) view.findViewById(R.id.sightImage);



        if(active != null) {
            sightTitle.setText(active.getString(active.getColumnIndex("name")));
            sightDesc.setText(active.getString(active.getColumnIndex("short_desc")));
            new LoadImage().execute(active.getString(active.getColumnIndex("imgurl")));
        }
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

            if(image != null){
                sightImg.setImageBitmap(image);

            }else{

                Toast.makeText(getActivity(), "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }
}

package net.sightwalk.Controllers.Tutorial;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.sightwalk.R;

public class TutorialFragment extends Fragment {

    View view;
    Integer tutorialText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tutorial, container, false);

        ImageView bla = (ImageView) view.findViewById(R.id.tutorialImageView);
        Drawable k = getResources().getDrawable(tutorialText);
        bla.setImageDrawable(k);

        return view;
    }

    public void setTutorialImg(Integer text){

        tutorialText = text;
    }
}
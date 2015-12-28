package net.sightwalk.Stores;

import android.content.Context;
import android.util.Log;

import net.sightwalk.Models.Sight;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Frank on 12/26/2015.
 */
public class SightSelectionStore extends SightStore {

    private static SightSelectionStore sharedInstance;

    public static SightSelectionStore getSharedInstance(String slot, SightsInterface client) {
        if (!(sharedInstance instanceof SightSelectionStore)) {
            sharedInstance = new SightSelectionStore(slot, client);
        }
        return sharedInstance;
    }

    private ArrayList<Sight> selected;

    public ArrayList<Sight> getSelectedSights() {
        return selected;
    }

    protected SightSelectionStore(String slot, SightsInterface client) {
        super(slot, client);
        selected = new ArrayList<>();
    }

    public boolean isSelected(Sight sight) {
        return selected.contains(sight);
    }

    public void setSelected(Sight sight, boolean state) {
        if (state && !isSelected(sight))
            selected.add(sight);

        if (!state && isSelected(sight))
            selected.remove(sight);
    }

    @Override
    public void triggerRemoveSight(Sight sight) {
        super.triggerRemoveSight(sight);

        if (isSelected(sight)) {
            setSelected(sight, false);
        }
    }

    public void setSelected(Sight sight) {
        setSelected(sight, true);
    }
}

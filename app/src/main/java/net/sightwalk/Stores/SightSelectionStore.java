package net.sightwalk.Stores;

import android.content.Context;

import net.sightwalk.Models.Sight;

import java.util.ArrayList;

public class SightSelectionStore extends SightStore {

    private static SightSelectionStore sharedInstance;

    public static SightSelectionStore getSharedInstance(String slot, SightsInterface client) {
        if (!(sharedInstance instanceof SightSelectionStore)) {
            sharedInstance = new SightSelectionStore(client);
        }

        SightStore.subscribe(slot, client);
        return sharedInstance;
    }

    public static SightSelectionStore getSharedInstance(Context context) {
        if (!(sharedInstance instanceof SightSelectionStore)) {
            sharedInstance = new SightSelectionStore(context);
        }

        return sharedInstance;
    }

    private ArrayList<Sight> selected;

    public ArrayList<Sight> getSelectedSights() {
        return selected;
    }

    protected SightSelectionStore(SightsInterface client) {
        super(client);
        selected = new ArrayList<>();
    }

    protected SightSelectionStore(Context context) {
        super(context);
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

    private Sight activeSight;

    public void setActiveSight(Sight sight){
        activeSight = sight;
    }

    public Sight getActiveSight(){
        return activeSight;
    }

    public void clearSelection(){
        selected = new ArrayList<>();
    }
}
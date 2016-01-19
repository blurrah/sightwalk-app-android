package net.sightwalk.Stores;

import android.content.Context;

import net.sightwalk.Models.Sight;

public interface SightsInterface {
    void addedSight(Sight sight);

    void removedSight(Sight sight);

    void updatedSight(Sight oldSight, Sight newSight);

    Context getApplicationContext();
}
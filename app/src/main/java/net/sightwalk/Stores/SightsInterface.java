package net.sightwalk.Stores;

import android.content.Context;

import net.sightwalk.Models.Sight;

/**
 * Created by Frank on 12/26/2015.
 */
public interface SightsInterface {
    void addedSight(Sight sight);

    void removedSight(Sight sight);

    void updatedSight(Sight oldSight, Sight newSight);

    Context getApplicationContext();
}

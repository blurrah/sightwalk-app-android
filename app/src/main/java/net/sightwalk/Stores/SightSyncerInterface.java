package net.sightwalk.Stores;

import net.sightwalk.Models.Sight;

/**
 * Created by Frank on 12/26/2015.
 */
public interface SightSyncerInterface {

    void triggerAddSight(Sight sight);

    void triggerRemoveSight(Sight sight);

    void triggerUpdateSight(Sight oldSight, Sight newSight);

}

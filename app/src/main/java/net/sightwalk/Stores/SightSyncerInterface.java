package net.sightwalk.Stores;

import net.sightwalk.Models.Sight;

public interface SightSyncerInterface {

    void triggerAddSight(Sight sight);

    void triggerRemoveSight(Sight sight);

    void triggerUpdateSight(Sight oldSight, Sight newSight);
}
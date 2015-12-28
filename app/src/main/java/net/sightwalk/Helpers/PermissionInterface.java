package net.sightwalk.Helpers;

/**
 * Created by Frank on 12/26/2015.
 */
public interface PermissionInterface {
    void granted(String... permission);

    void denied(String... permission);
}

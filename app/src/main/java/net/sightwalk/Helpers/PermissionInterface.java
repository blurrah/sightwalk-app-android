package net.sightwalk.Helpers;

public interface PermissionInterface {
    void granted(String... permission);
    void denied(String... permission);
}
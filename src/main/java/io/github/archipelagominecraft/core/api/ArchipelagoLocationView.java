package io.github.archipelagominecraft.core.api;

public interface ArchipelagoLocationView<T extends ArchipelagoLocationType<T,?>> {
    void check();
    boolean isChecked();
}

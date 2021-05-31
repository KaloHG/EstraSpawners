package net.estra.EstraSpawners.model;

import org.bukkit.Location;

public class Spawner {

    private Location location;
    private SpawnerType type;

    public Spawner(Location location, SpawnerType type) {
        this.location = location;
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public SpawnerType getType() {
        return type;
    }
}

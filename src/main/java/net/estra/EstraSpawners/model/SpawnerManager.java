package net.estra.EstraSpawners.model;

import net.estra.EstraSpawners.EstraSpawners;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpawnerManager {

    public List<Spawner> spawners = new ArrayList<>();
    public List<SpawnerType> spawnerTypes = new ArrayList<>();


    public void saveSpawners() {
        EstraSpawners.spawnerDAO.saveSpawners(spawners);
    }

    public void loadSpawners() {
        spawners = EstraSpawners.spawnerDAO.loadSpawners();
    }

    public void runExpSpawns() {
        EstraSpawners.logger.info("running EXP spawns");
        for(Spawner spawner : spawners) {
            int expAmnt = spawner.getType().getExpGen();
            Location location = spawner.getLocation();
            //Don't continue if the chunk isn't loaded that the spawner is in.
            if (location.getWorld().isChunkLoaded(location.getChunk())) {
                Collection<Entity> entityCollection = location.getWorld().getNearbyEntities(location, 33, 33, 33);
                Player nearest = null;
                double nearestDistance = Double.MAX_VALUE;
                for (Entity entity : entityCollection) {
                    if (entity.getType() == EntityType.PLAYER) {
                        double eDist = entity.getLocation().distance(location);
                        if (eDist < nearestDistance) {
                            nearest = (Player) entity;
                            nearestDistance = eDist;
                        }
                    }
                }
                //Now we should have our player.
                if (nearest == null) {
                    return;
                }

                if (nearest.getLocation().distance(spawner.getLocation()) > 10) {
                    return;
                }

                location.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, location, 1, 1, 1, 15);
                int oldLevel = nearest.getLevel();
                //rng exp whatever stolen from XPSpawners
                int xpToGive = 1 + (int) Math.floor(Math.random() * 2.0 * (double) expAmnt);
                nearest.giveExp(xpToGive);
                if (!(oldLevel < nearest.getLevel() && nearest.getLevel() % 5 == 0)) {
                    nearest.playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1);
                }
                EstraSpawners.logger.info("EXP was given to " + nearest.getName());
            }
            EstraSpawners.logger.info("finished cycling through spawners");
        }
    }

    public boolean isSpawnerPresent(Location location) {
        for(Spawner spawner : spawners) {
            if(spawner.getLocation().equals(location)) {
                return true;
            }
        }
        return false;
    }

    public Spawner getSpawner(Location location) {
        for(Spawner spawner : spawners) {
            if(spawner.getLocation().equals(location)) {
                return spawner;
            }
        }
        return null;
    }

    public void addSpawner(Spawner spawner) {
        if(!spawners.contains(spawner)) {
            spawners.add(spawner);
        }
    }

    public void removeSpawner(Spawner spawner) {
        if(spawners.contains(spawner)) {
            spawners.remove(spawner);
        }
    }

    public void loadSpawnerTypes(List<SpawnerType> types) {
        spawnerTypes = types;
    }

    public SpawnerType getSpawnerTypeByID(String id) {
        for(SpawnerType type : spawnerTypes) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return null;
    }

    public boolean isSpawnerByItemStack(ItemStack itemStack) {
        for(SpawnerType type : spawnerTypes) {
            if(type.isSpawner(itemStack)) {
                return true;
            }
        }
        return false;
    }

    public SpawnerType getSpawnerTypeByItemStack(ItemStack itemStack) {
        for(SpawnerType type : spawnerTypes) {
            if(type.isSpawner(itemStack)) {
                EstraSpawners.logger.info("returning type");
                return type;
            }
        }
        return null;
    }
}

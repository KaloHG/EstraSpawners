package net.estra.EstraSpawners.model;

import net.estra.EstraSpawners.EstraSpawners;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SpawnerType {
    private String id;
    private String name;
    private List<String> lore;
    private int expGen;
    private int expTime;

    public SpawnerType(String id, String name, List<String> lore, int expGen, int expTime) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.expGen = expGen;
        this.expTime = expTime;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getExpGen() {
        return expGen;
    }

    public int getExpTime() {
        return expTime;
    }

    public ItemStack getSpawnerItemStack() {
        ItemStack spawnerStack = new ItemStack(Material.MOB_SPAWNER);
        ItemMeta spawnerMeta = spawnerStack.getItemMeta();
        spawnerMeta.setDisplayName(name);
        spawnerMeta.setLore(lore);
        spawnerStack.setItemMeta(spawnerMeta);
        return spawnerStack;
    }

    public boolean isSpawner(ItemStack itemStack) {
        if(itemStack.getType() == Material.MOB_SPAWNER) {
            ItemMeta spawnerMeta = itemStack.getItemMeta();
            EstraSpawners.logger.info(spawnerMeta.getDisplayName());
            EstraSpawners.logger.info(name);
            if(spawnerMeta.getDisplayName().equalsIgnoreCase(name)) {
                if(spawnerMeta.getLore().equals(lore)) {
                    return true;
                }
            }
        }
        return false;
    }
}

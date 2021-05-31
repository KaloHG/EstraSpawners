package net.estra.EstraSpawners.listener;

import net.estra.EstraSpawners.EstraSpawners;
import net.estra.EstraSpawners.model.Spawner;
import net.estra.EstraSpawners.model.SpawnerType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnerListener implements Listener {

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if(block.getType() == Material.MOB_SPAWNER) {
            ItemStack item = event.getItemInHand();
            if(EstraSpawners.spawnerManager.isSpawnerByItemStack(item)) {
                SpawnerType spawnerType = EstraSpawners.spawnerManager.getSpawnerTypeByItemStack(item);

                Spawner newSpawner = new Spawner(block.getLocation(), spawnerType);
                EstraSpawners.spawnerManager.addSpawner(newSpawner);
                EstraSpawners.logger.info("Registering new spawner at " + block.getLocation().toString());
                event.getPlayer().sendMessage(ChatColor.GREEN + "Successfully created " + spawnerType.getName() + " at " + block.getX() + " " + block.getY() + " " + block.getZ());
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        if(EstraSpawners.spawnerManager.isSpawnerPresent(event.getBlock().getLocation())) {
            Spawner spawner = EstraSpawners.spawnerManager.getSpawner(event.getBlock().getLocation());
            EstraSpawners.spawnerManager.removeSpawner(spawner);
            event.getPlayer().sendMessage(ChatColor.RED + "Successfully destroyed " + spawner.getType().getName() + " at " + event.getBlock().getX() + " " + event.getBlock().getY() + " " + event.getBlock().getZ());
        }
    }

    @EventHandler
    public void spawnerShit(SpawnerSpawnEvent event) {
        //Spawners shouldn't spawn SHIT.
        event.setCancelled(true);
    }
}

package net.estra.EstraSpawners;

import net.estra.EstraSpawners.listener.SpawnerListener;
import net.estra.EstraSpawners.model.SpawnerManager;
import net.estra.EstraSpawners.model.SpawnerType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import vg.civcraft.mc.civmodcore.ACivMod;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EstraSpawners extends ACivMod {

    public static EstraSpawners instance;
    public static Configuration config;
    public static Logger logger;

    public static SpawnerDAO spawnerDAO;
    public static SpawnerManager spawnerManager;

    @Override
    public void onEnable() {
        instance = this;
        config = this.getConfig();
        logger = this.getLogger();

        loadSql();

        spawnerManager = new SpawnerManager();

        loadSpawnerTypes();
        spawnerManager.loadSpawners();

        this.getServer().getPluginManager().registerEvents(new SpawnerListener(), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                spawnerManager.runExpSpawns();
            }
        }, 200, 200);
    }

    @Override
    protected String getPluginName() {
        return "EstraSpawners";
    }

    @Override
    public void onDisable() {
        spawnerManager.saveSpawners();
    }

    public void loadSql() {
        ConfigurationSection sqlSection = config.getConfigurationSection("sql");
        String host = sqlSection.getString("host");
        int port = sqlSection.getInt("port");
        String dbName = sqlSection.getString("dbname");
        String username = sqlSection.getString("username");
        String password = sqlSection.getString("password");
        spawnerDAO = new SpawnerDAO(this, username, password, host, port, dbName, 5, 1000L, 600000L, 7200000L);
    }

    public void loadSpawnerTypes() {
        ConfigurationSection spawnerSection = config.getConfigurationSection("spawners");
        List<SpawnerType> spawnerTypes = new ArrayList<>();
        for(String key : spawnerSection.getKeys(false)) {
            ConfigurationSection spawnerType = spawnerSection.getConfigurationSection(key);
            String id = spawnerType.getString("id");
            String name = spawnerType.getString("name");
            List<String> lore = spawnerType.getStringList("lore");
            int expGen = spawnerType.getInt("expGen");
            int expTime = spawnerType.getInt("expTime");
            SpawnerType spawner = new SpawnerType(id, name, lore, expGen, expTime);
            spawnerTypes.add(spawner);

            logger.info("Successfully loaded SpawnerType: " + spawner.getName());
        }

        spawnerManager.loadSpawnerTypes(spawnerTypes);
    }

}

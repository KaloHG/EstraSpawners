package net.estra.EstraSpawners;

import net.estra.EstraSpawners.model.Spawner;
import net.estra.EstraSpawners.model.SpawnerType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpawnerDAO extends ManagedDatasource {

    public SpawnerDAO(ACivMod plugin, String user, String pass, String host, int port, String database, int poolSize, long connectionTimeout, long idleTimeout, long maxLifetime) {
        super(plugin, user, pass, host, port, database, poolSize, connectionTimeout, idleTimeout, maxLifetime);
        prepareMigrations();
        updateDatabase();
    }

    /**
     * Creates and updates tables.
     */
    private void prepareMigrations() {
        registerMigration(0, false, "CREATE TABLE IF NOT EXISTS spawners (`world` VARCHAR(50) NOT NULL, `x` INT(11) NOT NULL, `y` INT(11) NOT NULL, `z` INT(11) NOT NULL, `type` VARCHAR(50) NOT NULL);");
    }

    private void dropTable() {
        try {
            Connection conn = getConnection();
            PreparedStatement prep = conn.prepareStatement("DROP TABLE `spawners`;");
        } catch (SQLException ex) {
            EstraSpawners.logger.severe("Failed to reset spawner table :/" + ex.getMessage());
        }
    }

    /**
     * Saves all spawners provided.
     * @param spawners
     */
    public void saveSpawners(List<Spawner> spawners) {
        dropTable();
        prepareMigrations();
        try {
            Connection conn = getConnection();
            PreparedStatement prep = conn.prepareStatement("replace into spawners (world, x, y , z, type) values(?,?,?,?,?);"); {
                for (Spawner spawner : spawners) {
                    prep.setString(1, spawner.getLocation().getWorld().getName());
                    prep.setInt(2, spawner.getLocation().getBlockX());
                    prep.setInt(3, spawner.getLocation().getBlockY());
                    prep.setInt(4, spawner.getLocation().getBlockZ());
                    prep.setString(5, spawner.getType().getId());
                    prep.execute();
                }
            }
        } catch (SQLException ex) {
            EstraSpawners.logger.severe("Failed to save spawners!" + ex.getMessage());
        }
    }

    /**
     * Loads all spawners in the DAO
     */
    public List<Spawner> loadSpawners() {
        List<Spawner> spawners = new ArrayList<>();
        try(Connection conn = getConnection();
            PreparedStatement prep = conn.prepareStatement("select world, x, y, z, type FROM spawners;");
            ResultSet rs = prep.executeQuery()) {
                while(rs.next()) {
                    String world = rs.getString(1);
                    int x = rs.getInt(2);
                    int y = rs.getInt(3);
                    int z = rs.getInt(4);
                    String type = rs.getString(5);
                    Location location = new Location(Bukkit.getWorld(world), x, y, z);
                    SpawnerType spawnerType = EstraSpawners.spawnerManager.getSpawnerTypeByID(type);
                    Spawner spawner = new Spawner(location, spawnerType);
                    spawners.add(spawner);
                }
        } catch (SQLException ex) {
                EstraSpawners.logger.severe("Failed to load spawners :/ " + ex.getMessage());
        }
        return spawners;
    }
}

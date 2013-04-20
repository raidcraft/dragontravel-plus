package de.raidcraft.dragontravelplus.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.dragontravelplus.station.DragonStation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 28.11.12 - 18:14
 * Description:
 */
public class StationTable extends Table {

    public StationTable() {

        super("stations", "dragontp_");
    }

    @Override
    public void createTable() {

        try {
            executeUpdate(
                    "CREATE TABLE `" + getTableName() + "` (" +
                            "`id` INT NOT NULL AUTO_INCREMENT, " +
                            "`name` VARCHAR( 32 ) NOT NULL, " +
                            "`world` VARCHAR ( 32 ) NOT NULL ,\n" +
                            "`x` INT( 11 ) NOT NULL ,\n" +
                            "`y` INT( 11 ) NOT NULL ,\n" +
                            "`z` INT( 11 ) NOT NULL ,\n" +
                            "`cost_level` INT( 11 ) NOT NULL ,\n" +
                            "`main` TINYINT( 1 ) NOT NULL, " +
                            "`emergency` TINYINT( 1 ) NOT NULL, " +
                            "`creator` VARCHAR ( 32 ) NOT NULL, " +
                            "`created` VARCHAR ( 32 ) NOT NULL, " +
                            "PRIMARY KEY ( `id` )" +
                            ")");
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }

    public List<DragonStation> getAllStations(String worldName) {

        List<DragonStation> stations = new ArrayList<>();
        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM " + getTableName() + " WHERE world = '" + worldName + "';");

            while (resultSet.next()) {
                World world = Bukkit.getWorld(resultSet.getString("world"));
                if (world == null) continue;

                DragonStation station = new DragonStation(resultSet.getString("name")
                        , new Location(world, resultSet.getDouble("x"), resultSet.getDouble("y"), resultSet.getDouble("z"))
                        , resultSet.getInt("cost_level")
                        , resultSet.getBoolean("main")
                        , resultSet.getBoolean("emergency")
                        , resultSet.getString("creator")
                        , resultSet.getString("created"));

                stations.add(station);
            }
            resultSet.close();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
        return stations;
    }

    public List<DragonStation> getNearbyStations(Location location, int radius) {

        List<DragonStation> stations = new ArrayList<>();
        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM " + getTableName() + " WHERE " +
                            "x >= " + (location.getBlockX() - radius) + " AND " +
                            "x <= " + (location.getBlockX() + radius) + " AND " +
                            "y >= " + (location.getBlockY() - radius) + " AND " +
                            "y <= " + (location.getBlockY() + radius) + " AND " +
                            "z >= " + (location.getBlockZ() - radius) + " AND " +
                            "z <= " + (location.getBlockZ() + radius)
            );

            while (resultSet.next()) {
                World world = Bukkit.getWorld(resultSet.getString("world"));
                if (world == null) continue;

                DragonStation station = new DragonStation(resultSet.getString("name")
                        , new Location(world, resultSet.getDouble("x"), resultSet.getDouble("y"), resultSet.getDouble("z"))
                        , resultSet.getInt("cost_level")
                        , resultSet.getBoolean("main")
                        , resultSet.getBoolean("emergency")
                        , resultSet.getString("creator")
                        , resultSet.getString("created"));

                stations.add(station);
            }
            resultSet.close();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
        return stations;
    }

    public List<String> getEmergencyStations() {

        List<String> stations = new ArrayList<>();
        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM " + getTableName() + " WHERE emergency = '1'"
            );

            while (resultSet.next()) {
                stations.add(resultSet.getString("name"));
            }
            resultSet.close();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
        return stations;
    }

    public void addStation(DragonStation station) {

        try {
            String query = "INSERT INTO " + getTableName() + " (name, world, x, y, z, cost_level, main, emergency, creator, created) " +
                    "VALUES (" +
                    "'" + station.getName() + "'" + "," +
                    "'" + station.getLocation().getWorld().getName() + "'" + "," +
                    "'" + station.getLocation().getBlockX() + "'" + "," +
                    "'" + station.getLocation().getBlockY() + "'" + "," +
                    "'" + station.getLocation().getBlockZ() + "'" + "," +
                    "'" + station.getCostLevel() + "'" + "," +
                    "'" + ((station.isMainStation()) ? 1 : 0) + "'" + "," +
                    "'" + ((station.isEmergencyTarget()) ? 1 : 0) + "'" + "," +
                    "'" + station.getCreator() + "'" + "," +
                    "'" + station.getCreated() + "'" +
                    ");";

            executeUpdate(query);
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteStation(DragonStation station) {

        try {
            executeUpdate(
                    "DELETE FROM " + getTableName() + " WHERE name = '" + station.getName() + "'");
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }
}

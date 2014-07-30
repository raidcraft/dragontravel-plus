package de.raidcraft.dragontravelplus.tables;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;

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
                            "PRIMARY KEY ( `id` )" +
                            ")"
            );
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }

    public void migrate() {

        try {
            EbeanServer database = RaidCraft.getComponent(DragonTravelPlusPlugin.class).getDatabase();
            ResultSet resultSet = executeQuery("SELECT * FROM `" + getTableName() + "` WHERE 1");

            int cnt = 0;
            while (resultSet.next()) {
                TStation station = new TStation();
                station.setName(resultSet.getString("name"));
                station.setWorld(resultSet.getString("world"));
                station.setX(resultSet.getInt("x"));
                station.setY(resultSet.getInt("y"));
                station.setZ(resultSet.getInt("z"));
                station.setCostMultiplier(resultSet.getInt("cost_level"));
                station.setMainStation(resultSet.getBoolean("main"));
                station.setEmergencyStation(resultSet.getBoolean("emergency"));
                database.save(station);
                cnt++;
            }
            RaidCraft.LOGGER.info("Migrated " + cnt + " DTP stations!");
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }
}

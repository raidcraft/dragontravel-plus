package de.raidcraft.dragontravelplus.tables;

import de.raidcraft.api.database.Table;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.util.DateUtil;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 28.11.12 - 20:41
 * Description:
 */
public class PlayerStationsTable extends Table {

    public PlayerStationsTable() {

        super("players", "dragontp_");
    }

    @Override
    public void createTable() {

        try {
            executeUpdate(
                    "CREATE TABLE `" + getTableName() + "` (" +
                            "`id` INT NOT NULL AUTO_INCREMENT, " +
                            "`player` VARCHAR( 32 ) NOT NULL, " +
                            "`station_name` VARCHAR( 32 ) NOT NULL, " +
                            "`discovered` VARCHAR ( 32 ) NOT NULL, " +
                            "PRIMARY KEY ( `id` )" +
                            ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllPlayerStations(String player) {

        List<String> stations = new ArrayList<>();
        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM " + getTableName() + " WHERE player = '" + player + "'");

            while (resultSet.next()) {
                stations.add(resultSet.getString("station_name"));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stations;
    }

    public boolean playerIsFamiliar(Player player, DragonStation station) {

        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM " + getTableName() + " WHERE player = '" + player + "' AND station_name = '" + station.getName() + "'");

            while (resultSet.next()) {
                return true;
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addStation(String player, DragonStation station) {

        try {
            String query = "INSERT INTO " + getTableName() + " (player, station_name, discovered) " +
                    "VALUES (" +
                    "'" + player + "'" + "," +
                    "'" + station.getName() + "'" + "," +
                    "'" + DateUtil.getCurrentDateString() + "'" +
                    ");";

            executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package de.raidcraft.dragontravelplus.tables;

import com.silthus.raidcraft.util.component.DateUtil;
import com.silthus.raidcraft.util.component.database.Table;
import com.sk89q.commandbook.CommandBook;
import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.station.DragonStation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 28.11.12 - 20:41
 * Description:
 */
public class PlayerStations extends Table {

    public PlayerStations() {
        super("players", "dragontp_");
    }

    @Override
    public void createTable() {
        try {
            DragonTravelPlusModule.inst.getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (" +
                            "`id` INT NOT NULL AUTO_INCREMENT, " +
                            "`player` VARCHAR( 32 ) NOT NULL, " +
                            "`station_name` VARCHAR( 32 ) NOT NULL, " +
                            "`discovered` VARCHAR ( 32 ) NOT NULL, " +
                            "PRIMARY KEY ( `id` )" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    public List<String> getAllPlayerStations(String player) {

        List<String> stations = new ArrayList<>();
        try {
            ResultSet resultSet = DragonTravelPlusModule.inst.getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE player = '" + player + "'").executeQuery();

            while (resultSet.next()) {
                stations.add(resultSet.getString("station_name"));
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
        return stations;
    }

    public void addStation(String player, DragonStation station) {

        try {
            String query = "INSERT INTO " + getTableName() + " (player, station_name, discovered) " +
                    "VALUES (" +
                    "'" + player + "'" + "," +
                    "'" + station.getName() + "'" + "," +
                    "'" + DateUtil.getCurrentDateString() + "'" +
                    ");";

            Statement statement = DragonTravelPlusModule.inst.getConnection().createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
            e.printStackTrace();
        }
    }
}

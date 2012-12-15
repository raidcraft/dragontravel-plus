package de.raidcraft.dragontravelplus.tables;

import com.silthus.raidcraft.util.component.DateUtil;
import com.silthus.raidcraft.util.component.database.Table;
import com.sk89q.commandbook.CommandBook;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;

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
            getConnection().prepareStatement(
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

    public List<DragonStation> getAllPlayerStations(String player) {

        List<DragonStation> stations = new ArrayList<>();
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE player = '" + player + "' OR emergency = '1'").executeQuery();

            while (resultSet.next()) {
                DragonStation station = StationManager.INST.getDragonStation(resultSet.getString("station_name"));
                if(station == null) continue;

                stations.add(station);
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

            Statement statement = getConnection().createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
            e.printStackTrace();
        }
    }
}

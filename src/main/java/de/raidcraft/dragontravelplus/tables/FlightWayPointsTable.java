package de.raidcraft.dragontravelplus.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.dragontravelplus.flight.Flight;
import de.raidcraft.dragontravelplus.flight.WayPoint;
import de.raidcraft.util.DateUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 28.11.12 - 18:14
 * Description:
 */
public class FlightWayPointsTable extends Table {

    public FlightWayPointsTable() {

        super("flightpoints", "dragontp_");
    }

    @Override
    public void createTable() {

        try {
            executeUpdate(
                    "CREATE TABLE `" + getTableName() + "` (" +
                            "`id` INT NOT NULL AUTO_INCREMENT, " +
                            "`flight` VARCHAR( 32 ) NOT NULL, " +
                            "`world` VARCHAR ( 32 ) NOT NULL ,\n" +
                            "`x` INT( 11 ) NOT NULL ,\n" +
                            "`y` INT( 11 ) NOT NULL ,\n" +
                            "`z` INT( 11 ) NOT NULL ,\n" +
                            "`creator` VARCHAR ( 32 ) NOT NULL, " +
                            "`created` VARCHAR ( 32 ) NOT NULL, " +
                            "PRIMARY KEY ( `id` )" +
                            ")");
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }

    public Flight getFlight(String flightName) {

        flightName = flightName.toLowerCase();
        Flight flight = new Flight(flightName);
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE flight = '" + flightName + "' ORDER BY id").executeQuery();

            while (resultSet.next()) {
                WayPoint wayPoint = new WayPoint(resultSet.getString("world"),
                        resultSet.getDouble("x"),
                        resultSet.getDouble("y"),
                        resultSet.getDouble("z"));
                flight.addWaypoint(wayPoint);
            }
            resultSet.close();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
        if(flight.size() == 0) {
            return null;
        }
        return flight;
    }

    public boolean exists(String flightName) {

        flightName = flightName.toLowerCase();
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE flight = '" + flightName + "';").executeQuery();

            while (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
        return false;
    }

    public List<String> getExistingFlightNames() {

        List<String> flightNames = new ArrayList<>();
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " GROUP BY flight;").executeQuery();

            while (resultSet.next()) {
                flightNames.add(resultSet.getString("flight"));
            }
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
        return flightNames;
    }

    public void addFlight(Flight flight, String creator) {

        // delete existing flight
        deleteFlight(flight.getName());

        try {

            String query = "INSERT INTO " + getTableName() + " (flight, world, x, y, z, creator, created) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";

            PreparedStatement statement = getConnection().prepareStatement(query);

            getConnection().setAutoCommit(false);

            for(int i = 0; i < flight.size(); i++) {
                WayPoint wayPoint = flight.getNextWaypoint();

                statement.setString(1, flight.getName().toLowerCase());
                statement.setString(2, flight.getFlightWorld());
                statement.setDouble(3, wayPoint.getX());
                statement.setDouble(4, wayPoint.getY() - 3); // offset because of wrong dragon midpoint
                statement.setDouble(5, wayPoint.getZ());
                statement.setString(6, creator);
                statement.setString(7, DateUtil.getCurrentDateString());
                statement.executeUpdate();

                if(i % 100 == 0) {
                    getConnection().commit();
                }
            }
            getConnection().commit();
            getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteFlight(String flightName) {

        flightName = flightName.toLowerCase();
        try {
            getConnection().prepareStatement(
                    "DELETE FROM " + getTableName() + " WHERE flight = '" + flightName + "'").execute();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }
}

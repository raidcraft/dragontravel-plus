package de.raidcraft.dragontravelplus.station;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.exceptions.AlreadyExistsException;
import de.raidcraft.dragontravelplus.tables.PlayerStations;
import de.raidcraft.dragontravelplus.tables.StationTable;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Philip
 * Date: 25.11.12 - 13:57
 * Description:
 */
public class StationManager {

    public static final StationManager INST = new StationManager();

    public Map<String, DragonStation> existingStations = new HashMap<>();

    public void loadExistingStations() {

        existingStations.clear();
        int i = 0;
        for (DragonStation station : RaidCraft.getTable(StationTable.class).getAllStations()) {
            i++;
            existingStations.put(station.getName(), station);
        }

        RaidCraft.LOGGER.info("[DTP] Es wurden " + i + " Stationen geladen!");
    }

    public void addNewStation(DragonStation dragonStation) throws AlreadyExistsException {

        if (existingStations.containsKey(dragonStation.getName())) {
            throw new AlreadyExistsException("Eine Station mit diesem Namen existiert bereits!");
        }

        existingStations.put(dragonStation.getName(), dragonStation);
        RaidCraft.getTable(StationTable.class).addStation(dragonStation);
    }

    public List<Location> getAllStationLocations() {

        List<Location> locations = new ArrayList<>();
        for (Map.Entry<String, DragonStation> entry : existingStations.entrySet()) {
            locations.add(entry.getValue().getLocation());
        }
        return locations;
    }

    public List<DragonStation> getStations() {

        List<DragonStation> stations = new ArrayList<>();
        for (Map.Entry<String, DragonStation> entry : existingStations.entrySet()) {
            stations.add(entry.getValue());
        }
        return stations;
    }

    public DragonStation getDragonStation(String name) {

        for (Map.Entry<String, DragonStation> entry : existingStations.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public DragonStation getNearbyStation(Location location) {

        List<DragonStation> stations = RaidCraft.getTable(
                StationTable.class).getNearbyStations(location,
                RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.npcStationSearchRadius);
        if (stations.size() == 0) {
            return null;
        }
        return existingStations.get(stations.get(0).getName());
    }

    public List<DragonStation> getPlayerStations(Player player) {

        List<DragonStation> stations = new ArrayList<>();

        if(player.hasPermission("dragontravelplus.stations.all")) {

            for(Map.Entry<String, DragonStation> entry : existingStations.entrySet()) {
                stations.add(entry.getValue());
            }
            return stations;
        }

        List<String> stationNames = RaidCraft.getTable(PlayerStations.class).getAllPlayerStations(player.getName());
        stationNames.addAll(RaidCraft.getTable(StationTable.class).getEmergencyStations());
        for (String name : stationNames) {
            DragonStation station = existingStations.get(name);
            if (station == null) continue;
            if (stations.contains(station)) continue;
            if (station.getLocation().getWorld() != player.getWorld()) continue; // only stations on same world

            stations.add(station);
        }
        return stations;
    }

    public DragonStation getPlayerStation(Player player, String stationName) {

        for (DragonStation station : getPlayerStations(player)) {
            if (station.getName().equalsIgnoreCase(stationName)) {
                return station;
            }
        }
        return null;
    }

    public void assignStationWithPlayer(String player, DragonStation station) {

        RaidCraft.getTable(PlayerStations.class).addStation(player, station);
    }

    public void deleteStation(DragonStation station) {

        RaidCraft.getTable(StationTable.class).deleteStation(station);
    }

}

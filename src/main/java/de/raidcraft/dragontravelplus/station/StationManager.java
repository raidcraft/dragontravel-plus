package de.raidcraft.dragontravelplus.station;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.exceptions.AlreadyExistsException;
import de.raidcraft.dragontravelplus.tables.PlayerStationsTable;
import de.raidcraft.dragontravelplus.tables.StationTable;
import de.raidcraft.rcconversations.util.ChunkLocation;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

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
        for(World world : Bukkit.getWorlds()) {
            for (DragonStation station : RaidCraft.getTable(StationTable.class).getAllStations(world.getName())) {
                i++;
                existingStations.put(station.getName(), station);
            }
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

        name = name.replace("_", " ");

        for (DragonStation dragonStation : existingStations.values()) {
            if(dragonStation.getName().equalsIgnoreCase(name)) return dragonStation;
        }
        for (DragonStation dragonStation : existingStations.values()) {
            if(dragonStation.getName().toLowerCase().startsWith(name.toLowerCase())) return dragonStation;
        }
        return null;
    }

    public DragonStation getNearbyStation(Location location, int radius) {

        if(location == null) return null;

        StationTable stationTable = RaidCraft.getTable(StationTable.class);
        DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);

        if(stationTable == null || plugin == null) return null; // prevent npe during shutdown

        List<DragonStation> stations = stationTable.getNearbyStations(location, radius);
        if (stations.size() == 0) {
            return null;
        }
        return existingStations.get(stations.get(0).getName());
    }

    public Set<DragonStation> getStationsByChunk(Chunk chunk) {

        ChunkLocation chunkLocation = new ChunkLocation(chunk);
        Set<DragonStation> chunkStations = new HashSet<>();
        for(DragonStation station : getStations()) {
            ChunkLocation stationChunkLocation = new ChunkLocation(station.getLocation());
            if(chunkLocation.equals(stationChunkLocation)) {
                chunkStations.add(station);
            }
        }
        return chunkStations;
    }

    public List<DragonStation> getPlayerStations(Player player) {

        List<DragonStation> stations = new ArrayList<>();

        if(player.hasPermission("dragontravelplus.stations.all")) {

            for(Map.Entry<String, DragonStation> entry : existingStations.entrySet()) {
                stations.add(entry.getValue());
            }
            return stations;
        }

        List<String> stationNames = RaidCraft.getTable(PlayerStationsTable.class).getAllPlayerStations(player.getName());
        stationNames.addAll(RaidCraft.getTable(StationTable.class).getEmergencyStations());
        for (String name : stationNames) {
            DragonStation station = existingStations.get(name.replace("_", " "));
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
        for (DragonStation station : getPlayerStations(player)) {
            if (station.getName().toLowerCase().startsWith(stationName.toLowerCase())) {
                return station;
            }
        }
        return null;
    }

    public boolean stationIsFamiliar(Player player, DragonStation station) {

        return getPlayerStations(player).contains(station);
    }

    public void assignStationWithPlayer(String player, DragonStation station) {

        RaidCraft.getTable(PlayerStationsTable.class).addStation(player, station);
    }

    public void deleteStation(DragonStation station) {

        RaidCraft.getTable(StationTable.class).deleteStation(station);
    }
}

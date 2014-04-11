package de.raidcraft.dragontravelplus;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.tables.TPlayerStation;
import de.raidcraft.dragontravelplus.tables.TStation;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.api.station.UnknownStationException;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public final class StationManager implements Component {

    private final DragonTravelPlusPlugin plugin;
    private final Map<String, Station> loadedStations = new CaseInsensitiveMap<>();

    protected StationManager(DragonTravelPlusPlugin plugin) {

        this.plugin = plugin;
        load();
        RaidCraft.registerComponent(StationManager.class, this);
    }

    public void reload() {

        loadedStations.clear();
        load();
    }

    private void load() {

        int cnt = 0;
        List<TStation> stations = plugin.getDatabase().find(TStation.class).findList();
        for (TStation station : stations) {
            DragonStation dragonStation = new DragonStation(station.getName(), station.getLocation(),
                    station.getCostMultiplier(), station.isMainStation(), station.isEmergencyStation());
            loadedStations.put(dragonStation.getDisplayName(), dragonStation);
            cnt++;
        }
        plugin.getLogger().info("Loaded " + cnt + "/" + stations.size() + " DTP stations...");
    }

    public Station getStation(String name) throws UnknownStationException {

        if (!loadedStations.containsKey(name.toLowerCase())) {
            throw new UnknownStationException("No station with the name " + name + " found!");
        }
        return loadedStations.get(name.toLowerCase());
    }

    public List<Station> getUnlockedStations(Player player) {

        List<TPlayerStation> stationsList = plugin.getDatabase().find(TPlayerStation.class).where()
                .eq("player", player.getName())
                .isNotNull("discovered").findList();
        List<Station> stations = new ArrayList<>();
        for (TPlayerStation playerStation : stationsList) {
            try {
                stations.add(getStation(playerStation.getStation().getDisplayName().toLowerCase()));
            } catch (UnknownStationException e) {
                plugin.getLogger().warning(e.getMessage());
            }
        }
        // also add all emergency stations
        for (Station station : getAllStations()) {
            if (station instanceof DragonStation) {
                if (((DragonStation) station).isMainStation() || ((DragonStation) station).isEmergencyTarget()) {
                    stations.add(station);
                }
            }
        }
        return stations;
    }

    public Station getNearbyStation(Location location, int radius) throws UnknownStationException {

        for (Station station : loadedStations.values()) {
            if (LocationUtil.isWithinRadius(station.getLocation(), location, radius)) {
                return station;
            }
        }
        throw new UnknownStationException("No station found within the radius of " + radius + " near " + location.toString());
    }

    public DragonStation createNewStation(String name, Location location, int costLevel, boolean mainStation, boolean emergencyTarget) throws UnknownStationException {

        if (loadedStations.containsKey(name)) {
            throw new UnknownStationException("Duplicate station with the name " + name + " detected!");
        }
        DragonStation dragonStation = new DragonStation(name, location, costLevel, mainStation, emergencyTarget);
        dragonStation.save();
        loadedStations.put(dragonStation.getDisplayName(), dragonStation);
        return dragonStation;
    }

    public void deleteStation(DragonStation station) {

        loadedStations.remove(station.getDisplayName());
        TStation entry = plugin.getDatabase().find(TStation.class).where().eq("name", station.getDisplayName()).findUnique();
        plugin.getDatabase().delete(entry);
    }

    public List<Station> getAllStations() {

        return new ArrayList<>(loadedStations.values());
    }
}

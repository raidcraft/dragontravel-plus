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
            loadedStations.put(dragonStation.getName(), dragonStation);
            cnt++;
        }
        plugin.getLogger().info("Loaded " + cnt + "/" + stations.size() + " DTP stations...");
    }

    public Station getStation(String name) throws UnknownStationException {

        if (!loadedStations.containsKey(name)) {
            throw new UnknownStationException("No station with the name " + name + " found!");
        }
        return loadedStations.get(name);
    }

    public List<Station> getUnlockedStations(Player player) {

        List<TPlayerStation> stationsList = plugin.getDatabase().find(TPlayerStation.class).where()
                .eq("player", player.getName())
                .isNotNull("unlocked").findList();
        List<Station> stations = new ArrayList<>();
        for (TPlayerStation playerStation : stationsList) {
            try {
                stations.add(getStation(playerStation.getStation().getName()));
            } catch (UnknownStationException e) {
                plugin.getLogger().warning(e.getMessage());
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
}

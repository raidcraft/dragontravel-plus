package de.raidcraft.dragontravelplus;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.tables.TStation;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.api.station.UnknownStationException;
import de.raidcraft.util.CaseInsensitiveMap;

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
}

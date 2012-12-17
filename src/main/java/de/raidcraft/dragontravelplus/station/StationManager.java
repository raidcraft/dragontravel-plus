package de.raidcraft.dragontravelplus.station;

import com.silthus.raidcraft.util.component.database.ComponentDatabase;
import com.sk89q.commandbook.CommandBook;
import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.exceptions.AlreadyExistsException;
import de.raidcraft.dragontravelplus.tables.PlayerStations;
import de.raidcraft.dragontravelplus.tables.StationTable;
import org.bukkit.Location;

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

    private Map<String, DragonStation> existingStations = new HashMap<>();

    public void loadExistingStations() {

        existingStations.clear();
        int i = 0;
        for(DragonStation station : ComponentDatabase.INSTANCE.getTable(StationTable.class).getAllStations()) {
            i++;
            existingStations.put(station.getName().toLowerCase(), station);
        }
        CommandBook.logger().info("[DTP] Es wurden " + i + " Stationen geladen!");
    }
    
    public void addNewStation(DragonStation dragonStation) throws AlreadyExistsException {
        
        if(existingStations.containsKey(dragonStation.getName().toLowerCase())) {
            throw new AlreadyExistsException("Eine Station mit diesem Namen existiert bereits!");
        }
    
        existingStations.put(dragonStation.getName().toLowerCase(), dragonStation);
        ComponentDatabase.INSTANCE.getTable(StationTable.class).addStation(dragonStation);
    }
    
    public DragonStation getDragonStation(String name) {
        
        return existingStations.get(name.toLowerCase());
    }

    public DragonStation getNearbyStation(Location location) {

        List<DragonStation> stations =  ComponentDatabase.INSTANCE.getTable(
                StationTable.class).getNearbyStations(location,
                DragonTravelPlusModule.inst.config.npcStationSearchRadius);
        if(stations.size() == 0) {
            return null;
        }
        return existingStations.get(stations.get(0).getName().toLowerCase());
    }
    
    public List<DragonStation> getPlayerStations(String player) {
        List<DragonStation> stations = new ArrayList<>();
        List<String> stationNames = ComponentDatabase.INSTANCE.getTable(PlayerStations.class).getAllPlayerStations(player);
        stationNames.addAll(ComponentDatabase.INSTANCE.getTable(StationTable.class).getEmergencyStations());
        for(String name : stationNames) {
            DragonStation station = existingStations.get(name.toLowerCase());
            if(station == null) continue;
            if(stations.contains(station)) continue;

            stations.add(station);
        }
        return stations;
    }
    
    public DragonStation getPlayerStation(String player, String stationName) {
        for(DragonStation station : getPlayerStations(player)) {
            if(station.getName().equalsIgnoreCase(stationName)) {
                return station;
            }
        }
        return null;
    }
    
    public void assignStationWithPlayer(String player, DragonStation station) {
        ComponentDatabase.INSTANCE.getTable(PlayerStations.class).addStation(player, station);
    }

    public void deleteStation(DragonStation station) {
        ComponentDatabase.INSTANCE.getTable(StationTable.class).deleteStation(station);
    }

}

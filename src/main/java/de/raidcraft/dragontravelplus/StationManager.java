package de.raidcraft.dragontravelplus;

import com.silthus.raidcraft.util.component.database.ComponentDatabase;
import com.sk89q.commandbook.CommandBook;
import de.raidcraft.dragontravelplus.eceptions.AlreadyExistsException;
import de.raidcraft.dragontravelplus.tables.StationTable;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Philip
 * Date: 25.11.12 - 13:57
 * Description:
 */
public class StationManager {
    public static final StationManager INST = new StationManager();

    private Map<String, DragonStation> selectedDragonStations = new HashMap<>();
    private Map<String, DragonStation> existingStations = new HashMap<>();
    
    public DragonStation getSelectedDragonStation(String player) {
        return selectedDragonStations.get(player);
    }

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
        return existingStations.get(name);
    }


}

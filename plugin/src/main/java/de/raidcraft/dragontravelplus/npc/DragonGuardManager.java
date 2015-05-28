package de.raidcraft.dragontravelplus.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.StationManager;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.rcconversations.npc.NPC_Conservations_Manager;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.npc.StationTrait;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

/**
 * @author Philip Urban
 */
public class DragonGuardManager {

    public static void spawnDragonGuardNPC(Station station) {

        Location improvedLocation = station.getLocation().clone();
        improvedLocation.setY(improvedLocation.getY() + 1.5);
        DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
        NPC npc = NPC_Conservations_Manager.getInstance().spawnNonPersistNpcConservations(improvedLocation, "Drachenmeister", plugin.getName(), plugin.getConfig().conversationName);
        npc.addTrait(StationTrait.class);
        npc.getTrait(StationTrait.class).setStationName(station.getName());
    }

    public static void spawnAllDragonGuardNPCs(StationManager stationManager) {

        for(Station station : stationManager.getAllStations()) {
            if(!(station instanceof DragonStation)) continue;
            spawnDragonGuardNPC(station);
        }
    }

    public static void removeDragonGuard(DragonStation station) {
        // TODO: implement
        //        ConversationsTrait.removeFromServer(...);
    }

    public static void removeAllDragonGuards() {
        DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
        NPC_Manager.getInstance().removeAllNPCs(plugin.getName());
    }

}

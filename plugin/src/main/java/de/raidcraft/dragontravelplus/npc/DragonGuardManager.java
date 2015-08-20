package de.raidcraft.dragontravelplus.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.StationManager;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.rctravel.api.station.Station;
import org.bukkit.Location;

import java.util.List;

/**
 * @author Philip Urban
 */
public class DragonGuardManager {

    public static void spawnDragonGuardNPC(Station station) {

        Location improvedLocation = station.getLocation().clone();
        improvedLocation.setY(improvedLocation.getY() + 1.5);
        DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
        Conversations.spawnConversationHost(plugin.getName(), "Drachenmeister", plugin.getConfig().conversationName, improvedLocation);
    }

    public static void spawnAllDragonGuardNPCs(StationManager stationManager) {

        List<Station> stationList = stationManager.getAllStations();
        RaidCraft.LOGGER.info("[DragonTravel] Spawn " + stationList.size() + " Dragon Guards...");
        for(Station station : stationList) {
            spawnDragonGuardNPC(station);
        }
    }

    public static void removeDragonGuard(DragonStation station) {
        // TODO: implement
        //        ConversationsTrait.removeFromServer(...);
    }

    public static void removeAllDragonGuards() {
        DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
        NPC_Manager.getInstance().clear(plugin.getName());
    }

}
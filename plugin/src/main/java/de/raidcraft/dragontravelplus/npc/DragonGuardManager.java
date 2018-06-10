package de.raidcraft.dragontravelplus.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.StationManager;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.npc.StationTrait;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;

/**
 * @author Philip Urban
 */
public class DragonGuardManager {

    public static void spawnDragonGuardNPC(Station station) {

        Location improvedLocation = station.getLocation().clone();
        improvedLocation.setY(improvedLocation.getY() + 1.5);
        DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
        Optional<ConversationHost<?>> host = Conversations.spawnConversationHost(plugin.getName(), "Drachenmeister", plugin.getConfig().conversationName, improvedLocation);
        host.ifPresent(npc -> npc.addTrait(StationTrait.class));
        host.ifPresent(npc -> npc.getTrait(StationTrait.class).ifPresent(trait -> trait.setStationName(station.getName())));
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
        NPC_Manager.getInstance().removeAllNPCs(plugin.getName());
    }

}
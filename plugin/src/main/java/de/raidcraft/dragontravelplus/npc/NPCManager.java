package de.raidcraft.dragontravelplus.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.rcconversations.npc.ConversationsTrait;
import de.raidcraft.rctravel.api.station.Station;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

import static de.raidcraft.rcconversations.npc.ConversationsTrait.*;

/**
 * @author Philip Urban
 */
public class NPCManager {

    public static void createDragonGuard(Station station) {

        Location improvedLocation = station.getLocation().clone();
        improvedLocation.setY(improvedLocation.getY()+1);
        ConversationsTrait.createInitial(improvedLocation, RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().conversationName, "Drachenmeister");
    }

    public static void removeDragonGuard(DragonStation station) {
        // TODO: implement
//        ConversationsTrait.removeFromServer(...);
    }

}

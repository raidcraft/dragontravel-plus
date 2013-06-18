package de.raidcraft.dragontravelplus.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.rcconversations.npc.ConversationsTrait;
import de.raidcraft.rcconversations.npc.NPCRegistry;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

/**
 * @author Philip Urban
 */
public class NPCManager {

    public static void createDragonGuard(DragonStation station) {

        Location improvedLocation = station.getLocation().clone();
        improvedLocation.setY(improvedLocation.getY()+1);
        ConversationsTrait.create(station.getLocation(), RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().conversationName, "Drachenmeister", false);
    }

    public static void removeDragonGuard(DragonStation station) {

        String conversationName = RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().conversationName;
        for(NPC npc : NPCRegistry.INST.getSpawnedNPCs(station.getLocation().getChunk())) {
            ConversationsTrait trait = npc.getTrait(ConversationsTrait.class);
            if(!trait.getConversationName().equalsIgnoreCase(conversationName)) {
                continue;
            }

            if(npc.getBukkitEntity().getLocation().distance(station.getLocation()) <= 5) {
                NPCRegistry.INST.unregisterNPC(npc);
                npc.destroy();
            }
        }
    }

}

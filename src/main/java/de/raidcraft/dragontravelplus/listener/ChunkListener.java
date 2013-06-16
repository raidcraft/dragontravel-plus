package de.raidcraft.dragontravelplus.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.npc.NPCManager;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import de.raidcraft.rcconversations.npc.ConversationsTrait;
import de.raidcraft.rcconversations.npc.NPCRegistry;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Urban
 */
public class ChunkListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {

        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new DragonMasterChecker(event.getChunk()), 10);
    }

    public class DragonMasterChecker implements Runnable {

        private Chunk chunk;

        public DragonMasterChecker(Chunk chunk) {

            this.chunk = chunk;
        }

        @Override
        public void run() {

            String conversationName = RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().conversationName;
            List<DragonStation> stations = StationManager.INST.getStationsByChunk(chunk);
            List<NPC> spawnedNPCs = new ArrayList<>(NPCRegistry.INST.getSpawnedNPCs(chunk));
            for(NPC npc : spawnedNPCs) {
                ConversationsTrait trait = npc.getTrait(ConversationsTrait.class);
                if(!trait.getConversationName().equalsIgnoreCase(conversationName)) {
                    continue;
                }
                for(DragonStation station : stations) {
                    if(npc.getBukkitEntity().getLocation().distance(station.getLocation()) <= 5) {
                        stations.remove(station);
                        break;
                    }
                }

                // delete all npcs without station
                NPCRegistry.INST.unregisterNPC(npc);
                npc.destroy();
            }

            // if there are stations without npcs -> create new
            for(DragonStation station : stations) {
                NPCManager.createDragonGuard(station);
            }
        }
    }
}

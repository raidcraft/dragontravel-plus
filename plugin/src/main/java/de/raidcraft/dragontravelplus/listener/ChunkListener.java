package de.raidcraft.dragontravelplus.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.StationManager;
import de.raidcraft.dragontravelplus.npc.NPCManager;
import de.raidcraft.rcconversations.RCConversationsPlugin;
import de.raidcraft.rcconversations.npc.ConversationsTrait;
import de.raidcraft.rcconversations.npc.NPCRegistry;
import de.raidcraft.rcconversations.util.ChunkLocation;
import de.raidcraft.rctravel.api.station.Station;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Philip Urban
 */
public class ChunkListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {

        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new DragonMasterChecker(event.getChunk()), 3 * 20);
    }

    public class DragonMasterChecker implements Runnable {

        private Chunk chunk;

        public DragonMasterChecker(Chunk chunk) {

            this.chunk = chunk;
        }

        @Override
        public void run() {

            String conversationName = RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().conversationName;
            List<Station> stations = RaidCraft.getComponent(StationManager.class).getAllStations();

            // if there are stations without npcs -> create new

            for (Station station : new HashSet<>(stations)) {

                // check a second time
                Set<ChunkLocation> affectedChunks = NPCRegistry.INST.getAffectedChunkLocations(chunk);
                boolean found = false;
                for (ChunkLocation cl : affectedChunks) {
                    for (Entity entity : chunk.getWorld().getChunkAt(cl.getX(), cl.getZ()).getEntities()) {
                        if (!(entity instanceof LivingEntity)) continue;
                        if (entity.getLocation().distance(station.getLocation()) <= 5) {
                            NPC npc = RaidCraft.getComponent(RCConversationsPlugin.class).getCitizens().getNPCRegistry().getNPC(entity);
                            if (npc == null) continue;
                            ConversationsTrait trait = npc.getTrait(ConversationsTrait.class);
                            if (!trait.getConversationName().equalsIgnoreCase(conversationName)) continue;
                            stations.remove(station);
                            if (found) {
                                NPCRegistry.INST.unregisterNPC(npc);
                                npc.destroy();
                            } else {
                                found = true;
                            }
                        }
                    }
                }
            }

            for (Station station : stations) {
                RaidCraft.LOGGER.info("Create DragonGuard NPC for station: '" + station.getDisplayName() + "'!");
                NPCManager.createDragonGuard(station);
            }
        }
    }
}

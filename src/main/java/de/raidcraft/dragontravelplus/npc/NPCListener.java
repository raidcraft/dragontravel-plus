package de.raidcraft.dragontravelplus.npc;

import net.citizensnpcs.api.event.NPCSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Author: Philip
 * Date: 22.11.12 - 21:42
 * Description:
 */
public class NPCListener implements Listener {

    @EventHandler
    public void onNPCSpawn(NPCSpawnEvent event) {

        Bukkit.broadcastMessage("npc spawn");
    }

}

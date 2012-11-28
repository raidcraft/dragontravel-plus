package de.raidcraft.dragontravelplus.npc;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Philip
 * Date: 22.11.12 - 21:42
 * Description:
 */
public class NPCListener implements Listener {
    private Map<String, Conversation> conversations = new HashMap<> ();

    @EventHandler
    public void onSpawn(NPCSpawnEvent event) {
        if(!event.getNPC().hasTrait(DragonGuardTrait.class)) {
            return;
        }
        
        Bukkit.broadcastMessage("spawned");
    }
    
    @EventHandler
    public void onClick(NPCClickEvent event) {
        if(!event.getNPC().hasTrait(DragonGuardTrait.class)) {
            return;
        }

        event.getClicker().sendMessage("Hallo ich bin ein Drachenmeister!");
    }

    @EventHandler
    public void onRight(NPCClickEvent event) {
        if(!event.getNPC().hasTrait(DragonGuardTrait.class)) {
            return;
        }

        event.getClicker().sendMessage("Hallo ich bin ein Drachenmeister!");
    }
}

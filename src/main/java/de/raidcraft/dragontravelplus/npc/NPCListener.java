package de.raidcraft.dragontravelplus.npc;

import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Author: Philip
 * Date: 22.11.12 - 21:42
 * Description:
 */
public class NPCListener implements Listener {

    @EventHandler
    public void onSpawn(NPCSpawnEvent event) {
        NPC npc = event.getNPC();
        if(npc.getBukkitEntity().getType() == EntityType.PLAYER && !npc.hasTrait(DragonGuardTrait.class)) {
            DragonStation station = StationManager.INST.getNearbyStation(npc.getBukkitEntity().getLocation());

            if(station != null) {
                npc.addTrait(DragonGuardTrait.class);
                npc.getTrait(DragonGuardTrait.class).updateDragonGuardNPC();
            }
        }

    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if(!event.getNPC().hasTrait(DragonGuardTrait.class)) {
            return;
        }

        Conversation conversation = Conversation.conversations.get(event.getClicker().getName());
        conversation.trigger(event.getNPC(), Conversation.TriggerType.RIGHT_CLICK);
    }

    @EventHandler
    public void onLeftClick(NPCLeftClickEvent event) {
        if(!event.getNPC().hasTrait(DragonGuardTrait.class)) {
            return;
        }

        Conversation conversation = Conversation.conversations.get(event.getClicker().getName());
        conversation.trigger(event.getNPC(), Conversation.TriggerType.LEFT_CLICK);
    }
}

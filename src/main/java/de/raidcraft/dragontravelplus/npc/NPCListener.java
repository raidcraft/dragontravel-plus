package de.raidcraft.dragontravelplus.npc;

import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
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
        if(!event.getNPC().hasTrait(DragonGuardTrait.class)) {
            return;
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

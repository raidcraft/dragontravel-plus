package de.raidcraft.dragontravelplus.npc;

import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

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
        NPC npc = event.getNPC();
        npc.addTrait(LookClose.class);
        npc.getTrait(LookClose.class).lookClose(true);
        npc.data().set(NPC.DEFAULT_PROTECTED_METADATA, false);
        npc.addTrait(Equipment.class);
        npc.getTrait(Equipment.class).set(1, new ItemStack(Material.CHAINMAIL_HELMET));
        npc.getTrait(Equipment.class).set(2, new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        npc.getTrait(Equipment.class).set(3, new ItemStack(Material.CHAINMAIL_LEGGINGS));
        npc.getTrait(Equipment.class).set(4, new ItemStack(Material.CHAINMAIL_BOOTS));
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if(!event.getNPC().hasTrait(DragonGuardTrait.class)) {
            return;
        }

        Conversation conversation = conversations.get(event.getClicker().getName());
        conversation.trigger(event.getNPC(), Conversation.TriggerType.RIGHT_CLICK);
    }

    @EventHandler
    public void onLeftClick(NPCLeftClickEvent event) {
        if(!event.getNPC().hasTrait(DragonGuardTrait.class)) {
            return;
        }

        Conversation conversation = conversations.get(event.getClicker().getName());
        conversation.trigger(event.getNPC(), Conversation.TriggerType.LEFT_CLICK);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        conversations.put(event.getPlayer().getName(), new Conversation(event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        conversations.remove(event.getPlayer().getName());
    }

}

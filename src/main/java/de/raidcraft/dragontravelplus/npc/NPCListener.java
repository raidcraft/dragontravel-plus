package de.raidcraft.dragontravelplus.npc;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
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

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(!conversations.containsKey(event.getPlayer().getName())) {
            return;
        }
        if(Arrays.asList(DragonTravelPlusModule.inst.config.exitWords).contains(event.getMessage())) {
            conversations.remove(event.getPlayer().getName());
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "Gespräch verlassen...");
            return;
        }
        if(conversations.get(event.getPlayer().getName()).getDragonGuard().getDragonStation().getLocation()
                .distance(event.getPlayer().getLocation()) > DragonTravelPlusModule.inst.config.autoExitDistance) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "Der Drachenwächter hört dich nichtmehr...");
            return;
        }
        if(conversations.get(event.getPlayer().getName())
                .trigger(Conversation.TriggerType.CHAT_ANSWER, event.getMessage())) {
            event.setCancelled(true);
        }

    }
}

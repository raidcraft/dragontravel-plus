package de.raidcraft.dragontravelplus.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;

/**
 * Author: Philip
 * Date: 15.12.12 - 19:03
 * Description:
 */
public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        FlyingPlayer flyingPlayer = DragonManager.INST.getFlyingPlayer(player.getName());

        if (flyingPlayer == null) {
            return;
        }

        if (flyingPlayer.isInAir()) {
            event.setCancelled(true);
            return;
        } else {
            Bukkit.getScheduler().cancelTask(flyingPlayer.getWaitingTaskID());
            DragonManager.INST.flyingPlayers.remove(player);
            ChatMessages.warn(player, "Du hast schaden genommen, der Drache hat wieder abgedreht!");
            Conversation.conversations.remove(player.getName());
            return;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Conversation.conversations.put(event.getPlayer().getName(), new Conversation(event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Conversation.conversations.remove(event.getPlayer().getName());
        DragonManager.INST.abortFlight(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        DragonManager.INST.abortFlight(event.getEntity());
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {

        FlyingPlayer flyingPlayer = DragonManager.INST.getFlyingPlayer(event.getPlayer().getName());

        if (flyingPlayer == null || !flyingPlayer.isInAir()) {
            return;
        }

        for (String cmd : RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.forbiddenCommands) {

            if (event.getMessage().toLowerCase().startsWith("/" + cmd.toLowerCase())) {
                ChatMessages.warn(event.getPlayer(), "Dieser Befehl ist während dem Flug verboten!");
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        if (!Conversation.conversations.containsKey(event.getPlayer().getName())) {
            return;
        }
        Conversation conversation = Conversation.conversations.get(event.getPlayer().getName());
        FlyingPlayer flyingPlayer = DragonManager.INST.flyingPlayers.get(event.getPlayer());

        if (Arrays.asList(RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.exitWords).contains(event.getMessage())) {
            if (flyingPlayer != null && flyingPlayer.isInAir()) {
                DragonManager.INST.abortFlight(event.getPlayer());
                ChatMessages.success(event.getPlayer(), "Du hast den Flug abgebrochen!");
                event.setCancelled(true);
                return;
            }
            if (conversation.inConversation()) {
                conversation.abortConversation();
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.GRAY + "Gespräch verlassen...");
                return;
            }
        }

        if (!conversation.inConversation()) {
            return;
        }

        if (conversation.getDragonGuard().getDragonStation().getLocation()
                .distance(event.getPlayer().getLocation()) > RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.autoExitDistance) {
            conversation.abortConversation();
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "Der Drachenwächter hört dir nichtmehr zu...");
            return;
        }
        if (conversation.trigger(Conversation.TriggerType.CHAT_ANSWER, event.getMessage())) {
            event.setCancelled(true);
        }
    }
}

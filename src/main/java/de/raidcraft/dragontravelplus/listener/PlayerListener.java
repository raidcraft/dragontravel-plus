package de.raidcraft.dragontravelplus.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.Arrays;

/**
 * Author: Philip
 * Date: 15.12.12 - 19:03
 * Description:
 */
public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
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
            ChatMessages.warn(player, "Du hast schaden genommen, der Drache hat wieder abgedreht!");
            Conversation.conversations.get(player.getName()).abortConversation();
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        String message = event.getMessage();
        message = message.replace(" ", "_");

        if (!Conversation.conversations.containsKey(player.getName())) {
            return;
        }
        Conversation conversation = Conversation.conversations.get(player.getName());
        FlyingPlayer flyingPlayer = DragonManager.INST.flyingPlayers.get(player);

        if (Arrays.asList(RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.exitWords).contains(message)) {
            if (flyingPlayer != null && flyingPlayer.isInAir()) {
                DragonManager.INST.abortFlight(player);
                ChatMessages.success(player, "Du hast den Flug abgebrochen!");
                event.setCancelled(true);
                return;
            }
            if (conversation.inConversation()) {
                conversation.abortConversation();
                event.setCancelled(true);
                player.sendMessage(ChatColor.GRAY + "Gespräch verlassen...");
                return;
            }
        }

        if (!conversation.inConversation()) {
            return;
        }

        if (conversation.getDragonGuard().getDragonStation().getLocation().getWorld() != player.getLocation().getWorld()
                || conversation.getDragonGuard().getDragonStation().getLocation()
                .distance(player.getLocation()) > RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.autoExitDistance) {
            conversation.abortConversation();
            event.setCancelled(true);
            player.sendMessage(ChatColor.GRAY + "Der Drachenwächter hört dir nichtmehr zu...");
            return;
        }
        if (conversation.trigger(Conversation.TriggerType.CHAT_ANSWER, message)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {

        FlyingPlayer flyingPlayer = DragonManager.INST.getFlyingPlayer(event.getPlayer().getName());

        if(flyingPlayer == null || !flyingPlayer.isInAir() || flyingPlayer.getDragon() == null) {
            return;
        }
        RCDragon dragon = flyingPlayer.getDragon();

        if(dragon.getFlightType() == RCDragon.FLIGHT_TYPE.FLIGHT || dragon.getFlightType() == RCDragon.FLIGHT_TYPE.TRAVEL) {
            event.setCancelled(true);
        }

        if(dragon.getFlightType() != RCDragon.FLIGHT_TYPE.CONTROLLED_FLIGHT) {
            return;
        }

        // toggle control state
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if(dragon.isForceLanding()) {
                ChatMessages.info(event.getPlayer(), "Die Landung kann nicht abgebrochen werden!");
            }
            else {
                dragon.toggleControlled();
            }
        }
        // set landing place
        else {
            if(dragon.isLanding()) {
                if(dragon.isForceLanding()) {
                    ChatMessages.info(event.getPlayer(), "Die Landung kann nicht abgebrochen werden!");
                }
                else {
                    dragon.toggleControlled();
                    ChatMessages.info(event.getPlayer(), "Landen abgebrochen!");
                }
            }
            else {
                dragon.land();
                ChatMessages.info(event.getPlayer(), "Landen...");
            }
        }
    }
}

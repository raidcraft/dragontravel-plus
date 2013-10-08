package de.raidcraft.dragontravelplus.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.util.Arrays;

/**
 * Author: Philip
 * Date: 15.12.12 - 19:03
 * Description:
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {

        if (!(event.getExited() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getExited();


        FlyingPlayer flyingPlayer = DragonManager.INST.getFlyingPlayer(player.getName());

        if (flyingPlayer == null) {
            return;
        }

        if (flyingPlayer.isInAir()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuiet(PlayerQuitEvent event) {

        DragonManager.INST.abortFlight(event.getPlayer());
    }

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
        }
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

        for (String cmd : RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().forbiddenCommands) {

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

        FlyingPlayer flyingPlayer = DragonManager.INST.getFlyingPlayer(player.getName());

        if (Arrays.asList(RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().exitWords).contains(message)) {
            if (flyingPlayer != null && flyingPlayer.isInAir()) {
                DragonManager.INST.abortFlight(player);
                ChatMessages.success(player, "Du hast den Flug abgebrochen!");
                event.setCancelled(true);
                return;
            }
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

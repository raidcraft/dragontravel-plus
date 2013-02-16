package de.raidcraft.dragontravelplus.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightTravel;
import de.raidcraft.dragontravelplus.events.DragonLandEvent;
import de.raidcraft.dragontravelplus.events.RoutingFinishedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;


/**
 * Author: Philip
 * Date: 17.12.12 - 06:20
 * Description:
 */
public class DragonListener implements Listener {

    @EventHandler
    public void onEnderdragonExlplode(EntityExplodeEvent event) {

        if (event.getEntity() instanceof RCDragon) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDragonLand(DragonLandEvent event) {

        if (event.getPassenger() instanceof Player) {
            PassengerRemover passengerRemover =
                    new PassengerRemover(DragonManager.INST.getFlyingPlayer(((Player) event.getPassenger()).getName()).getPlayer());
            Bukkit.getScheduler().scheduleSyncDelayedTask(RaidCraft.getComponent(DragonTravelPlusPlugin.class), passengerRemover, 7 * 10);
        }
    }

    /*
     * Start flight after route calculation is finished
     */
    @EventHandler
    public void onRoutingFinished(RoutingFinishedEvent event) {

        event.getFlyingPlayer().setInAir(true);
        FlightTravel.flyFlight(event.getFlight(), event.getFlyingPlayer().getPlayer());
        RaidCraft.getEconomy().withdrawPlayer(event.getFlyingPlayer().getPlayer().getName(), event.getFlyingPlayer().getPrice());
        event.getFlyingPlayer().getPlayer().sendMessage(ChatColor.GRAY + "Schreibe '" + RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.exitWords[0] + "' in den Chat um den Flug abzubrechen!");
    }

    public class PassengerRemover implements Runnable {

        private Player player;

        public PassengerRemover(Player player) {

            this.player = player;
        }

        @Override
        public void run() {

            DragonManager.INST.flyingPlayers.remove(player);
        }
    }
}

package de.raidcraft.dragontravelplus.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import de.raidcraft.dragontravelplus.events.DragonLandEvent;
import org.bukkit.Bukkit;
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
            Bukkit.getScheduler().scheduleSyncDelayedTask(RaidCraft.getComponent(DragonTravelPlusPlugin.class), passengerRemover, 3 * 20);
        }
    }

    public class PassengerRemover implements Runnable {

        private Player player;

        public PassengerRemover(Player player) {

            this.player = player;
        }

        @Override
        public void run() {

            DragonManager.INST.getFlyingPlayers().remove(player);
        }
    }
}

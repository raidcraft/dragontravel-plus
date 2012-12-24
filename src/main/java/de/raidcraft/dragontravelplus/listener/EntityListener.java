package de.raidcraft.dragontravelplus.listener;

import com.sk89q.commandbook.CommandBook;
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
public class EntityListener implements Listener {

    @EventHandler
    public void onEnderdragonExlplode(EntityExplodeEvent event) {

        if (event.getEntity() instanceof RCDragon) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDragonLand(DragonLandEvent event) {
        if(event.getPassenger() instanceof Player) {
            PassengerRemover passengerRemover =
                    new PassengerRemover(DragonManager.INST.getFlyingPlayer(((Player) event.getPassenger()).getName()).getPlayer());
            Bukkit.getScheduler().scheduleSyncDelayedTask(CommandBook.inst(), passengerRemover, 7*10);
        }
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

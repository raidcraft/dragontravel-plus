package de.raidcraft.dragontravelplus.listener;

import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import de.raidcraft.dragontravelplus.events.DragonLandEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Map;


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
            for(Map.Entry<Player, FlyingPlayer> entry : DragonManager.INST.flyingPlayers.entrySet()) {
                if(entry.getKey().getName().equalsIgnoreCase(((Player) event.getPassenger()).getName())) {
                    DragonManager.INST.flyingPlayers.remove(entry.getKey());
                    break;
                }
            }
        }
    }
}

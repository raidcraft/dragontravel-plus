package de.raidcraft.dragontravelplus.listener;

import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Author: Philip
 * Date: 15.12.12 - 19:03
 * Description:
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getEntity();
        if(!DragonManager.INST.playerGetDamage(player)) {
            event.setCancelled(true);
        }
    }
}

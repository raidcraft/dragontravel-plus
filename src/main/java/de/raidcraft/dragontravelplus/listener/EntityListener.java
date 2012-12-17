package de.raidcraft.dragontravelplus.listener;

import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Author: Philip
 * Date: 17.12.12 - 06:20
 * Description:
 */
public class EntityListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEnderdragonExlplode(EntityExplodeEvent event) {

        if (event.getEntity() instanceof RCDragon) {
            event.setCancelled(true);
        }
    }
}

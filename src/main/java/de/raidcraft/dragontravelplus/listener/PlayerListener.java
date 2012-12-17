package de.raidcraft.dragontravelplus.listener;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

        if(!DragonManager.INST.flyingPlayers.containsKey(player)) {
            return;
        }

        FlyingPlayer flyingPlayer = DragonManager.INST.flyingPlayers.get(player);

        if(flyingPlayer.isInAir()) {
            event.setCancelled(true);
            return;
        }
        else {
            CommandBook.server().getScheduler().cancelTask(flyingPlayer.getWaitingTaskID());
            DragonManager.INST.flyingPlayers.remove(player);
            ChatMessages.warn(player, "Du hast schaden genommen, der Drache hat wieder abgedreht!");
            return;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {

    }
}

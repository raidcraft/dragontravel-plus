package de.raidcraft.dragontravelplus.listener;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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
            Conversation.conversations.remove(player.getName());
            return;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        DragonManager.INST.abortFlight(event.getPlayer());
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        DragonManager.INST.abortFlight(event.getEntity());
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {

        if(!DragonManager.INST.flyingPlayers.containsKey(event.getPlayer())
                || !DragonManager.INST.flyingPlayers.get(event.getPlayer()).isInAir()) {
            return;
        }

        FlyingPlayer flyingPlayer = DragonManager.INST.flyingPlayers.get(event.getPlayer());
        
        for(String cmd : DragonTravelPlusModule.inst.config.forbiddenCommands) {
            if(event.getMessage().toLowerCase().startsWith(cmd.toLowerCase())) {
                ChatMessages.warn(event.getPlayer(), "Dieser Befehl ist während dem Flug verboten!");
                event.setCancelled(true);
                return;
            }
        }
    }
}

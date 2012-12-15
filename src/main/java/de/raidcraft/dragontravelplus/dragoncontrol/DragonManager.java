package de.raidcraft.dragontravelplus.dragoncontrol;

import com.sk89q.commandbook.CommandBook;
import com.xemsdoom.dt.modules.Travels;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Philip
 * Date: 02.12.12 - 21:27
 * Description:
 */
public class DragonManager {
    private Map<Player, FlyingPlayer> flyingPlayers = new HashMap<>();
    public final static DragonManager INST = new DragonManager();

    public void takeoff(Player player, DragonStation targetStation, int delay) {
        flyingPlayers.put(player, new FlyingPlayer(player.getLocation()));
        CommandBook.inst().getServer().getScheduler()
                .scheduleSyncDelayedTask(CommandBook.inst(), new DelayedTakeoff(player, targetStation), delay * 20);
    }
    
    public void takeoff(Player player, DragonStation targetStation) {
        if(!flyingPlayers.containsKey(player)) {
            return;
        }
        flyingPlayers.get(player).setInAir(true);
        Travels.travelChord(player, targetStation.getLocation().getBlockX()
                , targetStation.getLocation().getBlockY()
                , targetStation.getLocation().getBlockZ());
    }

    public class DelayedTakeoff implements Runnable {

        private Player player;
        private DragonStation targetStation;
        
        public DelayedTakeoff(Player player, DragonStation targetStation) {
            this.player = player;
            this.targetStation = targetStation;
        }
        
        @Override
        public void run() {
                takeoff(player, targetStation);
        }
    }

    public boolean playerGetDamage(Player player) {
        if(!flyingPlayers.containsKey(player)) {
            return true;
        }

        FlyingPlayer flyingPlayer = flyingPlayers.get(player);

        if(flyingPlayer.isInAir()) {
            return false;
        }
        else {
            CommandBook.server().getScheduler().cancelTask(flyingPlayer.getWaitingTaskID());
            flyingPlayers.remove(player);
            ChatMessages.warn(player, "Du hast schaden genommen, der Drache hat wieder abgedreht!");
            return true;
        }
    }
}

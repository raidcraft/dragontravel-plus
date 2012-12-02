package de.raidcraft.dragontravelplus.dragonmanger;

import com.sk89q.commandbook.CommandBook;
import com.xemsdoom.dt.modules.Travels;
import de.raidcraft.dragontravelplus.station.DragonStation;
import org.bukkit.entity.Player;

/**
 * Author: Philip
 * Date: 02.12.12 - 21:27
 * Description:
 */
public class DragonManager {
    public final static DragonManager INST = new DragonManager();

    public void takeoff(Player player, DragonStation targetStation, int delay) {
        CommandBook.inst().getServer().getScheduler()
                .scheduleSyncDelayedTask(CommandBook.inst(), new DelayedTakeoff(player, targetStation), delay * 20);
    }
    
    public void takeoff(Player player, DragonStation targetStation) {
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
}

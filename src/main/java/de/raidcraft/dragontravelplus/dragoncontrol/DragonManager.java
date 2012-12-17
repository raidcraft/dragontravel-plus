package de.raidcraft.dragontravelplus.dragoncontrol;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Flight;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightTravel;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Waypoint;
import de.raidcraft.dragontravelplus.station.DragonStation;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Philip
 * Date: 02.12.12 - 21:27
 * Description:
 */
public class DragonManager {
    public Map<Player, FlyingPlayer> flyingPlayers = new HashMap<>();
    public final static DragonManager INST = new DragonManager();

    public void takeoff(Player player, DragonStation targetStation, int delay) {
        flyingPlayers.put(player, new FlyingPlayer(player));
        CommandBook.inst().getServer().getScheduler()
                .scheduleSyncDelayedTask(CommandBook.inst(), new DelayedTakeoffTask(player, targetStation), delay * 20);
    }
    
    public void takeoff(Player player, DragonStation targetStation) {
        if(!flyingPlayers.containsKey(player)) {
            return;
        }
        FlyingPlayer flyingPlayer = flyingPlayers.get(player);

        Flight flight = new Flight("Flight");
        flight.addWaypoint(new Waypoint(0, 200, 0)); // debug
        flight.addWaypoint(new Waypoint(targetStation.getLocation().getBlockX()
                , targetStation.getLocation().getBlockY()
                , targetStation.getLocation().getBlockZ()));

        FlightTravel.flyFlight(flight, player);
    }

    public class DelayedTakeoffTask implements Runnable {

        private Player player;
        private DragonStation targetStation;

        public DelayedTakeoffTask(Player player, DragonStation targetStation) {
            this.player = player;
            this.targetStation = targetStation;
        }

        @Override
        public void run() {
            takeoff(player, targetStation);
        }
    }

    public class CheckIfArrivedTask implements Runnable {
        
        @Override
        public void run() {

            List<Player> playerToRemove = new ArrayList<>();
            for(Map.Entry<Player, FlyingPlayer> entry : flyingPlayers.entrySet()) {
                FlyingPlayer flyingPlayer = entry.getValue();
                if(flyingPlayer.hasIncorrectState()) {
                    flyingPlayer.getPlayer().teleport(flyingPlayer.getStart());
                    playerToRemove.add(flyingPlayer.getPlayer());
                    continue;
                }

                
                for(Player player : playerToRemove) {
                    flyingPlayers.remove(player);
                }
            }
        }
    }
}

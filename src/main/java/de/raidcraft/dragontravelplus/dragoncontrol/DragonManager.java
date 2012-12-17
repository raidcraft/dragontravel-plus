package de.raidcraft.dragontravelplus.dragoncontrol;

import com.silthus.raidcraft.bukkit.CorePlugin;
import com.sk89q.commandbook.CommandBook;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.modules.Travels;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Flight;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightTravel;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Waypoint;
import de.raidcraft.dragontravelplus.station.DragonStation;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Philip
 * Date: 02.12.12 - 21:27
 * Description:
 */
public class DragonManager {
    public Map<Player, FlyingPlayer> flyingPlayers = new HashMap<>();
    public final static DragonManager INST = new DragonManager();

    public void takeoff(Player player, DragonStation targetStation, int delay, double price) {
        flyingPlayers.put(player, new FlyingPlayer(player, price));
        CommandBook.inst().getServer().getScheduler()
                .scheduleSyncDelayedTask(CommandBook.inst(), new DelayedTakeoffTask(player, targetStation), delay * 20);
    }
    
    public void takeoff(Player player, DragonStation targetStation, double price) {
        if(!flyingPlayers.containsKey(player)) {
            flyingPlayers.put(player, new FlyingPlayer(player, price));
        }
        FlyingPlayer flyingPlayer = flyingPlayers.get(player);

        Flight flight = new Flight("Flight");
        flight.addWaypoint(new Waypoint(0, 200, 0)); // debug
        flight.addWaypoint(new Waypoint(targetStation.getLocation().getBlockX()
                , targetStation.getLocation().getBlockY()
                , targetStation.getLocation().getBlockZ()));

        FlightTravel.flyFlight(flight, player);
        CorePlugin.get().getEconomy().substract(player, flyingPlayer.getPrice());
    }
    
    public void abortFlight(Player player) {
        if(!DragonManager.INST.flyingPlayers.containsKey(player)) {
            return;
        }

        FlyingPlayer flyingPlayer = DragonManager.INST.flyingPlayers.get(player);



        if(flyingPlayer.isInAir()) {
            Travels.removePlayerandDragon(flyingPlayer.getDragon().getBukkitEntity());
        }
        else {
            CommandBook.server().getScheduler().cancelTask(flyingPlayer.getWaitingTaskID());
        }
        player.teleport(flyingPlayer.getStart());   // teleport to start
        DragonManager.INST.flyingPlayers.remove(player);
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
            takeoff(player, targetStation, 0);
        }
    }
}

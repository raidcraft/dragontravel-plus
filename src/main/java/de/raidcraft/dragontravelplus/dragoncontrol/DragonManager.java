package de.raidcraft.dragontravelplus.dragoncontrol;

import com.silthus.raidcraft.bukkit.CorePlugin;
import com.sk89q.commandbook.CommandBook;
import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.modules.Travels;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Flight;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightTravel;
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
    
    public void takeoff(Player player, DragonStation start, DragonStation destination, double price) {

        FlyingPlayer flyingPlayer = new FlyingPlayer(player, start, destination, price);
        CommandBook.inst().getServer().getScheduler()
                .scheduleSyncDelayedTask(CommandBook.inst(), new DelayedTakeoffTask(flyingPlayer), DragonTravelPlusModule.inst.config.flightWarmup * 20);
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
        player.teleport(flyingPlayer.getStart().getLocation());   // teleport to start
        DragonManager.INST.flyingPlayers.remove(player);
    }

    public class DelayedTakeoffTask implements Runnable {

        private FlyingPlayer flyingPlayer;

        public DelayedTakeoffTask(FlyingPlayer flyingPlayer) {
            this.flyingPlayer = flyingPlayer;
        }

        @Override
        public void run() {
            if(!flyingPlayers.containsKey(flyingPlayer.getPlayer())) {
                flyingPlayers.put(flyingPlayer.getPlayer(), flyingPlayer);
            }

            Flight flight = FlightNavigator.INST.getFlight(flyingPlayer.getStart(), flyingPlayer.getDestination());
            flyingPlayer.setInAir(true);
            FlightTravel.flyFlight(flight, flyingPlayer.getPlayer());
            CorePlugin.get().getEconomy().substract(flyingPlayer.getPlayer(), flyingPlayer.getPrice());
        }
    }
}

package de.raidcraft.dragontravelplus.dragoncontrol;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.modules.Travels;
import de.raidcraft.dragontravelplus.station.DragonStation;
import org.bukkit.Bukkit;
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
        Bukkit.getScheduler()
                .scheduleAsyncDelayedTask(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new DelayedTakeoffTask(flyingPlayer), RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightWarmup * 20);
    }

    public void abortFlight(Player player) {

        if (!DragonManager.INST.flyingPlayers.containsKey(player)) {
            return;
        }

        FlyingPlayer flyingPlayer = DragonManager.INST.flyingPlayers.get(player);


        if (flyingPlayer.isInAir()) {
            Travels.removePlayerandDragon(flyingPlayer.getDragon().getBukkitEntity());
        } else {
            Bukkit.getScheduler().cancelTask(flyingPlayer.getWaitingTaskID());
        }
        player.teleport(flyingPlayer.getStart().getLocation());   // teleport to start
        DragonManager.INST.flyingPlayers.remove(player);
    }

    public FlyingPlayer getFlyingPlayer(String name) {

        for (Map.Entry<Player, FlyingPlayer> entry : DragonManager.INST.flyingPlayers.entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public class DelayedTakeoffTask implements Runnable {

        private FlyingPlayer flyingPlayer;

        public DelayedTakeoffTask(FlyingPlayer flyingPlayer) {

            this.flyingPlayer = flyingPlayer;
        }

        @Override
        public void run() {

            if (!flyingPlayers.containsKey(flyingPlayer.getPlayer())) {
                flyingPlayers.put(flyingPlayer.getPlayer(), flyingPlayer);
            }

            FlightNavigator.INST.calculateFlight(flyingPlayer, flyingPlayer.getStart().getLocation(), flyingPlayer.getDestination().getLocation());
        }
    }
}

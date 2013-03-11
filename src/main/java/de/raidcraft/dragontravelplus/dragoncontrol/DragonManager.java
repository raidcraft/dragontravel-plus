package de.raidcraft.dragontravelplus.dragoncontrol;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.EnqueuedNavigationTask;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.modules.Travels;
import de.raidcraft.dragontravelplus.station.DragonStation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
    private Map<FlyingPlayer, EnqueuedNavigationTask> enqueuedNavigationTasks = new HashMap<>();

    public final static DragonManager INST = new DragonManager();

    public void takeoff(Player player, DragonStation start, DragonStation destination, double price) {

        FlyingPlayer flyingPlayer = new FlyingPlayer(player, start.getLocation(), destination.getLocation(), price);

        // check periodically if route is calculated
        EnqueuedNavigationTask enqueuedNavigation = new EnqueuedNavigationTask(flyingPlayer);
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(RaidCraft.getComponent(DragonTravelPlusPlugin.class), enqueuedNavigation, 20, 2*20);
        enqueuedNavigation.setWaitingTaskId(taskId);
        enqueuedNavigationTasks.put(flyingPlayer, enqueuedNavigation);

        // start asynchronous route calculation
        Bukkit.getScheduler().runTaskAsynchronously(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new DelayedTakeoffTask(flyingPlayer));
    }

    public void abortFlight(Player player) {

        if (!DragonManager.INST.flyingPlayers.containsKey(player)) {
            return;
        }

        FlyingPlayer flyingPlayer = DragonManager.INST.flyingPlayers.get(player);


        if (flyingPlayer.isInAir()) {
            Travels.removePlayerAndDragon(flyingPlayer);
        } else {
            Bukkit.getScheduler().cancelTask(flyingPlayer.getWaitingTaskID());
        }
        player.teleport(flyingPlayer.getStart());   // teleport to start
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

    public void calculationFinished(FlyingPlayer flyingPlayer, List<Location> route) {

        EnqueuedNavigationTask enqueuedNavigationTask = DragonManager.INST.enqueuedNavigationTasks.get(flyingPlayer);
        enqueuedNavigationTask.setRoute(route);
        enqueuedNavigationTask.setCalculated(true);
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

            FlightNavigator.INST.calculateFlight(flyingPlayer, flyingPlayer.getStart(), flyingPlayer.getDestination());
        }
    }
}

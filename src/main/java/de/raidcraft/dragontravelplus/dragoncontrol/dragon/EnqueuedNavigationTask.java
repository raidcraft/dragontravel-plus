package de.raidcraft.dragontravelplus.dragoncontrol.dragon;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightTravel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.List;

/**
 * @author Philip
 */
public class EnqueuedNavigationTask implements Runnable {

    private FlyingPlayer flyingPlayer;
    private List<Location> route;
    private int waitingTaskId = 0;
    private boolean calculated = false;

    public EnqueuedNavigationTask(FlyingPlayer flyingPlayer) {

        this.flyingPlayer = flyingPlayer;
        this.waitingTaskId = waitingTaskId;
    }

    public void setWaitingTaskId(int waitingTaskId) {

        this.waitingTaskId = waitingTaskId;
    }

    public void setCalculated(boolean calculated) {

        this.calculated = calculated;
    }

    public void setRoute(List<Location> route) {

        this.route = route;
    }

    @Override
    public void run() {
        if(waitingTaskId != 0 && calculated) {
            calculated = false;
            Bukkit.getScheduler().cancelTask(waitingTaskId);
            flyingPlayer.setInAir(true);
            FlightTravel.flyDynamic(route, flyingPlayer.getPlayer());
            RaidCraft.getEconomy().withdrawPlayer(flyingPlayer.getPlayer().getName(), flyingPlayer.getPrice());
            flyingPlayer.getPlayer().sendMessage(ChatColor.GRAY + "Schreibe '" + RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.exitWords[0] + "' in den Chat um den Flug abzubrechen!");
        }
    }
}

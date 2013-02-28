package de.raidcraft.dragontravelplus.dragoncontrol.dragon;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Flight;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightTravel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * @author Philip
 */
public class EnqueuedNavigationTask implements Runnable {

    private FlyingPlayer flyingPlayer;
    private Flight flight;
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

    public void setFlight(Flight flight) {

        this.flight = flight;
    }

    @Override
    public void run() {
        if(waitingTaskId != 0 && calculated) {
            calculated = false;
            Bukkit.getScheduler().cancelTask(waitingTaskId);
            flyingPlayer.setInAir(true);
            FlightTravel.flyFlight(flight, flyingPlayer.getPlayer());
            RaidCraft.getEconomy().withdrawPlayer(flyingPlayer.getPlayer().getName(), flyingPlayer.getPrice());
            flyingPlayer.getPlayer().sendMessage(ChatColor.GRAY + "Schreibe '" + RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.exitWords[0] + "' in den Chat um den Flug abzubrechen!");
        }
    }
}

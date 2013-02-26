package de.raidcraft.dragontravelplus.dragoncontrol.dragon;

import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Flight;
import de.raidcraft.dragontravelplus.events.RoutingFinishedEvent;
import org.bukkit.Bukkit;

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
            Bukkit.getPluginManager().callEvent(new RoutingFinishedEvent(flyingPlayer, flight));
            Bukkit.getScheduler().cancelTask(waitingTaskId);
        }
    }
}

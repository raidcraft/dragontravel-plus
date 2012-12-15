package de.raidcraft.dragontravelplus.dragoncontrol;

import org.bukkit.Location;

/**
 * Author: Philip
 * Date: 15.12.12 - 19:05
 * Description:
 */
public class FlyingPlayer {
    private Location start;
    private boolean inAir;
    private int waitingTaskID = 0;

    public FlyingPlayer(Location start) {

        this.start = start.clone();
    }

    public Location getStart() {

        return start;
    }

    public boolean isInAir() {

        return inAir;
    }

    public void setInAir(boolean inAir) {

        this.inAir = inAir;
    }

    public void setWaitingTaskID(int waitingTaskID) {

        this.waitingTaskID = waitingTaskID;
    }

    public int getWaitingTaskID() {

        return waitingTaskID;
    }
}

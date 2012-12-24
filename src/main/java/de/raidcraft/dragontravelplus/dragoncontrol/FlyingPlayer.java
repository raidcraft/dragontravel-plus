package de.raidcraft.dragontravelplus.dragoncontrol;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import de.raidcraft.dragontravelplus.station.DragonStation;
import org.bukkit.entity.Player;

/**
 * Author: Philip
 * Date: 15.12.12 - 19:05
 * Description:
 */
public class FlyingPlayer {
    private Player player;
    private RCDragon dragon = null;
    private DragonStation start;
    private DragonStation destination;
    private boolean inAir = false;
    private int waitingTaskID = 0;
    private long startTime = 0;
    private double price = 0;

    public FlyingPlayer(Player player, DragonStation start, DragonStation destination, double price) {

        this.player = player;
        this.start = start;
        this.destination = destination;
        this.price = price;
    }

    public boolean hasIncorrectState() {
        if(inAir && !player.isInsideVehicle()) {
            return true;
        }

        if(inAir &&
                (System.currentTimeMillis() - startTime) > DragonTravelPlusModule.inst.config.flightTimeout*1000*60) {
            return true;
        }
        return false;
    }

    public Player getPlayer() {

        return player;
    }

    public RCDragon getDragon() {

        return dragon;
    }

    public void setDragon(RCDragon dragon) {

        this.dragon = dragon;
    }

    public DragonStation getStart() {

        return start;
    }

    public DragonStation getDestination() {

        return destination;
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

    public void setStartTime(long startTime) {

        this.startTime = startTime;
    }

    public long getStartTime() {

        return startTime;
    }

    public double getPrice() {

        return price;
    }
}

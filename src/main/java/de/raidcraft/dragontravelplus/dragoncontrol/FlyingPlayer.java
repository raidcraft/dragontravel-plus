package de.raidcraft.dragontravelplus.dragoncontrol;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import de.raidcraft.dragontravelplus.station.DragonStation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Author: Philip
 * Date: 15.12.12 - 19:05
 * Description:
 */
public class FlyingPlayer {

    private static final int ENTITY_NAME_INVISIBLE = 0x0;

    private final Player player;
    private final Location start;
    private RCDragon dragon = null;
    private Location destination;
    private boolean inAir = false;
    private long startTime = 0;
    private double price = 0;
    private BukkitTask waitingTask;
    private BukkitTask checkerTask;
    private DragonStation startStation;
    private DragonStation destinationStation;

    public FlyingPlayer(Player player, Location start, Location destination, double price) {

        this(player, start);
        this.destination = destination;
        this.price = price;
    }

    public FlyingPlayer(Player player, Location start) {

        this.player = player;
        this.start = start;
    }

    public FlyingPlayer(Player player, DragonStation startStation, DragonStation destinationStation, double price) {

        this.player = player;
        this.startStation = startStation;
        this.destinationStation = destinationStation;
        this.price = price;

        this.start = startStation.getLocation();
        this.destination = destinationStation.getLocation();
    }

    public boolean hasIncorrectState() {

        return inAir
                && !player.isInsideVehicle()
                || inAir
                && (System.currentTimeMillis() - startTime) > RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().flightTimeout * 1000 * 60;

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

    public Location getStart() {

        return start;
    }

    public Location getDestination() {

        return destination;
    }

    public boolean isInAir() {

        return inAir;
    }

    public void setInAir(boolean inAir) {

        this.inAir = inAir;
    }

    public void setWaitingTaskID(BukkitTask waitingTask) {

        this.waitingTask = waitingTask;
    }

    public BukkitTask getWaitingTaskID() {

        return waitingTask;
    }

    public void cancelWaitingTask() {

        if(this.waitingTask != null) {
            this.waitingTask.cancel();
        }
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

    public void setPrice(double price) {

        this.price = price;
    }

    public DragonStation getStartStation() {

        return startStation;
    }

    public void setStartStation(DragonStation startStation) {

        this.startStation = startStation;
    }

    public DragonStation getDestinationStation() {

        return destinationStation;
    }

    public void setDestinationStation(DragonStation destinationStation) {

        this.destinationStation = destinationStation;
    }

    public BukkitTask getCheckerTask() {

        return checkerTask;
    }

    public void setCheckerTask(BukkitTask checkerTask) {

        cancelCheckerTask();
        this.checkerTask = checkerTask;
    }

    public void cancelCheckerTask() {

        if(this.checkerTask != null) {
            this.checkerTask.cancel();
        }
    }
}

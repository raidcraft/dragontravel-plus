package de.raidcraft.dragontravelplus.dragoncontrol.dragon;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.FlightNavigator;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.modules.Travels;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.ControlledFlight;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Flight;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Waypoint;
import de.raidcraft.dragontravelplus.events.DragonLandEvent;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import net.minecraft.server.v1_4_R1.EntityEnderDragon;
import net.minecraft.server.v1_4_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class RCDragon extends EntityEnderDragon {

    public enum FLIGHT_TYPE {
        TRAVEL,
        FLIGHT,
        CONTROLLED_FLIGHT,
        DYNAMIC
    }

    private FLIGHT_TYPE flightType;
    private Entity entity;
    private FlyingPlayer flyingPlayer;
    private Location target;

    // Travel
    private int maxY;
    private boolean finalMove = false;
    private boolean move = false;

    // Flight
    private Flight flight;
    private Waypoint firstwp;

    // Dynamic Flight
    private List<Location> route;
    private int routeIndex = 0;

    // First WayPoint coords
    private double fwpX;
    private double fwpY;
    private double fwpZ;

    // Amount to fly up/down during a flight
    private double XTick;
    private double YTick;
    private double ZTick;

    // Distance to the right wp coords
    private double distanceX;
    private double distanceY;
    private double distanceZ;

    // Controlled Flight
    private ControlledFlight controlledFlight;
    private boolean currentlyControlled = true;
    private boolean toggleControl = false;
    private Waypoint landingPlace = null;
    private boolean landing = false;
    private boolean forceLanding = false;
    private int durationTaskId = 0;

    // Start points for tick calculation
    private double startX;
    private double startY;
    private double startZ;


    // Start Location
    Location start;

    public RCDragon(Location start, World notchWorld) {

        super(notchWorld);

        this.start = start;
        this.target = start;

        setPosition(start.getX(), start.getY(), start.getZ());

        yaw = start.getYaw() + 180;
        while (yaw > 360)
            yaw -= 360;
        while (yaw < 0)
            yaw += 360;
        if (yaw < 45 || yaw > 315)
            yaw = 0F;
        else if (yaw < 135)
            yaw = 90F;
        else if (yaw < 225)
            yaw = 180F;
        else
            yaw = 270F;
    }

    public RCDragon(World world) {

        super(world);
    }

    public void startTravel(FlyingPlayer flyingPlayer, Location loc) {

        this.flyingPlayer = flyingPlayer;
        entity = getBukkitEntity();

        target.setX(loc.getBlockX());
        target.setY(loc.getBlockY());
        target.setZ(loc.getBlockZ());

        this.startX = start.getX();
        this.startY = start.getY();
        this.startZ = start.getZ();

        maxY = RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightHeight;

        setMoveTravel();
        yaw = getCorrectYaw(target.getX(), target.getZ());
        flightType = FLIGHT_TYPE.TRAVEL;
        move = true;
    }

    public void startFlight(FlyingPlayer flyingPlayer, Flight flight) {

        this.flyingPlayer = flyingPlayer;
        entity = getBukkitEntity();

        this.flight = flight;

        this.firstwp = flight.getFirstWaypoint();
        this.fwpX = firstwp.getX();
        this.fwpY = firstwp.getY();
        this.fwpZ = firstwp.getZ();

        this.startX = start.getX();
        this.startY = start.getY();
        this.startZ = start.getZ();

        target.setX(fwpX);
        target.setY(fwpY);
        target.setZ(fwpZ);

        setMoveFlight();
        yaw = getCorrectYaw(target.getX(), target.getZ());
        move = true;
        flightType = FLIGHT_TYPE.FLIGHT;
    }

    public void startControlled(FlyingPlayer flyingPlayer, ControlledFlight controlledFlight) {

        this.flyingPlayer = flyingPlayer;
        entity = getBukkitEntity();
        this.controlledFlight = controlledFlight;

        // start duration control timer
        if(controlledFlight.getDuration() > 0) {
            durationTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new Runnable() {
                @Override
                public void run() {
                    if(entity.getPassenger() instanceof Player) {
                        ChatMessages.info((Player)entity.getPassenger(), "Die Flugzeit ist abgelaufen, der Drache landet nun...");
                    }
                    forceLanding = true;
                    land();
                }
            }, controlledFlight.getDuration() * 20);
        }

        startX = start.getX();
        startY = start.getY();
        startZ = start.getZ();

        target.setX(startX);
        target.setY(startY);
        target.setZ(startZ);

        setMoveControlled();
        yaw = getCorrectYaw(target.getX(), target.getZ());
        move = true;
        flightType = FLIGHT_TYPE.CONTROLLED_FLIGHT;
    }

    public void startDynamicFlight(FlyingPlayer flyingPlayer, List<Location> route) {

        this.flyingPlayer = flyingPlayer;
        entity = getBukkitEntity();

        this.route = route;
        this.routeIndex = 1;

        this.startX = start.getX();
        this.startY = start.getY();
        this.startZ = start.getZ();

        target.setX(route.get(routeIndex).getX());
        target.setY(route.get(routeIndex).getY());
        target.setZ(route.get(routeIndex).getZ());

        setMoveFlight();
        yaw = getCorrectYaw(target.getX(), target.getZ());
        move = true;
        flightType = FLIGHT_TYPE.DYNAMIC;
    }

    /**
     * Gets the correct yaw for this specific path
     */
    private float getCorrectYaw(double targetx, double targetz) {

        if (this.locZ > targetz)
            return (float) (-Math.toDegrees(Math.atan((this.locX - targetx) / (this.locZ - targetz))));
        if (this.locZ < targetz) {
            return (float) (-Math.toDegrees(Math.atan((this.locX - targetx) / (this.locZ - targetz)))) + 180.0F;
        }
        return this.yaw;
    }

    /**
     * Sets the x,y,z move for each tick
     */
    public void setMoveFlight() {

        this.distanceX = this.startX - target.getX();
        this.distanceY = this.startY - target.getY();
        this.distanceZ = this.startZ - target.getZ();

        double tick = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY) + (distanceZ * distanceZ)) / RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightSpeed;
        YTick = Math.abs(distanceY) / tick;
        XTick = Math.abs(distanceX) / tick;
        ZTick = Math.abs(distanceZ) / tick;
    }

    /**
     * Sets the x,z move for each tick
     */
    public void setMoveTravel() {

        this.distanceX = this.startX - target.getX();
        this.distanceY = this.startY - target.getY();
        this.distanceZ = this.startZ - target.getZ();

        double tick = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY) + (distanceZ * distanceZ)) / RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightSpeed;
        XTick = Math.abs(distanceX) / tick;
        ZTick = Math.abs(distanceZ) / tick;
    }

    public void setMoveControlled() {

        this.distanceX = this.startX - target.getX();
        this.distanceY = this.startY - target.getY();
        this.distanceZ = this.startZ - target.getZ();

        double tick = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY) + (distanceZ * distanceZ)) / RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.controlledFlightSpeed;
        YTick = Math.abs(distanceY) / tick;
        XTick = Math.abs(distanceX) / tick;
        ZTick = Math.abs(distanceZ) / tick;
    }

    @Override
    public void c() {

        // Travel
        if(flightType == FLIGHT_TYPE.TRAVEL) {
            travel();
            return;
        }

        // Flight
        if(flightType == FLIGHT_TYPE.FLIGHT) {
            flight();
        }

        // Dynamic Flight
        if(flightType == FLIGHT_TYPE.DYNAMIC) {
            dynamic();
        }

        // Controlled flight
        if(flightType == FLIGHT_TYPE.CONTROLLED_FLIGHT) {
            controlled();
        }
    }

    /**
     * Is called during flight with with waypoints
     */
    public void flight() {

        // Returns, the dragon won't move
        if (!move)
            return;

        // Init move variables
        double myX = locX;
        double myY = locY;
        double myZ = locZ;

        if ((int) myX != (int) target.getX()) {
            if (myX < target.getX()) {
                myX += XTick;
            } else {
                myX -= XTick;
            }
        }

        if ((int) myY != (int) target.getY()) {
            if (myY < target.getY()) {
                myY += YTick;
            } else {
                myY -= YTick;
            }
        }

        if ((int) myZ != (int) target.getZ()) {
            if (myZ < target.getZ()) {
                myZ += ZTick;
            } else {
                myZ -= ZTick;
            }
        }

        // If myZ = toZ, then we will load the next waypoint or
        // finish the flight, in case it was the last waypoint to fly
        if (((int) myZ >= (int) target.getZ() - 2 && (int) myZ <= (int) target.getZ() + 2)
                && ((int) myY >= (int) target.getY() - 2 && (int) myY <= (int) target.getY() + 2)
                && ((int) myX >= (int) target.getX() - 2 && (int) myX <= (int) target.getX() + 2)) {
            Waypoint wp = flight.getNextWaypoint();

            // Removing the entity and dismouting the player
            if (wp == null) {

                if (passenger != null) {
                    Bukkit.getPluginManager().callEvent(new DragonLandEvent(passenger.getBukkitEntity()));
                }
                Travels.removePlayerAndDragon(flyingPlayer);
                return;
            }

            this.startX = locX;
            this.startY = locY;
            this.startZ = locZ;

            target.setX(wp.getX());
            target.setY(wp.getY());
            target.setZ(wp.getZ());
            setMoveFlight();
            yaw = getCorrectYaw(target.getX(), target.getZ());
            return;
        }

        setPosition(myX, myY, myZ);
    }

    /**
     * Is called during normal Travel
     */
    public void travel() {

        // Returns, the dragon won't move
        if (!move)
            return;

        Entity entity = getBukkitEntity();

        if (entity.getPassenger() == null)
            return;

        double myX = locX;
        double myY = locY;
        double myZ = locZ;

        if (finalMove) {

            // Flying down on end
            if ((int) locY > (int) target.getY())
                myY -= RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightSpeed;

                // Flying up on end
            else if ((int) locY < (int) target.getY())
                myY += RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightSpeed;

                // Removing entity
            else {

                if (passenger != null) {
                    Bukkit.getPluginManager().callEvent(new DragonLandEvent(passenger.getBukkitEntity()));
                }
                Travels.removePlayerAndDragon(flyingPlayer);
                return;
            }

            setPosition(myX, myY, myZ);
            return;
        }

        // Getting the correct height
        if ((int) locY < maxY) {
            myY += RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightSpeed;
        }

        if (myX < target.getX()) {
            myX += XTick;
        } else {
            myX -= XTick;
        }

        if (myZ < target.getZ()) {
            myZ += ZTick;
        } else {
            myZ -= ZTick;
        }

        if ((int) myZ == (int) target.getZ())
            finalMove = true;

        setPosition(myX, myY, myZ);
    }

    /*
     * Is called during dynamic flight
     */
    public void dynamic() {

        // Returns, the dragon won't move
        if (!move)
            return;

        // Init move variables
        double myX = locX;
        double myY = locY;
        double myZ = locZ;

        if ((int) myX != (int) target.getX()) {
            if (myX < target.getX()) {
                myX += XTick;
            } else {
                myX -= XTick;
            }
        }

        if ((int) myY != (int) target.getY()) {
            if (myY < target.getY()) {
                myY += YTick;
            } else {
                myY -= YTick;
            }
        }

        if ((int) myZ != (int) target.getZ()) {
            if (myZ < target.getZ()) {
                myZ += ZTick;
            } else {
                myZ -= ZTick;
            }
        }

        // check if checkpoint reached and load next one. If nothing over -> end flight
        if (((int) myZ >= (int) target.getZ() - 2 && (int) myZ <= (int) target.getZ() + 2)
                && ((int) myY >= (int) target.getY() - 2 && (int) myY <= (int) target.getY() + 2)
                && ((int) myX >= (int) target.getX() - 2 && (int) myX <= (int) target.getX() + 2)) {

            Location nextCheckpoint = null;
            routeIndex++;
            if(routeIndex < route.size()) {
                nextCheckpoint = route.get(routeIndex);
            }

            // removing the entity and dismounting the player
            if (nextCheckpoint == null) {

                if (passenger != null) {
                    Bukkit.getPluginManager().callEvent(new DragonLandEvent(passenger.getBukkitEntity()));
                }
                Travels.removePlayerAndDragon(flyingPlayer);
                return;
            }

            // optimize next checkpoint (dynamic routing!!!)
            if(RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.useDynamicRouting) {
                FlightNavigator.INST.optimizeCheckpoint(routeIndex + 1, route);
            }

            this.startX = myX;
            this.startY = myY;
            this.startZ = myZ;

            target.setX(nextCheckpoint.getX());
            target.setY(nextCheckpoint.getY());
            target.setZ(nextCheckpoint.getZ());
            setMoveFlight();
            yaw = getCorrectYaw(target.getX(), target.getZ());
            return;
        }

        setPosition(myX, myY, myZ);
    }

    /*
     * Is called during flight controlled by players line of sight
     */
    public void controlled() {

        // Returns, the dragon won't move
        if (!move)
            return;

        // Init move variables
        double myX = locX;
        double myY = locY;
        double myZ = locZ;

        if(toggleControl) {
            toggleControl = false;
            // stop dragon
            if(currentlyControlled) {
                target.setX(locX);
                target.setY(locY);
                target.setZ(locZ);
                currentlyControlled = false;
                return;
            }
            else {
                currentlyControlled = true;
            }
        }

        // set landing place
        if(landingPlace != null) {
            target.setX(landingPlace.getX());
            target.setY(landingPlace.getY());
            target.setZ(landingPlace.getZ());
            landingPlace = null;
            currentlyControlled = false;
        }

        // check if landing place is reached
        if(landing &&
                ((int) myZ >= (int) target.getZ() - 2 && (int) myZ <= (int) target.getZ() + 2)
                && ((int) myY >= (int) target.getY() - 2 && (int) myY <= (int) target.getY() + 2)
                && ((int) myX >= (int) target.getX() - 2 && (int) myX <= (int) target.getX() + 2)) {
            if(entity.getPassenger() != null) {
                Bukkit.getPluginManager().callEvent(new DragonLandEvent(passenger.getBukkitEntity()));
            }
            Travels.removePlayerAndDragon(flyingPlayer);
            return;
        }

        // set new target direction if controlled by player
        if (currentlyControlled) {

            this.startX = locX;
            this.startY = locY;
            this.startZ = locZ;

            if(!(entity.getPassenger() instanceof Player)) {
                return;
            }
            Player player = (Player) entity.getPassenger();

            Location target = player.getTargetBlock(null, RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.controlledTargetDistance).getLocation();

            target.setX(target.getX());
            target.setY(target.getY());
            target.setZ(target.getZ());

            if(target.getY() > entity.getWorld().getMaxHeight()) {
                target.setY(entity.getWorld().getMaxHeight());
            }

            setMoveControlled();
            yaw = getCorrectYaw(target.getX(), target.getZ());
        }

        if ((int) myX != (int) target.getX()) {
            if (myX < target.getX()) {
                myX += XTick;
            } else {
                myX -= XTick;
            }
        }

        if ((int) myY != (int) target.getY()) {
            if (myY < target.getY()) {
                myY += YTick;
            } else {
                myY -= YTick;
                // speed up landing
                if(landing) {
                    myY -= 0.5;
                }
            }
        }

        if ((int) myZ != (int) target.getZ()) {
            if (myZ < target.getZ()) {
                myZ += ZTick;
            } else {
                myZ -= ZTick;
            }
        }

        setPosition(myX, myY, myZ);
    }

    public double x_() {

        return 3;
    }

    public void cancelDurationTask() {
        Bukkit.getScheduler().cancelTask(durationTaskId);
    }

    public void toggleControlled() {

        toggleControl = true;
        landing = false;
    }

    public void land() {

        Block targetBlock = getBukkitEntity().getLocation().getBlock();

        // search first hard block
        while(targetBlock.getType() == Material.AIR) {
            targetBlock = targetBlock.getRelative(0, -1, 0);
        }
        targetBlock = targetBlock.getRelative(0, -5, 0);

        landingPlace = new Waypoint(targetBlock.getLocation());
        landing = true;
    }

    public boolean isForceLanding() {

        return forceLanding;
    }

    public boolean isLanding() {

        return landing;
    }

    public FLIGHT_TYPE getFlightType() {

        return flightType;
    }
}
package de.raidcraft.dragontravelplus.dragoncontrol.dragon;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
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

public class RCDragon extends EntityEnderDragon {

    // Travel
    private double toX;
    private double toY;
    private double toZ;
    private int maxY;
    private boolean finalmove = false;
    private boolean move = false;

    // Flight
    private Flight flight;
    private Waypoint firstwp;

    // First Waypoint coords
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

    // Basics
    boolean isFlight = false;
    boolean isTravel = false;
    boolean isControlled = false;
    Entity entity;

    // Start Location
    Location start;

    public RCDragon(Location loca, World notchWorld) {

        super(notchWorld);

        this.start = loca;
        setPosition(loca.getX(), loca.getY(), loca.getZ());
        yaw = loca.getYaw() + 180;
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

    public void startTravel(Location loc) {

        toX = loc.getBlockX();
        toY = loc.getBlockY();
        toZ = loc.getBlockZ();

        this.startX = start.getX();
        this.startY = start.getY();
        this.startZ = start.getZ();

        maxY = RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightHeight;

        setMoveTravel();
        yaw = getCorrectYaw(toX, toZ);
        isTravel = true;
        move = true;
    }

    public void startFlight(Flight flight) {

        entity = getBukkitEntity();

        this.flight = flight;

        this.firstwp = flight.getFirstWaypoint();
        this.fwpX = firstwp.getX();
        this.fwpY = firstwp.getY();
        this.fwpZ = firstwp.getZ();

        this.startX = start.getX();
        this.startY = start.getY();
        this.startZ = start.getZ();

        toX = fwpX;
        toY = fwpY;
        toZ = fwpZ;

        setMoveFlight();
        yaw = getCorrectYaw(toX, toZ);
        move = true;
        isFlight = true;
    }

    public void startControlled(ControlledFlight controlledFlight) {

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

        toX = startX;
        toY = startY;
        toZ = startZ;

        setMoveControlled();
        yaw = getCorrectYaw(toX, toZ);
        move = true;
        isControlled = true;
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

        this.distanceX = this.startX - toX;
        this.distanceY = this.startY - toY;
        this.distanceZ = this.startZ - toZ;

        double tick = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY) + (distanceZ * distanceZ)) / RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightSpeed;
        YTick = Math.abs(distanceY) / tick;
        XTick = Math.abs(distanceX) / tick;
        ZTick = Math.abs(distanceZ) / tick;
    }

    /**
     * Sets the x,z move for each tick
     */
    public void setMoveTravel() {

        this.distanceX = this.startX - toX;
        this.distanceY = this.startY - toY;
        this.distanceZ = this.startZ - toZ;

        double tick = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY) + (distanceZ * distanceZ)) / RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightSpeed;
        XTick = Math.abs(distanceX) / tick;
        ZTick = Math.abs(distanceZ) / tick;
    }

    public void setMoveControlled() {

        this.distanceX = this.startX - toX;
        this.distanceY = this.startY - toY;
        this.distanceZ = this.startZ - toZ;

        double tick = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY) + (distanceZ * distanceZ)) / RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.controlledFlightSpeed;
        YTick = Math.abs(distanceY) / tick;
        XTick = Math.abs(distanceX) / tick;
        ZTick = Math.abs(distanceZ) / tick;
    }

    @Override
    public void c() {

        // Travel
        if(isTravel) {
            travel();
            return;
        }

        // Flight
        if(isFlight) {
            flight();
        }

        // Controlled flight
        if(isControlled) {
            control();
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

        if ((int) myX != (int) toX) {
            if (myX < toX) {
                myX += XTick;
            } else {
                myX -= XTick;
            }
        }

        if ((int) myY != (int) toY) {
            if (myY < toY) {
                myY += YTick;
            } else {
                myY -= YTick;
            }
        }

        if ((int) myZ != (int) toZ) {
            if (myZ < toZ) {
                myZ += ZTick;
            } else {
                myZ -= ZTick;
            }
        }

        // If myZ = toZ, then we will load the next waypoint or
        // finish the flight, in case it was the last waypoint to fly
        if (((int) myZ >= (int) toZ - 2 && (int) myZ <= (int) toZ + 2)
                && ((int) myY >= (int) toY - 2 && (int) myY <= (int) toY + 2)
                && ((int) myX >= (int) toX - 2 && (int) myX <= (int) toX + 2)) {
            Waypoint wp = flight.getNextWaypoint();

            // Removing the entity and dismouting the player
            if (wp == null) {

                if (passenger != null) {
                    Bukkit.getPluginManager().callEvent(new DragonLandEvent(passenger.getBukkitEntity()));
                }
                Travels.removePlayerandDragon(entity);
                return;
            }

            this.startX = locX;
            this.startY = locY;
            this.startZ = locZ;

            toX = wp.getX();
            toY = wp.getY();
            toZ = wp.getZ();
            setMoveFlight();
            yaw = getCorrectYaw(toX, toZ);
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

        if (finalmove) {

            // Flying down on end
            if ((int) locY > (int) toY)
                myY -= RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightSpeed;

                // Flying up on end
            else if ((int) locY < (int) toY)
                myY += RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightSpeed;

                // Removing entity
            else {

                if (passenger != null) {
                    Bukkit.getPluginManager().callEvent(new DragonLandEvent(passenger.getBukkitEntity()));
                }
                Travels.removePlayerandDragon(entity);
                return;
            }

            setPosition(myX, myY, myZ);
            return;
        }

        // Getting the correct height
        if ((int) locY < maxY) {
            myY += RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightSpeed;
        }

        if (myX < toX) {
            myX += XTick;
        } else {
            myX -= XTick;
        }

        if (myZ < toZ) {
            myZ += ZTick;
        } else {
            myZ -= ZTick;
        }

        if ((int) myZ == (int) toZ)
            finalmove = true;

        setPosition(myX, myY, myZ);
    }

    /*
     * Is called during flight controlled by players line of sight
     */
    public void control() {

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
                toX = locX;
                toY = locY;
                toZ = locZ;
                currentlyControlled = false;
                return;
            }
            else {
                currentlyControlled = true;
            }
        }

        // set landing place
        if(landingPlace != null) {
            toX = landingPlace.getX();
            toY = landingPlace.getY();
            toZ = landingPlace.getZ();
            landingPlace = null;
            currentlyControlled = false;
        }

        // check if landing place is reached
        if(landing &&
                ((int) myZ >= (int) toZ - 2 && (int) myZ <= (int) toZ + 2)
                && ((int) myY >= (int) toY - 2 && (int) myY <= (int) toY + 2)
                && ((int) myX >= (int) toX - 2 && (int) myX <= (int) toX + 2)) {
            if(entity.getPassenger() != null) {
                Bukkit.getPluginManager().callEvent(new DragonLandEvent(passenger.getBukkitEntity()));
            }
            Travels.removePlayerandDragon(entity);
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

            Location target = player.getTargetBlock(null, 20).getLocation();

            toX = target.getX();
            toY = target.getY();
            toZ = target.getZ();
            setMoveControlled();
            yaw = getCorrectYaw(toX, toZ);
        }

        if ((int) myX != (int) toX) {
            if (myX < toX) {
                myX += XTick;
            } else {
                myX -= XTick;
            }
        }

        if ((int) myY != (int) toY) {
            if (myY < toY) {
                myY += YTick;
            } else {
                myY -= YTick;
                // speed up landing
                if(landing) {
                    myY -= 0.5;
                }
            }
        }

        if ((int) myZ != (int) toZ) {
            if (myZ < toZ) {
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

        Block targetBLock = getBukkitEntity().getLocation().getBlock();

        // search first hard block
        while(targetBLock.getType() == Material.AIR) {
            targetBLock = targetBLock.getRelative(0, -1, 0);
        }
        targetBLock = targetBLock.getRelative(0, -5, 0);

        landingPlace = new Waypoint(targetBLock.getLocation());
        landing = true;
    }

    public boolean isForceLanding() {

        return forceLanding;
    }

    public boolean isLanding() {

        return landing;
    }

    public boolean isControlled() {

        return isControlled;
    }
}
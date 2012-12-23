package de.raidcraft.dragontravelplus.dragoncontrol.dragon;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.modules.Travels;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Flight;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Waypoint;
import de.raidcraft.dragontravelplus.events.DragonLandEvent;
import net.minecraft.server.v1_4_6.EntityEnderDragon;
import net.minecraft.server.v1_4_6.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

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

	// Start points for tick calculation
	private double startX;
	private double startY;
	private double startZ;

	// Basics
	boolean isFlight = false;
	boolean isTravel = false;
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

		maxY = DragonTravelPlusModule.inst.config.flightHeight;

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

		double tick = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY) + (distanceZ * distanceZ)) / DragonTravelPlusModule.inst.config.flightSpeed;
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

		double tick = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY) + (distanceZ * distanceZ)) / DragonTravelPlusModule.inst.config.flightSpeed;
		XTick = Math.abs(distanceX) / tick;
		ZTick = Math.abs(distanceZ) / tick;
	}

	@Override
	public void c() {

		// Travel
		if (isTravel) {
			travel();
			return;
		}

		// Flight
		if (isFlight) {
			flight();
		}
	}

	/**
	 * Flight with waypoints
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
		if (((int) myZ >= (int) toZ-2 &&  (int)myZ <= (int) toZ+2)
                && ((int) myY >= (int) toY-2 &&  (int)myY <= (int) toY+2)
                && ((int) myX >= (int) toX-2 &&  (int)myX <= (int) toX+2)) {
			Waypoint wp = flight.getNextWaypoint();

			// Removing the entity and dismouting the player
			if (wp == null) {

                if(passenger != null) {
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
	 * Normal Travel
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
				myY -= DragonTravelPlusModule.inst.config.flightSpeed;

			// Flying up on end
			else if ((int) locY < (int) toY)
				myY += DragonTravelPlusModule.inst.config.flightSpeed;

			// Removing entity
			else {

                if(passenger != null) {
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
			myY += DragonTravelPlusModule.inst.config.flightSpeed;
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

	public double x_() {
		return 3;
	}
}
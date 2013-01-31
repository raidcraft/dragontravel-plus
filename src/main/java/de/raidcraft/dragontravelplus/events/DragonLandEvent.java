package de.raidcraft.dragontravelplus.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Author: Philip
 * Date: 16.12.12 - 22:24
 * Description:
 */
public class DragonLandEvent extends Event {

    private Entity passenger;

    public DragonLandEvent(Entity passenger) {

        this.passenger = passenger;
    }

    public Entity getPassenger() {

        return passenger;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}

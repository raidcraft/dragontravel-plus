package de.raidcraft.dragontravelplus.events;

import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Flight;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Author: Philip
 * Date: 25.12.12 - 20:48
 * Description:
 */
public class RoutingFinishedEvent extends Event {

    private FlyingPlayer flyingPlayer;
    private Flight flight;

    public RoutingFinishedEvent(FlyingPlayer flyingPlayer, Flight flight) {

        this.flyingPlayer = flyingPlayer;
        this.flight = flight;
    }

    public FlyingPlayer getFlyingPlayer() {

        return flyingPlayer;
    }

    public Flight getFlight() {

        return flight;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}

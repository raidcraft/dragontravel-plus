package de.raidcraft.dragontravelplus.api.passenger;

import de.raidcraft.dragontravelplus.api.flight.Flight;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public class AbstractPassenger<T extends LivingEntity> implements Passenger<T> {

    private final T entity;
    private Flight flight;

    public AbstractPassenger(T entity) {

        this.entity = entity;
    }

    @Override
    public String getName() {

        return getEntity().getCustomName();
    }

    @Override
    public T getEntity() {

        return entity;
    }

    @Override
    public void setFlight(Flight flight) {

        this.flight = flight;
    }

    @Override
    public Flight getFlight() {

        return flight;
    }

    @Override
    public boolean hasFlight() {

        return getFlight() != null;
    }

    @Override
    public boolean isFlying() {

        return getFlight() != null && getFlight().isActive();
    }
}

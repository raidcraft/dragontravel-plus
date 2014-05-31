package de.raidcraft.dragontravelplus.passengers;

import de.raidcraft.api.flight.passenger.AbstractPassenger;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class BukkitPlayerPassenger extends AbstractPassenger<Player> {

    public BukkitPlayerPassenger(Player entity) {

        super(entity);
    }

    @Override
    public void sendMessage(String message) {

        getEntity().sendMessage(message);
    }
}

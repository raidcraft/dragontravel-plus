package de.raidcraft.dragontravelplus.passengers;

import de.raidcraft.dragontravelplus.api.passenger.AbstractPassenger;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class BukkitPlayerPassenger extends AbstractPassenger<Player> {

    public BukkitPlayerPassenger(Player entity) {

        super(entity);
    }
}

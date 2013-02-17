package de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement;

import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.modules.Travels;
import org.bukkit.entity.Player;

public class FlightTravel {

    public static void flyFlight(Flight flight, Player player) {

        // Mounting the player
        if (!Travels.mountDragon(player))
            return;

        // Getting the dragon
        RCDragon dragon = DragonManager.INST.flyingPlayers.get(player).getDragon();

        if (dragon == null)
            return;

        dragon.startFlight(flight);
    }

    public static void flyControlled(ControlledFlight controlledFlight, Player player) {

        // Mounting the player
        if (!Travels.mountDragon(player))
            return;

        // Getting the dragon
        RCDragon dragon = DragonManager.INST.flyingPlayers.get(player).getDragon();

        if (dragon == null)
            return;

        dragon.startControlled(controlledFlight);
    }
}

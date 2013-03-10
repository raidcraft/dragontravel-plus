package de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement;

import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.modules.Travels;
import org.bukkit.entity.Player;

public class FlightTravel {

    public static void flyFlight(Flight flight, Player player) {

        // Mounting the player
        if (!Travels.mountDragon(player))
            return;

        FlyingPlayer flyingPlayer = DragonManager.INST.flyingPlayers.get(player);

        // Getting the dragon
        RCDragon dragon = flyingPlayer.getDragon();

        if (dragon == null)
            return;

        dragon.startFlight(flyingPlayer, flight);
    }

    public static void flyControlled(ControlledFlight controlledFlight, Player player) {

        // Mounting the player
        if (!Travels.mountDragon(player))
            return;

        FlyingPlayer flyingPlayer = DragonManager.INST.flyingPlayers.get(player);

        // Getting the dragon
        RCDragon dragon = flyingPlayer.getDragon();

        if (dragon == null)
            return;

        dragon.startControlled(flyingPlayer, controlledFlight);
    }
}

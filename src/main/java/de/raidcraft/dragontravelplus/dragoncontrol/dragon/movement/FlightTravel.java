package de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.modules.Travels;
import de.raidcraft.dragontravelplus.flight.Flight;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlightTravel {

    public static void flyFlight(Flight flight, Player player) {
        flyFlight(flight, player, 0);
    }

    public static void flyFlight(Flight flight, Player player, int delay) {

        FlyingPlayer flyingPlayer = DragonManager.INST.getFlyingPlayer(player.getName());

        if(flyingPlayer != null && flyingPlayer.isInAir()) {
            return;
        }

        flyingPlayer = new FlyingPlayer(player, player.getLocation());
        DragonManager.INST.flyingPlayers.put(player, flyingPlayer);

        // port player to start
        Location departure = flight.getFirstWaypointCopy().getLocation();
        if(player.getLocation().getWorld().getName().equalsIgnoreCase(departure.getWorld().getName())
                && player.getLocation().distance(departure) > 15) {
            Location location = flight.getFirstWaypointCopy().getLocation();
            location.setY(location.getY() + 4);
            player.teleport(location);
            delay += 20;
        }

        StartFlightTask startFlightTask = new StartFlightTask(flyingPlayer, flight);
        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(DragonTravelPlusPlugin.class), startFlightTask, delay);
    }

    public static void flyControlled(ControlledFlight controlledFlight, Player player) {

        FlyingPlayer flyingPlayer = DragonManager.INST.getFlyingPlayer(player.getName());

        if(flyingPlayer != null && flyingPlayer.isInAir()) {
            return;
        }

        flyingPlayer = new FlyingPlayer(player, player.getLocation());
        flyingPlayer.setInAir(true);
        DragonManager.INST.flyingPlayers.put(player, flyingPlayer);

        // Mounting the player
        if (!Travels.mountDragon(player))
            return;

        // Getting the dragon
        RCDragon dragon = flyingPlayer.getDragon();

        if (dragon == null)
            return;

        dragon.startControlled(flyingPlayer, controlledFlight);
    }

    public static class StartFlightTask implements Runnable {

        private FlyingPlayer flyingPlayer;
        private Flight flight;

        public StartFlightTask(FlyingPlayer flyingPlayer, Flight flight) {

            this.flyingPlayer = flyingPlayer;
            this.flight = flight;
        }

        public void run() {

            // Mounting the player
            if (!Travels.mountDragon(flyingPlayer.getPlayer()))
                return;

            // Getting the dragon
            RCDragon dragon = flyingPlayer.getDragon();

            if (dragon == null)
                return;

            // start flight
            flyingPlayer.setInAir(true);
            dragon.startFlight(flyingPlayer, flight);
        }
    }
}

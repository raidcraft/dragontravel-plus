package de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.PlayerChecker;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.modules.Travels;
import de.raidcraft.dragontravelplus.flight.Flight;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class FlightTravel {

    public static void flyFlight(Flight flight, FlyingPlayer flyingPlayer, double speed) {

        flyFlight(flight, flyingPlayer, speed, 0);
    }

    public static void flyFlight(Flight flight, Player player, double speed) {

        FlyingPlayer flyingPlayer = DragonManager.INST.getFlyingPlayer(player.getName());
        flyFlight(flight, flyingPlayer, speed, 0);
    }

    public static void flyFlight(Flight flight, Player player, double speed, int delay) {

        FlyingPlayer flyingPlayer = DragonManager.INST.getFlyingPlayer(player.getName());
        if(flyingPlayer == null) {
            flyingPlayer = new FlyingPlayer(player, player.getLocation());
        }
        flyFlight(flight, flyingPlayer, speed, delay);
    }

    public static void flyFlight(Flight flight, FlyingPlayer flyingPlayer, double speed, int delay) {

        Player player = flyingPlayer.getPlayer();

        if(flyingPlayer == null || flyingPlayer.isInAir()) {
            return;
        }

        DragonManager.INST.setFlyingPlayer(flyingPlayer);

        // port player to start
        Location departure = flight.getWayPoint(0).getLocation();
        if(player.getLocation().getWorld().getName().equalsIgnoreCase(departure.getWorld().getName())
                && player.getLocation().distance(departure) > 15) {
            departure.setY(departure.getY() + 4);
            player.teleport(departure);
            delay += 20;
        }

        StartFlightTask startFlightTask = new StartFlightTask(flyingPlayer, flight, speed);
        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(DragonTravelPlusPlugin.class), startFlightTask, delay);
    }

    public static void flyControlled(ControlledFlight controlledFlight, Player player) {

        FlyingPlayer flyingPlayer = DragonManager.INST.getFlyingPlayer(player.getName());

        if(flyingPlayer != null && flyingPlayer.isInAir()) {
            return;
        }

        flyingPlayer = new FlyingPlayer(player, player.getLocation());
        flyingPlayer.setInAir(true);
        DragonManager.INST.setFlyingPlayer(flyingPlayer);

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
        private double speed;

        public StartFlightTask(FlyingPlayer flyingPlayer, Flight flight, double speed) {

            this.flyingPlayer = flyingPlayer;
            this.flight = flight;
            this.speed = speed;
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
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new PlayerChecker(flyingPlayer), 0, 10);
            flyingPlayer.setCheckerTask(task);
            dragon.setCustomNameVisible(false);
            dragon.startFlight(flyingPlayer, flight, speed);
        }
    }
}

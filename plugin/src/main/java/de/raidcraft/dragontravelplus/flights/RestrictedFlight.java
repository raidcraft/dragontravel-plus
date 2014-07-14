package de.raidcraft.dragontravelplus.flights;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.flight.AbstractFlight;
import de.raidcraft.api.flight.flight.FlightException;
import de.raidcraft.api.flight.flight.Path;
import de.raidcraft.api.language.Translator;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.FlightManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.util.Arrays;

/**
 * @author Silthus
 */
public abstract class RestrictedFlight extends AbstractFlight implements Listener {

    public RestrictedFlight(Aircraft<?> aircraft, Path path, Location startLocation, Location endLocation) {

        super(aircraft, path, startLocation, endLocation);
    }

    @Override
    public long getMoveInterval() {

        return RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().flightTaskInterval;
    }

    // TODO: player check
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent event) {

        if (!hasPassenger(event.getPlayer())) {
            return;
        }
        if (Arrays.asList(RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().exitWords).contains(event.getMessage())) {
            abortFlight();
            event.setCancelled(true);
        }
    }

    // TODO: doku
    // TODO: performance - gilt das für alle player?
    // TODO: player check
    // necessary? einfach vom Spieler immer deaktivieren
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {

        if (hasPassenger(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    // TODO: player check, immer Schaden deaktivieren?
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        // cancel damage done to the passenger
        if (hasPassenger((LivingEntity) event.getEntity())) {
            event.setCancelled(true);
        }
        // cancel damage done to the aircraft
        if (getAircraft() != null && event.getEntity().equals(getAircraft().getBukkitEntity())) {
            event.setCancelled(true);
        }
    }                    #

    // TODO: player check
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onCommand(PlayerCommandPreprocessEvent event) {

        if (!hasPassenger(event.getPlayer())) {
            return;
        }
        for (String cmd : RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().forbiddenCommands) {
            if (event.getMessage().toLowerCase().startsWith("/" + cmd.toLowerCase())) {
                Translator.msg(DragonTravelPlusPlugin.class, event.getPlayer(),
                        "flight.blocked-cmd", "You are now allowed to use this command while flying.");
                event.setCancelled(true);
                return;
            }
        }
    }

    // TODO: player check
    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOW)
    public void onVehicleExit(VehicleExitEvent event) {

        if (hasPassenger(event.getExited())) {
            // abort the flight
            abortFlight();
            event.setCancelled(true);
        }
    }

    // TODO: player check
    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOW)
    public void onPlayerCrouch(PlayerToggleSneakEvent event) {

        if (hasPassenger(event.getPlayer())) {
            // abort the flight
            abortFlight();
            event.setCancelled(true);
        }
    }

    // TODO: player check
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (hasPassenger(event.getPlayer())) {
            abortFlight();
        }
    }

    // TODO: player check und entity check
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDeath(EntityDeathEvent event) {

        if (hasPassenger(event.getEntity())) {
            abortFlight();
        }
    }

    @Override
    public void onStartFlight() throws FlightException {

        RaidCraft.getComponent(FlightManager.class).registerFlight(this);
        Bukkit.getPluginManager().registerEvents(this, RaidCraft.getComponent(DragonTravelPlusPlugin.class));
    }

    @Override
    public void onAbortFlight() throws FlightException {

        RaidCraft.getComponent(FlightManager.class).unregisterFlight(this);
        unregisterListener();
    }

    @Override
    public void onEndFlight() throws FlightException {

        RaidCraft.getComponent(FlightManager.class).unregisterFlight(this);
        unregisterListener();
    }

    private void unregisterListener() {

        // unregister a little bit later to catch fall damage and such
        // TODO: sideeffects
        DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {

                HandlerList.unregisterAll(RestrictedFlight.this);
            }
        }, plugin.getConfig().flightTimeout);
    }
}
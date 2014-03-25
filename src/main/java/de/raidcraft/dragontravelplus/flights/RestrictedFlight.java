package de.raidcraft.dragontravelplus.flights;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.language.Translator;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.FlightManager;
import de.raidcraft.dragontravelplus.api.aircraft.Aircraft;
import de.raidcraft.dragontravelplus.api.flight.AbstractFlight;
import de.raidcraft.dragontravelplus.api.flight.FlightException;
import de.raidcraft.dragontravelplus.api.flight.Path;
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
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.util.Arrays;

/**
 * @author Silthus
 */
public abstract class RestrictedFlight extends AbstractFlight implements Listener {

    public RestrictedFlight(Aircraft<?> aircraft, Path path, Location startLocation, Location endLocation) {

        super(aircraft, path, startLocation, endLocation);
    }

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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {

        if (hasPassenger(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        if (hasPassenger((LivingEntity) event.getEntity())) {
            event.setCancelled(true);
        }
    }

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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onVehicleExit(VehicleExitEvent event) {

        if (hasPassenger(event.getExited())) {
            // abort the flight
            abortFlight();
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (hasPassenger(event.getPlayer())) {
            abortFlight();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDeath(EntityDeathEvent event) {

        if (hasPassenger(event.getEntity())) {
            abortFlight();
        }
    }

    @Override
    public void startFlight() throws FlightException {

        super.startFlight();
        RaidCraft.getComponent(FlightManager.class).registerFlight(this);
        Bukkit.getPluginManager().registerEvents(this, RaidCraft.getComponent(DragonTravelPlusPlugin.class));
    }

    @Override
    public void abortFlight() {

        super.abortFlight();
        RaidCraft.getComponent(FlightManager.class).unregisterFlight(this);
        unregisterListener();
    }

    @Override
    public void endFlight() throws FlightException {

        super.endFlight();
        RaidCraft.getComponent(FlightManager.class).unregisterFlight(this);
        unregisterListener();
    }

    private void unregisterListener() {

        // unregister a little bit later to catch fall damage and such
        DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {

                HandlerList.unregisterAll(RestrictedFlight.this);
            }
        }, plugin.getConfig().flightTimeout);
    }
}

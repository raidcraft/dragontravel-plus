package de.raidcraft.dragontravelplus.flights;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.language.Translator;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.api.aircraft.Aircraft;
import de.raidcraft.dragontravelplus.api.flight.AbstractFlight;
import de.raidcraft.dragontravelplus.api.flight.FlightException;
import de.raidcraft.dragontravelplus.api.flight.Path;
import org.bukkit.Bukkit;
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

    public RestrictedFlight(Aircraft<?> aircraft, Path path) {

        super(aircraft, path);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent event) {

        if (!getAircraft().containsPassenger(event.getPlayer())) {
            return;
        }
        if (Arrays.asList(RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().exitWords).contains(event.getMessage())) {
            try {
                abortFlight();
                event.setCancelled(true);
            } catch (FlightException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {

        if (getAircraft().containsPassenger(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        if (getAircraft().containsPassenger((LivingEntity) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onCommand(PlayerCommandPreprocessEvent event) {

        if (!getAircraft().containsPassenger(event.getPlayer())) {
            return;
        }
        for (String cmd : RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().forbiddenCommands) {
            if (event.getMessage().toLowerCase().startsWith("/" + cmd.toLowerCase())) {
                Translator.msg(DragonTravelPlusPlugin.class, event.getPlayer(),                        "flight.blocked-cmd", "You are now allowed to use this command while flying.");
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onVehicleExit(VehicleExitEvent event) {

        if (getAircraft().containsPassenger(event.getExited())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (getAircraft().containsPassenger(event.getPlayer())) {
            try {
                abortFlight();
            } catch (FlightException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDeath(EntityDeathEvent event) {

        if (getAircraft().containsPassenger(event.getEntity())) {
            try {
                abortFlight();
            } catch (FlightException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startFlight() throws FlightException {

        super.startFlight();
        Bukkit.getPluginManager().registerEvents(this, RaidCraft.getComponent(DragonTravelPlusPlugin.class));
    }

    @Override
    public void abortFlight() throws FlightException {

        super.abortFlight();
        HandlerList.unregisterAll(this);
    }

    @Override
    public void endFlight() throws FlightException {

        super.abortFlight();
        HandlerList.unregisterAll(this);
    }
}

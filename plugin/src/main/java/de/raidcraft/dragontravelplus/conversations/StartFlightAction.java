package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.flight.flight.Path;
import de.raidcraft.dragontravelplus.FlightManager;
import de.raidcraft.dragontravelplus.RouteManager;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class StartFlightAction implements Action<Player> {

    private final FlightManager flightManager = RaidCraft.getComponent(FlightManager.class);
    private final RouteManager routeManager = RaidCraft.getComponent(RouteManager.class);

    @Override
    @Information(
            value = "flight.start",
            desc = "Starts a preconfigured named flight.",
            conf = {
                    "flight: <name of the flight>",
                    "delay: 0"
            }
    )
    public void accept(Player player, ConfigurationSection config) {

        String pathName = config.getString("flight");
        Optional<Path> path = routeManager.getPath(pathName);

        if (!path.isPresent()) {
            player.sendMessage(ChatColor.RED + "Unknown flight path " + pathName + " configured in " + ConfigUtil.getFileName(config));
            return;
        }

        flightManager.createFlight(player, path.get()).startDelayedFlight(config.getInt("delay", 0));
    }
}

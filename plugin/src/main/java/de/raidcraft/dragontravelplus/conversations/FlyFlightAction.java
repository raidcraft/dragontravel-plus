package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.conversation.ConversationVariable;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.FlightException;
import de.raidcraft.api.flight.flight.Path;
import de.raidcraft.api.flight.flight.UnknownPathException;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.RouteManager;
import de.raidcraft.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Philip
 */
public class FlyFlightAction implements Action<Player> {

    @Override
    @Information(
            value = "flight.fly",
            desc = "Starts the given flight for the player. Will teleport the player to the start position.",
            conf = {
                    "flight: name of the flight",
                    "delay: how long to delay the takeoff"
            },
            aliases = "DTP_FLIGHT"
    )
    public void accept(Player player, ConfigurationSection config) {

        String flightName = ConversationVariable.getString(player, DTPConversationConstants.FLIGHT_NAME).orElse(config.getString("flight"));
        long delay = TimeUtil.parseTimeAsTicks(config.getString("delay", "1"));

        try {
            DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
            RouteManager routeManager = plugin.getRouteManager();
            Path path = routeManager.getPath(flightName);

            final Flight flight = plugin.getFlightManager().createFlight(plugin.getFlightManager().getPassenger(player), path);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                try {
                    flight.startFlight();
                } catch (FlightException e) {
                    Conversations.message(player, ChatColor.RED + e.getMessage());
                    e.printStackTrace();
                    Conversations.endActiveConversation(player, ConversationEndReason.ERROR);
                }
            }, delay);
        } catch (UnknownPathException e) {
            e.printStackTrace();
            Conversations.endActiveConversation(player, ConversationEndReason.ERROR);
        }
    }
}

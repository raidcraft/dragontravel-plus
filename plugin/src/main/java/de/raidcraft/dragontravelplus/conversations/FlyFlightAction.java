package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.FlightException;
import de.raidcraft.api.flight.flight.Path;
import de.raidcraft.api.flight.flight.UnknownPathException;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.RouteManager;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.action.WrongArgumentValueException;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.conversations.EndReason;
import de.raidcraft.rcconversations.util.ParseString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * @author Philip
 */
@ActionInformation(name = "DTP_FLIGHT")
public class FlyFlightAction extends AbstractAction {

    @Override
    public void run(final Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String flightName = args.getString("flight");
        flightName = ParseString.INST.parse(conversation, flightName);
        int delay = args.getInt("delay", 0);

        try {
            DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
            RouteManager routeManager = plugin.getRouteManager();
            Path path = routeManager.getPath(flightName);

            double speed = args.getDouble("speed", 1);
            final Flight flight = plugin.getFlightManager().createFlight(plugin.getFlightManager().getPassenger(conversation.getPlayer()), path);

            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {

                    try {
                        flight.startFlight();
                    } catch (FlightException e) {
                        conversation.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }, delay);
        } catch (UnknownPathException e) {
            conversation.endConversation(EndReason.FAILURE);
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Flight '" + flightName + "' does not exists!");
        }
    }
}

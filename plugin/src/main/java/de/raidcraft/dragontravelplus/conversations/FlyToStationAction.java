package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.conversation.ConversationVariable;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.FlightException;
import de.raidcraft.api.flight.passenger.Passenger;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.StationManager;
import de.raidcraft.dragontravelplus.flights.PayedFlight;
import de.raidcraft.dragontravelplus.routes.Route;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.api.station.UnknownStationException;
import de.raidcraft.util.ConfigUtil;
import de.raidcraft.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class FlyToStationAction implements Action<Player> {

    @Override
    @Information(
            value = "station.goto",
            desc = "Starts a flight to the given station from the current location of the player.",
            conf = {
                    "target: <target station>",
                    "price: [0]",
                    "delay: until the dragon takes off",
                    "start: optional start station - if none is defined the current player position will be used"
            },
            aliases = {"DTP_STATION"}
    )
    public void accept(Player player, ConfigurationSection config) {

        String targetName = ConversationVariable.getString(player, DTPConversationConstants.STATION_TARGET_NAME).orElse(config.getString("target"));
        String priceString = ConversationVariable.getString(player, DTPConversationConstants.PRICE).orElse(config.getString("price"));
        String startName = ConversationVariable.getString(player, DTPConversationConstants.STATION_SOURCE_NAME).orElse(config.getString("start"));
        long delay = TimeUtil.parseTimeAsTicks(config.getString("delay"));
        double price = RaidCraft.getEconomy().parseCurrencyInput(priceString);

        DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
        StationManager stationManager = plugin.getStationManager();

        if (price > 0 && !RaidCraft.getEconomy().hasEnough(player.getUniqueId(), price)) {
            player.sendMessage(ChatColor.RED + "Du hast nicht genügend Geld um den Flug anzutreten!");
            Conversations.endActiveConversation(player, ConversationEndReason.ENDED);
            return;
        }

        DragonStation target;
        try {
            target = (DragonStation) stationManager.getStation(targetName);
        } catch (UnknownStationException e) {
            RaidCraft.LOGGER.warning("invalid station target " + targetName + " in " + ConfigUtil.getFileName(config));
            Conversations.endActiveConversation(player, ConversationEndReason.ERROR);
            return;
        }
        Route route;
        try {
            Station start = stationManager.getStation(startName);
            route = plugin.getRouteManager().getRoute(start, target);
        } catch (UnknownStationException e) {
            // lets issue a free flight directly to the target
            route = plugin.getRouteManager().getRoute(player.getLocation(), target);
        }

        Passenger passenger = plugin.getFlightManager().getPassenger(player);
        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new TakeoffDelayedTask(route, passenger, price), delay);
        Conversations.endActiveConversation(player, ConversationEndReason.ENDED);
    }

    public class TakeoffDelayedTask implements Runnable {

        private final Route route;
        private final Passenger passenger;
        private final double price;

        public TakeoffDelayedTask(Route route, Passenger passenger, double price) {

            this.route = route;
            this.passenger = passenger;
            this.price = price;
        }

        @Override
        public void run() {

            try {
                if (price > 0 && !RaidCraft.getEconomy().hasEnough(passenger.getEntity().getUniqueId(), price)) {
                    passenger.getEntity().sendMessage(ChatColor.RED + "Du hast nicht genügend Geld um den Flug anzutreten!");
                    return;
                }
                Flight flight = route.createFlight(passenger);
                if (flight instanceof PayedFlight) {
                    ((PayedFlight) flight).setPrice(price);
                }
                flight.startFlight();
            } catch (FlightException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

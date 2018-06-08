package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.FlightException;
import de.raidcraft.api.flight.passenger.Passenger;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.StationManager;
import de.raidcraft.dragontravelplus.paths.DragonStationRoute;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.rcconversations.RCConversationsPlugin;
import de.raidcraft.rcconversations.api.action.*;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.conversations.EndReason;
import de.raidcraft.rcconversations.util.MathHelper;
import de.raidcraft.rcconversations.util.ParseString;
import de.raidcraft.rctravel.api.station.UnknownStationException;
import org.bukkit.Bukkit;

/**
 * @author Philip
 */
@ActionInformation(name = "DTP_STATION")
public class FlyToStationAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException, UnknownStationException {

        String startName = args.getString("startStage", null);
        String targetName = args.getString("target", null);
        targetName = ParseString.INST.parse(conversation, targetName);
        startName = ParseString.INST.parse(conversation, startName);
        int delay = args.getInt("delay", 0);
        String priceString = args.getString("price", "0");
        priceString = ParseString.INST.parse(conversation, priceString);
        double price = MathHelper.solveDoubleFormula(priceString);

        DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
        StationManager stationManager = plugin.getStationManager();

        DragonStation target = (DragonStation) stationManager.getStation(targetName);
        if (target == null) {
            throw new WrongArgumentValueException("Wrong argument value in withAction '" + getName() + "': Station '" + targetName + "' does not exists!");
        }

        DragonStation start;
        if (startName == null) {
            start = new DragonStation(conversation.getName(), conversation.getPlayer().getLocation().clone());
        } else {
            start = (DragonStation) stationManager.getStation(startName);
        }
        if (start == null) {
            throw new WrongArgumentValueException("Wrong argument value in withAction '" + getName() + "': Station '" + targetName + "' does not exists!");
        }

        DragonStationRoute route = plugin.getRouteManager().getRoute(start, target);
        Passenger passenger = plugin.getFlightManager().getPassenger(conversation.getPlayer());
        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new TakeoffDelayedTask(route, passenger), delay);
    }

    public class TakeoffDelayedTask implements Runnable {

        private DragonStationRoute route;
        private Passenger passenger;

        public TakeoffDelayedTask(DragonStationRoute route, Passenger passenger) {

            this.route = route;
            this.passenger = passenger;
        }

        @Override
        public void run() {

            try {
                RaidCraft.getComponent(RCConversationsPlugin.class).getConversationManager()
                        .endConversation(passenger.getEntity().getUniqueId(), EndReason.SILENT);
                Flight flight = route.createFlight(passenger);
                flight.startFlight();
            } catch (FlightException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

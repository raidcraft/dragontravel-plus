package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightTravel;
import de.raidcraft.dragontravelplus.flight.Flight;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.action.WrongArgumentValueException;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.util.ParseString;

/**
 * @author Philip
 */
@ActionInformation(name = "DTP_FLIGHT")
public class FlyFlightAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String flightName = args.getString("flight");
        flightName = ParseString.INST.parse(conversation, flightName);
        int delay = args.getInt("delay", 0);

        Flight flight = Flight.loadFlight(flightName);

        if(flight == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Flight '" + flightName + "' does not exists!");
        }

        FlightTravel.flyFlight(flight, conversation.getPlayer(), RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightSpeed, delay);
    }
}

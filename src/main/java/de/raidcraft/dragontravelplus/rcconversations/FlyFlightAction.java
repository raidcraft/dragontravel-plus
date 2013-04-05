package de.raidcraft.dragontravelplus.rcconversations;

import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightTravel;
import de.raidcraft.dragontravelplus.flight.Flight;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.WrongArgumentValueException;
import de.raidcraft.rcconversations.api.conversation.Conversation;

/**
 * @author Philip
 */
public class FlyFlightAction extends AbstractAction {

    public FlyFlightAction(String name) {

        super(name);
    }

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws Throwable {

        String flightName = args.getString("flight");
        int delay = args.getInteger("delay", 0);

        Flight flight = Flight.loadFlight(flightName);

        if(flight == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Flight '" + flightName + "' does not exists!");
        }

        FlightTravel.flyFlight(flight, conversation.getPlayer(), delay);
    }
}

package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;

/**
 * @author Philip
 */
@ActionInformation(name = "FIND_DRAGONSTATION")
public class FindDragonstationAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        int radius = args.getInt("radius");
        String success = args.getString("onsuccess", null);
        String failure = args.getString("onfailure", null);

        DragonStation station = StationManager.INST.getNearbyStation(conversation.getHost().getLocation(), radius);

        if(station == null) {
            if(failure != null) {
                conversation.setCurrentStage(failure);
                conversation.triggerCurrentStage();
            }
        }
        else {
            if(success != null) {
                conversation.set("dtp_station_name", station.getName());
                conversation.set("dtp_station_friendlyname", station.getFriendlyName());
                conversation.setCurrentStage(success);
                conversation.triggerCurrentStage();
            }
        }
    }
}

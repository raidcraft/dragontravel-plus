package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import org.bukkit.ChatColor;

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
            if(!StationManager.INST.stationIsFamiliar(conversation.getPlayer(), station)) {
                StationManager.INST.assignStationWithPlayer(conversation.getPlayer().getName(), station);
                conversation.getPlayer().sendMessage(ChatColor.GREEN + "Du besucht diese Drachenstation zum ersten mal!");
            }

            conversation.set("dtp_station_name", station.getName());
            conversation.set("dtp_station_friendlyname", station.getFriendlyName());
            if(success != null) {
                conversation.setCurrentStage(success);
                conversation.triggerCurrentStage();
            }
        }
    }
}

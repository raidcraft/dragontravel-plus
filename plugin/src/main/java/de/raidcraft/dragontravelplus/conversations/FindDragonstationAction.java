package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rctravel.api.station.UnknownStationException;
import org.bukkit.ChatColor;

/**
 * @author Philip
 */
@ActionInformation(name = "FIND_DRAGONSTATION")
public class FindDragonstationAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        try {
            int radius = args.getInt("radius");
            String success = args.getString("onsuccess", null);
            String failure = args.getString("onfailure", null);

            DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
            DragonStation station = (DragonStation) plugin.getStationManager().getNearbyStation(conversation.getHost().getLocation(), radius);

            if (station == null) {
                if (failure != null) {
                    conversation.setCurrentStage(failure);
                    conversation.triggerCurrentStage();
                }
                return;
            }

            if (!station.hasDiscovered(conversation.getPlayer().getName())) {
                station.setDiscovered(conversation.getPlayer().getName(), true);
                conversation.getPlayer().sendMessage(ChatColor.GREEN + "Du besuchst diese Drachenstation zum ersten mal!");
            }

            conversation.set("dtp_station_name", station.getName());
            conversation.set("dtp_station_friendlyname", station.getDisplayName());

            if (success != null) {
                conversation.setCurrentStage(success);
                conversation.triggerCurrentStage();
            }
        } catch (UnknownStationException e) {
            throw new ActionArgumentException(e.getMessage());
        }
    }
}

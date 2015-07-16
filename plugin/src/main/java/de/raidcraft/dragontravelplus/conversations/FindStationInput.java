package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.answer.ConfiguredAnswer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.StationManager;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.api.station.UnknownStationException;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author mdoering
 */
public class FindStationInput extends ConfiguredAnswer {

    public FindStationInput(String type, ConfigurationSection config) {

        super(type, config);
    }

    @Override
    protected void load(ConfigurationSection args) {


    }

    @Override
    public boolean processInput(Conversation conversation, String input) {

        try {
            DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
            StationManager stationManager = plugin.getStationManager();
            Station station = stationManager.getStationFromInput(input);
            conversation.set(DTPConversationConstants.STATION_TARGET_NAME, station.getName());
            conversation.set(DTPConversationConstants.STATION_TARGET_FRIENDLY_NAME, station.getDisplayName());
            return true;
        } catch (UnknownStationException e) {
            conversation.sendMessage(ChatColor.RED + e.getMessage());
            return false;
        }
    }
}

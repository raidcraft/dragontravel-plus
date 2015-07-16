package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.conversations.conversations.PlayerConversation;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.rctravel.api.station.UnknownStationException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DragonTravelConversation extends PlayerConversation {

    private DragonStation station;
    private int stationSearchRadius;

    public DragonTravelConversation(Player player, ConversationTemplate conversationTemplate, ConversationHost conversationHost) {

        super(player, conversationTemplate, conversationHost);
        try {
            DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
            DragonStation station = (DragonStation) plugin.getStationManager().getNearbyStation(getHost().getLocation(), stationSearchRadius);

            if (station == null) {
                sendMessage(ChatColor.RED + "Es wurde keine passende Drachenstation in der Nähe gefunden!");
                RaidCraft.LOGGER.warning("Invalid dragontravel station NPC at " + getHost().getLocation());
                end(ConversationEndReason.ERROR);
                return;
            }

            if (!station.hasDiscovered(player.getUniqueId())) {
                station.setDiscovered(player.getUniqueId(), true);
                player.sendMessage(ChatColor.GREEN + "Du besuchst diese Drachenstation zum ersten mal!");
            }

            this.station = station;
            set("dtp_station_name", station.getName());
            set("dtp_station_friendlyname", station.getDisplayName());
        } catch (UnknownStationException e) {
            e.printStackTrace();
            end(ConversationEndReason.ERROR);
        }
    }
}

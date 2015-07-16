package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.conversations.conversations.ConfiguredConversationTemplate;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.rctravel.api.station.UnknownStationException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DragonTravelConversationTemplate extends ConfiguredConversationTemplate {

    private DragonStation station;
    private int stationSearchRadius;

    public DragonTravelConversationTemplate(String identifier, ConfigurationSection config) {

        super(identifier, config);
    }

    @Override
    protected void load(ConfigurationSection args) {

        this.stationSearchRadius = args.getInt("search-radius", 5);
    }

    @Override
    public Conversation startConversation(Player player, ConversationHost host) {

        Conversation conversation = super.startConversation(player, host);
        try {
            DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
            DragonStation station = (DragonStation) plugin.getStationManager().getNearbyStation(conversation.getHost().getLocation(), stationSearchRadius);

            if (station == null) {
                conversation.sendMessage(ChatColor.RED + "Es wurde keine passende Drachenstation in der Nähe gefunden!");
                RaidCraft.LOGGER.warning("Invalid dragontravel station NPC at " + conversation.getHost().getLocation());
                conversation.end(ConversationEndReason.ERROR);
                return conversation;
            }

            if (!station.hasDiscovered(player.getUniqueId())) {
                station.setDiscovered(player.getUniqueId(), true);
                player.sendMessage(ChatColor.GREEN + "Du besuchst diese Drachenstation zum ersten mal!");
            }

            this.station = station;
            conversation.set("dtp_station_name", station.getName());
            conversation.set("dtp_station_friendlyname", station.getDisplayName());
        } catch (UnknownStationException e) {
            e.printStackTrace();
            conversation.end(ConversationEndReason.ERROR);
        }
        return conversation;
    }
}

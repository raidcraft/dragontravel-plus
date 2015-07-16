package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.ReasonableRequirement;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.conversation.ConversationVariable;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.api.flight.flight.FlightException;
import de.raidcraft.dragontravelplus.StationManager;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.api.station.UnknownStationException;
import de.raidcraft.util.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Philip
 */
public class CheckStationTravelRequirement implements ReasonableRequirement<Player> {

    @Override
    @Information(
            value = "flight.check",
            desc = "Checks if the player can start the queued flight.",
            conf = {
                    "target: target station",
                    "price: 1g",
                    "check-price: true",
                    "check-discovered: true - whether or not to check if the target station was discovered",
                    "start: start station"
            },
            aliases = "DTP_CHECK_PLAYER"
    )
    public boolean test(Player player, ConfigurationSection config) {

        try {
            checkFlight(player, config);
            return true;
        } catch (UnknownStationException | FlightException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
            Conversations.endActiveConversation(player, ConversationEndReason.ERROR);
            return false;
        }
    }

    @Override
    public String getReason(Player player, ConfigurationSection config) {

        try {
            checkFlight(player, config);
            return "success";
        } catch (UnknownStationException | FlightException e) {
            return e.getMessage();
        }
    }

    private void checkFlight(Player player, ConfigurationSection config) throws FlightException, UnknownStationException {

        String startName = ConversationVariable.getString(player, "dtp_station_name").orElse(config.getString("start"));
        String targetName = ConversationVariable.getString(player, "dtp_target_name").orElse(config.getString("target"));
        boolean checkPrice = config.getBoolean("check-price");
        boolean checkDiscovered = config.getBoolean("check-discovered");

        if (targetName == null) {
            throw new FlightException("Ungültige Ziel Station angegeben!");
        }

        StationManager stationManager = RaidCraft.getComponent(StationManager.class);
        Station start = null;
        Station target = stationManager.getStationFromInput(targetName);
        if (startName != null) start = stationManager.getStation(startName);

        if (target == null) {
            throw new UnknownStationException("Die angegebene Station existiert nicht!");
        }

        if (start != null && start.equals(target)) {
            throw new FlightException("Du befindest dich bereits an dieser Station!");
        }

        Location startLocation = start != null ? start.getLocation() : player.getLocation();

        ConversationVariable.set(player, "dtp_target_name", target.getName());
        ConversationVariable.set(player, "dtp_target_friendlyname", target.getDisplayName());
        ConversationVariable.set(player, "dtp_target_distance", LocationUtil.getDistance(target.getLocation(), startLocation));

        Economy economy = RaidCraft.getEconomy();
        double price = 0.0;
        if (start instanceof DragonStation && target instanceof DragonStation) {
            price = ((DragonStation) start).getPrice((DragonStation) target);
        }

        ConversationVariable.set(player, "dtp_target_price", price);
        ConversationVariable.set(player, "dtp_target_price_formatted", economy.getFormattedAmount(price));

        if (checkPrice && !economy.hasEnough(player.getUniqueId(), price)) {
            throw new FlightException("Du benötigst mindestens " + economy.getFormattedAmount(price) + " um dorthin zu fliegen!");
        }

        if (checkDiscovered && (target instanceof DragonStation && !((DragonStation) target).hasDiscovered(player.getUniqueId()))) {
            throw new FlightException("Du musst den Drachenmeister dieser Station erst noch kennen lernen!");
        }
    }
}

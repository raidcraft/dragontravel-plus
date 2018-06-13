package de.raidcraft.dragontravelplus.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.config.builder.ConfigGenerator;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.rctravel.api.station.SimpleStation;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.tasks.TakeoffDelayedTask;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class TravelToStationAction implements Action<Player> {

    @Override
    @ConfigGenerator.Information(
            value = "station.travel",
            desc = "Travels to the given station. Uses the current player position or given station as start.",
            aliases = {"travel"},
            conf = {
                    "start: [station name]",
                    "target: <station name>",
                    "delay: [in sec]",
                    "confirm: true/->false asks the player to confirm the travelTo",
                    "pay: true/->false if the player must pay for the trip"
            }
    )
    public void accept(Player player, ConfigurationSection config) {

        String startName = config.getString("start", null);
        String targetName = config.getString("target", null);
        int delay = config.getInt("delay", 0);

        DragonTravelPlusPlugin plugin = RaidCraft.getComponent(DragonTravelPlusPlugin.class);
        Optional<Station> targetStation = plugin.getStationManager().getStation(targetName);

        if (!targetStation.isPresent()) {
            player.sendMessage(ChatColor.RED + "Invalid target station in action " + getIdentifier() + " and config: " + ConfigUtil.getFileName(config));
            return;
        }

        Optional<Station> startStation;
        if (startName == null) {
            startStation = Optional.of(new SimpleStation(getIdentifier(), player.getLocation().clone()));
        } else {
            startStation = plugin.getStationManager().getStation(startName);
        }
        if (!startStation.isPresent()) {
            player.sendMessage(ChatColor.RED + "Invalid start station in action " + getIdentifier() + " and config: " + ConfigUtil.getFileName(config));
            return;
        }

        double price = targetStation.get().getPrice(startStation.get().getLocation());

        if (config.getBoolean("confirm", false)) {
            Conversations.getOrStartConversation(player).changeToStage(
                    Conversations.buildStage("confirm-travelTo")
                            .withText("Möchtest du nach " + targetStation.get().getDisplayName() + " reisen?",
                                    config.getBoolean("pay", false) && price > 0
                                            ? "Das kostet dich " + RaidCraft.getEconomy().getFormattedAmount(price) + "."
                                            : "Diese Reise ist für dich kostenlos."
                            )
                            .withAnswer("Ja auf gehts!", answer -> answer
                                    .withRequirement((type, config1) -> RaidCraft.getEconomy().hasEnough(player.getUniqueId(), price))
                                    .withAction((type, config1) -> startStation.get().travelTo(player, targetStation.get()))
                                    .withAction((type, config1) -> RaidCraft.getEconomy().substract(player.getUniqueId(), price))
                                    .withAction(Action.endConversation(ConversationEndReason.ENDED))
                            )
                            .withAnswer("Nein danke, ich habe es mir anders überlegt.", answer -> answer.withAction(Action.changeToPreviousStage())).build()
            );
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, new TakeoffDelayedTask(startStation.get(), targetStation.get(), player), delay);
        }
    }
}

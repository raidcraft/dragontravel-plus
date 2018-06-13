package de.raidcraft.dragontravelplus.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.dragontravelplus.StationManager;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.npc.StationTrait;
import de.raidcraft.rctravel.util.StationConversationUtil;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;
import java.util.stream.Collectors;

public class ListStationsAction implements Action<Conversation> {

    @Override
    @Information(
            value = "stations.list",
            desc = "Lists all DragonTravelPlus stations in the current conversation.",
            type = Conversation.class,
            conf = {
                    "station: the station that should list its targets",
                    "useNearestStation: [true/->false] - if true station name can be blank",
                    "searchRadius: radius to search for nearest station",
                    "allowTravelToUndiscovered: [true/->false] - if true the player can only choose discovered stations",
                    "order: ->ALPHABETIC_ASC | ALPHABETIC_DESC | DISTANCE_ASC | DISTANCE_DESC | PRICE_ASC | PRICE_DESC",
                    "filter: FREE | ->DISCOVERED | ALL"
            }
    )
    public void accept(Conversation conversation, ConfigurationSection config) {

        StationManager stationManager = RaidCraft.getComponent(StationManager.class);

        Optional<Station> station = stationManager.getStation(config.getString("station", conversation.getHost()
                .getTrait(StationTrait.class).map(trait -> ((StationTrait) trait).getStationName())
                .orElse("").toString()));

        if (!station.isPresent() && config.getBoolean("useNearestStation", false)) {
            station = stationManager.getNearbyStation(conversation.getLocation(), config.getInt("searchRadius", 10));
        }

        if (!station.isPresent()) {
            Conversations.error(conversation, "Invalid station in action " + getIdentifier() + " and config: " + ConfigUtil.getFileName(config));
            return;
        }

        Station startStation = station.get();
        conversation.changeToStage(StationConversationUtil.buildStationList(conversation,
                stationManager.getAllStations().stream().filter(s -> !s.equals(startStation)).collect(Collectors.toList()),
                TravelToStationAction.class, (target, actionBuilder) -> {
            actionBuilder.withConfig("target", target.getName());
            actionBuilder.withConfig("start", startStation.getName());
            actionBuilder.withConfig("confirm", true);
            actionBuilder.withConfig("pay", true);
        }));
    }
}

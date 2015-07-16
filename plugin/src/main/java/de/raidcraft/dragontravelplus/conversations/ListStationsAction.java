package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.dragontravelplus.StationManager;
import de.raidcraft.dragontravelplus.comparators.AlphabeticComparator;
import de.raidcraft.dragontravelplus.comparators.DistanceComparator;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.api.station.UnknownStationException;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListStationsAction implements Action<Conversation> {

    @Override
    @Information(
            value = "stations.list",
            desc = "Constructs a conversation that lists all the possible DTP stations " +
                    "to the player makes it possible to pick one as a flight target.",
            aliases = {"DTP_LIST_STATIONS"}
    )
    public void accept(Conversation conversation, ConfigurationSection config) {

        try {
            ListType type = ListType.valueOf(config.getString("type", "ALPHABETIC"));
            if (type == null) {
                RaidCraft.LOGGER.warning("invalid dtp list type inside " + ConfigUtil.getFileName(config));
                conversation.end(ConversationEndReason.ERROR);
                return;
            }

            StationManager stationManager = RaidCraft.getComponent(StationManager.class);
            DragonStation currentStation;
            // if we are in a dedicated dtp conversation get the station directly
            if (conversation instanceof DragonTravelConversation) {
                currentStation = ((DragonTravelConversation) conversation).getStation();
            } else {
                currentStation = (DragonStation) stationManager.getStation(conversation.getString("dtp_station_name", config.getString("station")));
            }

            if (currentStation == null) {
                RaidCraft.LOGGER.warning("Could not find current dragon station at " + conversation.getOwner().getLocation() + " in " + ConfigUtil.getFileName(config));
                conversation.sendMessage(ChatColor.RED + "Tut mir leid ich konnte in deiner Nähe keine Drachenstation finden.");
                conversation.end(ConversationEndReason.ERROR);
                return;
            }

            String confirmStageName = config.getString("confirmstage");

            if (confirmStageName == null) {
                RaidCraft.LOGGER.warning("Missing argument in '" + ConfigUtil.getFileName(config) + "': Confirmstage or Returnstage is missing!");
                conversation.end(ConversationEndReason.ERROR);
                return;
            }

            Optional<Stage> confirmStage = conversation.getStage(confirmStageName);
            if (!confirmStage.isPresent()) {
                RaidCraft.LOGGER.warning("Confirm stage " + confirmStageName + " not found!");
                conversation.end(ConversationEndReason.ERROR);
                return;
            }

            List<Station> stations = stationManager.getUnlockedStations(conversation.getOwner());

            if (type == ListType.ALPHABETIC) {
                Collections.sort(stations, new AlphabeticComparator());
            }
            if (type == ListType.DISTANCE) {
                Collections.sort(stations, new DistanceComparator(currentStation));
            }

            if (type == ListType.FREE) {
                stations = stations.stream()
                        .filter(station -> station instanceof DragonStation)
                        .filter(station -> currentStation.getPrice((DragonStation) station) == 0)
                        .collect(Collectors.toList());
            }

            stations = stations.stream()
                    .filter(station -> station.getLocation().getWorld() != null)
                    .filter(station -> station.getLocation().getWorld().equals(currentStation.getLocation().getWorld()))
                    .distinct()
                    .collect(Collectors.toList());
            stations.remove(currentStation);

            if (stations.isEmpty()) {
                Stage.of(conversation, "Du kennst keine passende Stationen!",
                        Answer.of("Ok zurück",
                                Action.changeStage(conversation.getCurrentStage().get())))
                        .changeTo();
                return;
            }

            Stage listStationsStage = Stage.of(conversation, "Du kennst folgende Drachenstationen:|" + ChatColor.GRAY + type.getInfoText());

            stations.stream().filter(station -> station instanceof DragonStation)
                    .forEach(station -> listStationsStage
                            .addAnswer(createStationAnswer(conversation, currentStation, (DragonStation) station, confirmStage.get())));

            conversation.set("dtp_station_name", currentStation.getName());
            conversation.changeToStage(listStationsStage);
        } catch (UnknownStationException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
            conversation.end(ConversationEndReason.ERROR);
        }
    }

    private Answer createStationAnswer(Conversation conversation, DragonStation start, DragonStation target, Stage confirmStage) {

        StringBuilder builder = new StringBuilder();
        double price = start.getPrice(target);
        if (price > 0 && !RaidCraft.getEconomy().hasEnough(conversation.getOwner().getUniqueId(), price)) {
            builder.append(ChatColor.DARK_GRAY);
        }

        builder.append(target.getDisplayName());
        builder.append(" ").append(RaidCraft.getEconomy().getFormattedAmount(price));

        int distance = (int) start.getLocation().distance(target.getLocation());
        if (distance < 1000) {
            builder.append(ChatColor.GRAY).append(" (").append(distance).append("m)");
        } else {
            builder.append(ChatColor.GRAY).append(" (").append(((double) distance) / 1000.).append("km)");
        }

        Answer answer = Answer.of(builder.toString(),
                Action.setConversationVariable("dtp_start_name", start.getName()),
                Action.setConversationVariable("dtp_target_name", target.getName()),
                Action.setConversationVariable("dtp_target_price", price)
        );
        if (confirmStage == null) {
            confirmStage = Stage.of(conversation, "Der Flug nach " + target.getDisplayName() + " kostet dich " + RaidCraft.getEconomy().getFormattedAmount(price)
                            + "|Willst du den Flug starten?",
                    Answer.of("Ja, los geht`s!",
                            Action.text("Ein wenig Geduld, mein Drache ist gleich da!"),
                            Action.of(FlyToStationAction.class)
                                    .withArgs("start", start.getName())
                                    .withArgs("target", target.getName())
                                    .withArgs("price", price)
                                    .withArgs("delay", "1s")
                    ),
                    Answer.of("Nein das ist mir zu teuer!",
                            Action.text("Dann musst du wohl zu Fuß gehen."),
                            Action.endConversation(ConversationEndReason.ENDED)
                    )
            );
        }
        answer.addAction(Action.changeStage(confirmStage));
        return answer;
    }

    public enum ListType {

        ALPHABETIC("Alphabetisch sortiert"),
        DISTANCE("Nach Entfernung sortiert"),
        FREE("Es werden nur kostenlose angezeigt");

        private String infoText;

        private ListType(String infoText) {

            this.infoText = infoText;
        }

        public String getInfoText() {

            return infoText;
        }
    }
}
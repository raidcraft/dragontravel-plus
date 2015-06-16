package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.StationManager;
import de.raidcraft.dragontravelplus.comparators.AlphabeticComparator;
import de.raidcraft.dragontravelplus.comparators.DistanceComparator;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.rcconversations.actions.common.StageAction;
import de.raidcraft.rcconversations.actions.variables.SetVariableAction;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.action.MissingArgumentException;
import de.raidcraft.rcconversations.api.action.WrongArgumentValueException;
import de.raidcraft.rcconversations.api.answer.Answer;
import de.raidcraft.rcconversations.api.answer.SimpleAnswer;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.api.stage.SimpleStage;
import de.raidcraft.rcconversations.api.stage.Stage;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.api.station.UnknownStationException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Philip
 */
@ActionInformation(name = "DTP_LIST_STATIONS")
public class ListStationsAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        try {
            String typeName = args.getString("type");
            ListType type = ListType.valueOf(typeName);
            if (type == null) {
                throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Type '" + typeName + "' does not exists!");
            }

            StationManager stationManager = RaidCraft.getComponent(StationManager.class);
            DragonStation currentStation = (DragonStation) stationManager.getStation(conversation.getString("dtp_station_name"));

            String confirmStage = args.getString("confirmstage");
            String returnStage = args.getString("returnstage");
            int pageSize = args.getInt("pagesize", 4);

            if (confirmStage == null || returnStage == null) {
                throw new MissingArgumentException("Missing argument in action '" + getName() + "': Confirmstage or Returnstage is missing!");
            }

            String entranceStage = "dtp_stationslist";

            List<Station> stations = stationManager.getUnlockedStations(conversation.getPlayer());

            if (type == ListType.ALPHABETIC) {
                Collections.sort(stations, new AlphabeticComparator());
            }
            if (type == ListType.DISTANCE) {
                Collections.sort(stations, new DistanceComparator(currentStation));
            }

            if (type == ListType.FREE) {
                if(currentStation.isFree()) {
                    stations = stations.stream()
                            .filter(station -> station instanceof DragonStation)
                            .map(station -> (DragonStation) station)
                            .filter(DragonStation::isFree)
                            .collect(Collectors.toList());
                } else {
                    stations = stations.stream()
                            .filter(station -> station instanceof DragonStation)
                            .map(station -> (DragonStation) station)
                            .filter(DragonStation::isMainStation)
                            .filter(DragonStation::isFree)
                            .collect(Collectors.toList());
                }
            }

            stations = stations.stream()
                    .filter(station -> station.getLocation().getWorld() != null)
                    .filter(station -> station.getLocation().getWorld().equals(currentStation.getLocation().getWorld()))
                    .distinct()
                    .collect(Collectors.toList());
            stations.remove(currentStation);

            if (stations.isEmpty()) {
                List<Answer> answers = new ArrayList<>();
                answers.add(new SimpleAnswer("1", "Ok zurück", new ActionArgumentList("A", StageAction.class, "stage", returnStage)));
                conversation.addStage(new SimpleStage(entranceStage, "Du kennst keine passende Stationen!", answers));
            }

            int pages = (int) Math.ceil((double) stations.size() / (double) pageSize);
            if (pages == 0) pages = 1;
            int x = 0;
            for (int i = 0; i < pages; i++) {

                Stage stage;
                List<Answer> answers = new ArrayList<>();
                String text;

                text = "Du kennst folgende Drachenstationen (" + (i + 1) + "/" + pages + "):|&7(" + type.getInfoText() + ")";

                int a;
                for (a = 0; a < pageSize; a++) {
                    if (x < stations.size()) {
                        answers.add(createStationAnswer(conversation.getPlayer(), a, currentStation, (DragonStation) stations.get(x), confirmStage));
                        x++;
                    } else {
                        break;
                    }
                }
                a++;

                String nextStage;
                if (pages - 1 == i) {
                    nextStage = entranceStage;
                } else {
                    nextStage = entranceStage + "_" + (i + 1);
                }
                String thisStage;
                if (i == 0) {
                    thisStage = entranceStage;
                } else {
                    thisStage = entranceStage + "_" + i;
                }

                if (pages > 1) {
                    answers.add(new SimpleAnswer(String.valueOf(a), "&7Nächste Seite", new ActionArgumentList(String.valueOf(a), StageAction.class, "stage", nextStage)));
                }
                stage = new SimpleStage(thisStage, text, answers);

                conversation.addStage(stage);
            }

            conversation.setCurrentStage(entranceStage);
            conversation.triggerCurrentStage();
        } catch (UnknownStationException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    private Answer createStationAnswer(Player player, int number, DragonStation start, DragonStation target, String confirmStage) {

        List<ActionArgumentList> actions = new ArrayList<>();
        int i = 0;
        Map<String, Object> data = new HashMap<>();
        data.put("variable", "dtp_target_name");
        data.put("local", true);
        data.put("value", target.getName());
        actions.add(new ActionArgumentList(String.valueOf(i++), SetVariableAction.class, data));
        actions.add(new ActionArgumentList(String.valueOf(i++), StageAction.class, "stage", confirmStage));

        StringBuilder builder = new StringBuilder();
        double price = start.getPrice(target);
        if (price > 0 && !RaidCraft.getEconomy().hasEnough(player.getUniqueId(), price)) {
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

        return new SimpleAnswer(String.valueOf(number + 1), builder.toString(), actions);
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
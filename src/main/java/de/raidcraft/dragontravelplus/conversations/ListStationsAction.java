package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.comparator.AlphabeticComparator;
import de.raidcraft.dragontravelplus.comparator.DistanceComparator;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import de.raidcraft.dragontravelplus.util.FlightCosts;
import de.raidcraft.rcconversations.actions.common.StageAction;
import de.raidcraft.rcconversations.actions.variables.SetVariableAction;
import de.raidcraft.rcconversations.api.action.*;
import de.raidcraft.rcconversations.api.answer.Answer;
import de.raidcraft.rcconversations.api.answer.SimpleAnswer;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.api.stage.SimpleStage;
import de.raidcraft.rcconversations.api.stage.Stage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Philip
 */
@ActionInformation(name = "DTP_LIST_STATIONS")
public class ListStationsAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String typeName = args.getString("type");
        ListType type = ListType.valueOf(typeName);
        if(type == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Type '" + typeName + "' does not exists!");
        }

        DragonStation currentStation = StationManager.INST.getDragonStation(conversation.getString("dtp_station_name"));

        String confirmStage = args.getString("confirmstage");
        String returnStage = args.getString("returnstage");
        int pageSize = args.getInt("pagesize", 4);

        if(confirmStage == null || returnStage == null) {
            throw new MissingArgumentException("Missing argument in action '" + getName() + "': Confirmstage or Returnstage is missing!");
        }

        String entranceStage = "dtp_stationslist";

        List<DragonStation> stations = StationManager.INST.getPlayerStations(conversation.getPlayer());

        if(type == ListType.ALPHABETIC) {
            Collections.sort(stations, new AlphabeticComparator());
        }
        if(type == ListType.DISTANCE) {
            Collections.sort(stations, new DistanceComparator(currentStation));
        }

        if(type == ListType.FREE) {
            List<DragonStation> freeStations = new ArrayList<>();
            for(DragonStation s : stations) {
                if(s.getPrice() == 0) {
                    freeStations.add(s);
                }
            }
            stations = freeStations;
        }

        for(DragonStation station : stations) {
            if(station.equals(currentStation)) {
                stations.remove(station);
                break;
            }
        }

        if(stations.size() == 0) {
            List<Answer> answers = new ArrayList<>();
            answers.add(new SimpleAnswer("1", "Ok zurück", new ActionArgumentList("A", StageAction.class, "stage", returnStage)));
            conversation.addStage(new SimpleStage(entranceStage, "Du kennst keine passende Stationen!", answers));
        }

        int pages = (int) (((double) stations.size() / (double) pageSize) + 0.5);
        if(pages == 0) pages = 1;
        for (int i = 0; i < pages; i++) {

            Stage stage;
            List<Answer> answers = new ArrayList<>();
            String text;

            text = "Du kennst folgende Drachenstationen (" + (i+1) + "/" + pages + "):|&7(" + type.getInfoText() + ")";
            int a;

            for (a = 0; a < pageSize; a++) {
                if (stations.size() <= a + (i * pageSize)) break;
                answers.add(createStationAnswer(conversation.getPlayer(), a, currentStation, stations.get(i*pageSize + a), confirmStage));
            }
            a++;

            String nextStage;
            if (pages - 1 == i) {
                nextStage = entranceStage;
            }
            else {
                nextStage = entranceStage + "_" + (i + 1);
            }
            String thisStage;
            if(i == 0) {
                thisStage = entranceStage;
            }
            else {
                thisStage = entranceStage + "_" + i;
            }

            if(pages > 1) {
                answers.add(new SimpleAnswer(String.valueOf(a), "&7Nächste Seite", new ActionArgumentList(String.valueOf(a), StageAction.class, "stage", nextStage)));
            }
            stage = new SimpleStage(thisStage, text, answers);

            conversation.addStage(stage);
        }

        conversation.setCurrentStage(entranceStage);
        conversation.triggerCurrentStage();
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
        double price = FlightCosts.getPrice(start, target);
        if(!RaidCraft.getEconomy().hasEnough(player.getName(), price)) {
            builder.append(ChatColor.DARK_GRAY);
        }

        builder.append(target.getFriendlyName());
        builder.append(" ").append(RaidCraft.getEconomy().getFormattedAmount(price));

        int distance = (int)start.getLocation().distance(target.getLocation());
        if(distance < 1000) {
            builder.append(ChatColor.GRAY).append(" (").append(distance).append("m)");
        }
        else {
            builder.append(ChatColor.GRAY).append(" (").append(((double)distance)/1000.).append("km)");
        }

        return new SimpleAnswer(String.valueOf(number + 1), builder.toString(), actions);
    }

    public enum ListType {

        ALPHABETIC("Alphabetisch sortiert"),
        DISTANCE("Nach Entfernung sortiert"),
        FREE("Es werden nu kostenlose angezeigt");

        private String infoText;

        private ListType(String infoText) {

            this.infoText = infoText;
        }

        public String getInfoText() {

            return infoText;
        }
    }
}

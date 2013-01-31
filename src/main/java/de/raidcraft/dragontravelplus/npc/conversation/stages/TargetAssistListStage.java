package de.raidcraft.dragontravelplus.npc.conversation.stages;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.util.FlightCosts;
import de.raidcraft.dragontravelplus.util.FlightDistance;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Philip
 * Date: 02.12.12 - 16:51
 * Description:
 */
public class TargetAssistListStage extends Stage {

    private List<DragonStation> stations = new ArrayList<>();
    private Map<Integer, DragonStation> answerAssignment = new HashMap<>();
    private Map<Integer, SpecialAnswers> specialAnswers = new HashMap<>();
    private int page;

    public TargetAssistListStage(Conversation conversation, List<DragonStation> stations, int page) {

        super(conversation);
        this.stations = stations;
        this.page = page;

        init();
    }

    private void init() {

        specialAnswers.clear();
        answerAssignment.clear();
        int maxPerPage = RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.maxStationPerPage;
        if (page > Math.ceil((double) stations.size() / (double) maxPerPage) - 1) {
            page = 0;
        }
        int endIndex = page * maxPerPage + maxPerPage;
        if (endIndex > stations.size()) endIndex = stations.size();
        int i = 0;
        List<String> reply = new ArrayList<>();
        for (DragonStation station : stations.subList(page * maxPerPage, endIndex)) {
            i++;
            answerAssignment.put(i, station);
            double price = FlightCosts.getPrice(getConversation().getDragonGuard().getDragonStation(), station);
            String strike = "";
            if (price > RaidCraft.getEconomy().getBalance(getConversation().getPlayer().getName())) {
                strike += ChatColor.STRIKETHROUGH;
            }
            reply.add(strike + station.getName() + ChatColor.RESET + ChatColor.RED + " $"
                    + price + " " + ChatColor.DARK_GREEN + FlightDistance.getPrintDistance(getConversation().getDragonGuard().getDragonStation(), station));
        }
        if (stations.size() > maxPerPage) {
            i++;
            specialAnswers.put(i, SpecialAnswers.MORE);
            reply.add(ChatColor.GOLD + RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convTargetAssistListTellMore);
        }
        i++;
        specialAnswers.put(i, SpecialAnswers.BACK);
        reply.add(ChatColor.GOLD + RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convTargetAssistListGoBack);

        setTextToSpeak(RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convTargetAssistListSpeak);
        setPlayerReply(reply.toArray(new String[reply.size()]));
    }

    @Override
    public void speak() {

        super.speak();
        showAnswers();
    }

    @Override
    public boolean processAnswer(String answer) {

        try {
            int choice = Integer.parseInt(answer);
            if (specialAnswers.containsKey(choice)) {
                if (specialAnswers.get(choice) == SpecialAnswers.MORE) {
                    page++;
                    init();
                    speak();
                } else if (specialAnswers.get(choice) == SpecialAnswers.BACK) {
                    getConversation().setCurrentStage(new TargetAssistRegionStage(getConversation()));
                    getConversation().getCurrentStage().speak();
                }
                return true;
            } else {
                DragonStation station = answerAssignment.get(choice);
                if (station != null) {
                    getConversation().setCurrentStage(new FlightConfirmStage(getConversation()
                            , this
                            , station));
                    getConversation().getCurrentStage().speak();
                    return true;
                }
            }
        } catch (Exception e) {
        }
        wrongAnswerWarning();
        return true;
    }

    public enum SpecialAnswers {
        MORE,
        BACK
    }
}

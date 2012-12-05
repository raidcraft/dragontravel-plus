package de.raidcraft.dragontravelplus.npc.conversation.stages;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.dragonmanger.DragonManager;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.station.DragonStation;
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
        int maxPerPage = DragonTravelPlusModule.inst.config.maxStationPerPage;
        if(page > Math.ceil((double)stations.size()/(double)maxPerPage)) {
            page = 0;
        }
        int endIndex = page*maxPerPage + maxPerPage;
        if(endIndex > stations.size()) endIndex = stations.size();
        int i = 0;
        List<String> reply = new ArrayList<>();
        for(DragonStation station : stations.subList(page*maxPerPage,endIndex)) {
            i++;
            answerAssignment.put(i, station);
            reply.add(station.getName());
        }
        if(stations.size() > maxPerPage) {
            i++;
            specialAnswers.put(i, SpecialAnswers.MORE);
            reply.add(ChatColor.GOLD + DragonTravelPlusModule.inst.config.convTargetAssistListTellMore);
        }
        i++;
        specialAnswers.put(i, SpecialAnswers.BACK);
        reply.add(ChatColor.GOLD + DragonTravelPlusModule.inst.config.convTargetAssistListGoBack);

        setTextToSpeak(DragonTravelPlusModule.inst.config.convTargetAssistListSpeak);
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

            if(specialAnswers.containsKey(choice)) {
                if(specialAnswers.get(choice) == SpecialAnswers.MORE) {
                    page++;
                    init();
                    speak();
                }
                else if(specialAnswers.get(choice) == SpecialAnswers.BACK) {
                    getConversation().setCurrentStage(new TargetAssistRegionStage(getConversation()));
                    getConversation().getCurrentStage().speak();
                }
                return true;
            }
            else {
                DragonStation station = answerAssignment.get(choice);
                if(station != null) {
                    List<String> replacedMsg = new ArrayList<>();
                    for(String line : DragonTravelPlusModule.inst.config.convTargetAssistListTakeoff) {
                        replacedMsg.add(line.replace("%ds", station.getName()));
                    }

                    speak(replacedMsg.toArray(new String[replacedMsg.size()]));

                    DragonManager.INST.takeoff(getConversation().getPlayer(), station, 5);

                    getConversation().setCurrentStage(null);
                    return true;
                }
            }
        }
        catch (Exception e) {}
        wrongAnswerWarning();
        return true;
    }

    public enum SpecialAnswers {
        MORE,
        BACK
    }
}

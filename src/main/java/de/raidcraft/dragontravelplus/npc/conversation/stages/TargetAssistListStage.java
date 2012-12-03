package de.raidcraft.dragontravelplus.npc.conversation.stages;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.station.DragonStation;

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
    private int page;

    public TargetAssistListStage(Conversation conversation, List<DragonStation> stations, int page) {

        super(conversation);
        this.stations = stations;
        this.page = page;

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
        
        setTextToSpeak(DragonTravelPlusModule.inst.config.convTargetAssistRegionSpeak);
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
        }
        catch (Exception e) {}
        wrongAnswerWarning();
        return true;
    }
}

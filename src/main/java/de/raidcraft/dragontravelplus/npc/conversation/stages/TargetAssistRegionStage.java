package de.raidcraft.dragontravelplus.npc.conversation.stages;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Philip
 * Date: 02.12.12 - 16:51
 * Description:
 */
public class TargetAssistRegionStage extends Stage {
    private Map<DragonStation.MapLocation, List<DragonStation>> playerStations = new HashMap<>();
    private Map<Integer, DragonStation.MapLocation> answerAssignment = new HashMap<>();

    public TargetAssistRegionStage(Conversation conversation) {

        super(conversation);
        
        setTextToSpeak(DragonTravelPlusModule.inst.config.convTargetAssistRegionSpeak);
        
        List<DragonStation> unsortedPlayerStations = StationManager.INST.getPlayerStations(getConversation().getPlayer().getName());
        for(DragonStation station : unsortedPlayerStations) {
            List<DragonStation> stations;
            if(playerStations.containsKey(station.getMapLocation())) {
                stations = playerStations.get(station.getMapLocation());
            }
            else {
                stations = new ArrayList<>();
            }
            stations.add(station);

            playerStations.put(station.getMapLocation(), stations);
        }
        
//        if(playerStations.keySet().size() == 1) {
//            getConversation().setCurrentStage(new TargetAssistListStage(getConversation()
//                    , unsortedPlayerStations
//                    , 0
//            ));
//            getConversation().getCurrentStage().speak();
//            return;
//        }

        int i = 0;
        List<String> reply = new ArrayList<>();
        for(Map.Entry<DragonStation.MapLocation, List<DragonStation>> entry : playerStations.entrySet()) {
            i++;
            answerAssignment.put(i, entry.getKey());
            reply.add(entry.getKey().getName());
        }
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
            if(answerAssignment.containsKey(choice)) {
                getConversation().setCurrentStage(new TargetAssistListStage(getConversation()
                        , playerStations.get(answerAssignment.get(choice))
                        , 0
                ));
                getConversation().getCurrentStage().speak();
                return true;
            }
        }
        catch (Exception e) {}
        wrongAnswerWarning();
        return true;
    }
}

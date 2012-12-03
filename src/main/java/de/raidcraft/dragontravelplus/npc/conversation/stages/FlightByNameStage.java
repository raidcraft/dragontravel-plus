package de.raidcraft.dragontravelplus.npc.conversation.stages;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.dragonmanger.DragonManager;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 02.12.12 - 16:51
 * Description:
 */
public class FlightByNameStage extends Stage {

    public FlightByNameStage(Conversation conversation) {

        super(conversation);
        
        setTextToSpeak(DragonTravelPlusModule.inst.config.convFlightByNameSpeak);
    }

    @Override
    public void speak() {

        super.speak();
    }

    @Override
    public boolean processAnswer(String answer) {

        DragonStation station = StationManager.INST.getPlayerStation(getConversation().getPlayer().getName(), answer);

        if(station == null) {
            wrongAnswerWarning(DragonTravelPlusModule.inst.config.convFlightByNameUnknownStation);
            return true;
        }

        if(station.getName() == getConversation().getDragonGuard().getDragonStation().getName()) {
            wrongAnswerWarning(DragonTravelPlusModule.inst.config.convFlightByNameSameStation);
            return true;
        }

        List<String> replacedMsg = new ArrayList<>();
        for(String line : DragonTravelPlusModule.inst.config.convFlightByNameTakeoff) {
            replacedMsg.add(line.replace("%ds", station.getName()));
        }
        
        speak(replacedMsg.toArray(new String[replacedMsg.size()]));

        DragonManager.INST.takeoff(getConversation().getPlayer(), station, 5);

        getConversation().setCurrentStage(null);
        return true;
    }
}

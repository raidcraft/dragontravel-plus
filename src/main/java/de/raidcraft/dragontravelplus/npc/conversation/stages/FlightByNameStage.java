package de.raidcraft.dragontravelplus.npc.conversation.stages;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;

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

        getConversation().setCurrentStage(new ProcessEconomyStage(getConversation()
                , this
                , station
                , true));
        getConversation().getCurrentStage().speak();
        return true;
    }
}

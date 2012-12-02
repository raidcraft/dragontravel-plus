package de.raidcraft.dragontravelplus.npc.conversation.stages;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.station.StationManager;

/**
 * Author: Philip
 * Date: 02.12.12 - 16:51
 * Description:
 */
public class FirstMeetStage extends Stage {

    public FirstMeetStage(Conversation conversation) {

        super(conversation);
        
        setTextToSpeak(DragonTravelPlusModule.inst.config.convFirstMeetSpeak);
    }

    @Override
    public void speak() {

        super.speak();

        StationManager.INST.assignStationWithPlayer(getConversation().getPlayer().getName()
                , getConversation().getDragonGuard().getDragonStation());
        getConversation().setCurrentStage(null);
    }
}

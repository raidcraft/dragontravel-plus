package de.raidcraft.dragontravelplus.npc.conversation.stages;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
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

        setTextToSpeak(RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convFirstMeetSpeak);
    }

    @Override
    public void speak() {

        super.speak();

        StationManager.INST.assignStationWithPlayer(getConversation().getPlayer().getName()
                , getConversation().getDragonGuard().getDragonStation());
        getConversation().updatePlayerStations();
        getConversation().setCurrentStage(null);
    }
}

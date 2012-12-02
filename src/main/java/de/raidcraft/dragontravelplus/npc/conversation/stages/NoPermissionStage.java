package de.raidcraft.dragontravelplus.npc.conversation.stages;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;

/**
 * Author: Philip
 * Date: 02.12.12 - 16:51
 * Description:
 */
public class NoPermissionStage extends Stage {

    public NoPermissionStage(Conversation conversation) {

        super(conversation);
        
        setTextToSpeak(DragonTravelPlusModule.inst.config.convNoPermissionSpeak);
    }
}

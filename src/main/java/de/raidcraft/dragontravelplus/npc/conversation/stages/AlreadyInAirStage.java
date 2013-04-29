package de.raidcraft.dragontravelplus.npc.conversation.stages;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;

/**
 * Author: Philip
 * Date: 02.12.12 - 16:51
 * Description:
 */
public class AlreadyInAirStage extends Stage {

    public AlreadyInAirStage(Conversation conversation) {

        super(conversation);

        setTextToSpeak(RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convAlreadyInAir);
    }
}

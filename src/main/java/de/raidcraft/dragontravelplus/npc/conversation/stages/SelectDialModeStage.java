package de.raidcraft.dragontravelplus.npc.conversation.stages;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;

/**
 * Author: Philip
 * Date: 02.12.12 - 16:51
 * Description:
 */
public class SelectDialModeStage extends Stage {

    public SelectDialModeStage(Conversation conversation) {

        super(conversation);

        setTextToSpeak(RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convSelectDialModeSpeak);
        setPlayerReply(RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convSelectDialModeAnswers);
    }

    @Override
    public void speak() {

        super.speak();
        showAnswers();
    }

    @Override
    public boolean processAnswer(String answer) {

        try {
            switch (Integer.parseInt(answer)) {
                case 1:
                    getConversation().setCurrentStage(new TargetAssistRegionStage(getConversation()));
                    getConversation().getCurrentStage().speak();
                    return true;
                case 2:
                    getConversation().setCurrentStage(new FlightByNameStage(getConversation()));
                    getConversation().getCurrentStage().speak();
                    return true;
                default:
                    wrongAnswerWarning();
            }
        } catch (Exception e) {
            wrongAnswerWarning();
        }
        return true;
    }
}

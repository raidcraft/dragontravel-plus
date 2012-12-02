package de.raidcraft.dragontravelplus.npc.conversation.stages;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;

/**
 * Author: Philip
 * Date: 02.12.12 - 16:51
 * Description:
 */
public class TargetAssistRegionStage extends Stage {

    public TargetAssistRegionStage(Conversation conversation) {

        super(conversation);
        
        setTextToSpeak(DragonTravelPlusModule.inst.config.convSelectDialModeSpeak);
        setPlayerReply(DragonTravelPlusModule.inst.config.convSelectDialModeAnswers);
    }

    @Override
    public void speak() {

        super.speak();
        showAnswers();
    }

    @Override
    public boolean processAnswer(String answer) {
//        switch(answer) {
//            case 0:
//                getConversation().setCurrentStage();
//                break;
//            case 1:
//                getConversation().setCurrentStage();
//                break;
//            default:
//                wrongAnswerWarning();
//        }
        return false;
    }
}

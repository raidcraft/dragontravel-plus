package de.raidcraft.dragontravelplus.npc.conversation.stages;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import org.bukkit.ChatColor;

/**
 * Author: Philip
 * Date: 01.12.12 - 16:57
 * Description:
 */
public abstract class Stage {
    private Conversation conversation;
    private String[] textToSpeak = new String[]{};
    private String[] playerReply = new String[]{};
    private String[] wrongAnswerReply = DragonTravelPlusModule.inst.config.convWrongAnswerWarning;

    public Stage(Conversation conversation) {
        this.conversation = conversation;
    }

    public void speak() {
        speak(textToSpeak);
    }
    
    public void showAnswers() {
        conversation.getPlayer().sendMessage("-----");
        int i = 0;
        for(String line : playerReply) {
            i++;
            conversation.getPlayer().sendMessage(i + " : " + Conversation.ANSWER_COLOR + replaceParameters(line));
        }
    }

    public boolean processAnswer(String answer) {
        return false;
    }
    
    public String replaceParameters(String line) {
        line = line.replace("%pn", getConversation().getPlayer().getName());
        line = line.replace("%sn", getConversation().getDragonGuard().getDragonStation().getName());
        return line;
    }

    public void wrongAnswerWarning() {
        wrongAnswerWarning(wrongAnswerReply);
    }
    
    public void wrongAnswerWarning(String[] warning) {
        speak(warning, ChatColor.RED);
    }
    
    public void speak(String[] msg) {
        speak(msg, Conversation.SPEAK_COLOR);
    }

    public void speak(String[] msg, ChatColor color) {
        conversation.getPlayer().sendMessage("-----");
        for(String line : msg) {

            conversation.getPlayer().sendMessage(color + replaceParameters(line));
        }
    }

    public Conversation getConversation() {

        return conversation;
    }

    public void setTextToSpeak(String[] textToSpeak) {

        this.textToSpeak = textToSpeak;
    }

    public void setPlayerReply(String[] playerReply) {

        this.playerReply = playerReply;
    }
}

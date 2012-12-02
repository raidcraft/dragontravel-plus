package de.raidcraft.dragontravelplus.npc.conversation.stages;

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

    public Stage(Conversation conversation) {
        this.conversation = conversation;
    }

    public void speak() {
        conversation.getPlayer().sendMessage("-----");
        for(String line : textToSpeak) {

            line = line.replace("%s", getConversation().getPlayer().getName());
            conversation.getPlayer().sendMessage(ChatColor.AQUA + line);
        }
    }

    public Conversation getConversation() {

        return conversation;
    }

    public void setTextToSpeak(String[] textToSpeak) {

        this.textToSpeak = textToSpeak;
    }
}

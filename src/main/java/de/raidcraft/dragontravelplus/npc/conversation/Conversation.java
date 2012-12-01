package de.raidcraft.dragontravelplus.npc.conversation;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

/**
 * Author: Philip
 * Date: 25.11.12 - 17:47
 * Description:
 */
public class Conversation {
    private Player player;
    private NPC npc;

    public Conversation(Player player) {

        this.player = player;
    }
    
    public void trigger(NPC npc, TriggerType triggerType) {

        trigger(npc, triggerType, null);
    }
    
    public void trigger(NPC npc, TriggerType triggerType, String data) {

        
    }

    public enum TriggerType {
        LEFT_CLICK,
        RIGHT_CLICK,
        CHAT_ANSWER,
        REPEAT
    }


}

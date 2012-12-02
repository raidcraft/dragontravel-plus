package de.raidcraft.dragontravelplus.npc.conversation;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.npc.DragonGuardTrait;
import de.raidcraft.dragontravelplus.npc.conversation.stages.DisabledStage;
import de.raidcraft.dragontravelplus.npc.conversation.stages.FirstMeetStage;
import de.raidcraft.dragontravelplus.npc.conversation.stages.NoPermissionStage;
import de.raidcraft.dragontravelplus.npc.conversation.stages.Stage;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Author: Philip
 * Date: 25.11.12 - 17:47
 * Description:
 */
public class Conversation {
    public final static ChatColor SPEAK_COLOR = ChatColor.AQUA;
    private Player player;
    private NPC npc;
    DragonGuardTrait dragonGuard;
    private Stage currentStage;
    private List<DragonStation> playerStations = null;

    public Conversation(Player player) {

        this.player = player;
    }
    
    public void trigger(NPC npc, TriggerType triggerType) {

        trigger(npc, triggerType, null);
    }
    
    public void trigger(NPC npc, TriggerType triggerType, String data) {

        if(playerStations == null) {
            playerStations = StationManager.INST.getPlayerStations(player.getName());
        }

        if(DragonTravelPlusModule.inst.config.disabled && !player.hasPermission("dtp.ignore.disabled")) {
            currentStage = new DisabledStage(this);
        }
        
        if(!player.hasPermission("dtp.use")) {
            currentStage = new NoPermissionStage(this);
        }

        // start new conversation
        if(this.npc != npc) {
            this.npc = npc;
            this.dragonGuard = npc.getTrait(DragonGuardTrait.class);
            currentStage = null;
        }

        // player doesn't know this station
        if(!playerStations.contains(npc.getTrait(DragonGuardTrait.class).getDragonStation())) {
            currentStage = new FirstMeetStage(this);
        }



        if(currentStage == null) {
            return;
        }

        if(triggerType == TriggerType.LEFT_CLICK || triggerType == TriggerType.RIGHT_CLICK || triggerType == TriggerType.REPEAT) {
            currentStage.speak();
        }
    }

    public void setCurrentStage(Stage currentStage) {

        this.currentStage = currentStage;
    }

    public Player getPlayer() {

        return player;
    }

    public NPC getNpc() {

        return npc;
    }

    public DragonGuardTrait getDragonGuard() {

        return dragonGuard;
    }

    public enum TriggerType {
        LEFT_CLICK,
        RIGHT_CLICK,
        CHAT_ANSWER,
        REPEAT
    }


}

package de.raidcraft.dragontravelplus.npc.conversation;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.npc.DragonGuardTrait;
import de.raidcraft.dragontravelplus.npc.conversation.stages.*;
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
    public final static ChatColor ANSWER_COLOR = ChatColor.YELLOW;
    private Player player;
    private NPC npc;
    DragonGuardTrait dragonGuard;
    private Stage currentStage;
    private List<DragonStation> playerStations = null;

    public Conversation(Player player) {

        this.player = player;
    }

    public boolean trigger(TriggerType triggerType, String data) {

        return trigger(null, triggerType, data);
    }
    
    public boolean trigger(NPC npc, TriggerType triggerType) {

        return trigger(npc, triggerType, null);
    }
    
    public boolean trigger(NPC npc, TriggerType triggerType, String data) {

        if(playerStations == null) {
            updatePlayerStations();
        }

        if(triggerType == TriggerType.CHAT_ANSWER && currentStage != null) {
            currentStage.processAnswer(data);
            return true;
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
            currentStage = new SelectDialModeStage(this);
        }

        if(currentStage == null) {
            return false;
        }

        if(triggerType == TriggerType.LEFT_CLICK || triggerType == TriggerType.RIGHT_CLICK || triggerType == TriggerType.REPEAT) {
            currentStage.speak();
        }
        return false;
    }

    public void updatePlayerStations() {
        playerStations = StationManager.INST.getPlayerStations(player.getName());
    }

    public void setCurrentStage(Stage currentStage) {

        this.currentStage = currentStage;
    }

    public Stage getCurrentStage() {

        return currentStage;
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

package de.raidcraft.dragontravelplus.npc.conversation;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.npc.DragonGuardTrait;
import de.raidcraft.dragontravelplus.npc.conversation.stages.*;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Philip
 * Date: 25.11.12 - 17:47
 * Description:
 */
public class Conversation {
    public static Map<String, Conversation> conversations = new HashMap<>();

    public final static ChatColor SPEAK_COLOR = ChatColor.AQUA;
    public final static ChatColor ANSWER_COLOR = ChatColor.YELLOW;
    private Player player;
    private NPC npc = null;
    DragonGuardTrait dragonGuard;
    private Stage currentStage = null;
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
            return currentStage.processAnswer(data);
        }

        // start new conversation
        if(this.npc != npc || this.npc == null) {
            this.npc = npc;
            this.dragonGuard = npc.getTrait(DragonGuardTrait.class);
            updatePlayerStations();
            currentStage = null;
        }

        if(DragonTravelPlusModule.inst.config.disabled && !player.hasPermission("dragontravelplus.ignore.disabled")) {
            currentStage = new DisabledStage(this);
        }
        
        if(!player.hasPermission("dragontravelplus.use")) {
            currentStage = new NoPermissionStage(this);
        }

        // player doesn't know this station
        if(!StationManager.INST.getPlayerStations(player.getName()).contains(npc.getTrait(DragonGuardTrait.class).getDragonStation())) {
            currentStage = new FirstMeetStage(this);
        }

        if(currentStage == null) {
            if(playerStations.size() == 0) {
                currentStage = new NoStationsStage(this);
            }
            else {
                currentStage = new SelectDialModeStage(this);
            }
        }

        if(triggerType == TriggerType.LEFT_CLICK || triggerType == TriggerType.RIGHT_CLICK || triggerType == TriggerType.REPEAT) {
            currentStage.speak();
        }
        return false;
    }

    public void updatePlayerStations() {
        playerStations = StationManager.INST.getPlayerStations(player.getName());
        if(getDragonGuard() != null) {
            playerStations.remove(getDragonGuard().getDragonStation());
        }
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
    
    public List<DragonStation> getPlayerStations() {
        return playerStations;
    }

    public NPC getNpc() {

        return npc;
    }

    public DragonGuardTrait getDragonGuard() {

        return dragonGuard;
    }

    public void abortConversation() {
        setCurrentStage(null);
    }

    public boolean inConversation() {
        if(currentStage != null) {
            return true;
        }
        return false;
    }

    public enum TriggerType {
        LEFT_CLICK,
        RIGHT_CLICK,
        CHAT_ANSWER,
        REPEAT
    }
}

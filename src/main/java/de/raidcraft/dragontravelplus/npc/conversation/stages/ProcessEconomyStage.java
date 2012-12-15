package de.raidcraft.dragontravelplus.npc.conversation.stages;

import com.silthus.raidcraft.bukkit.CorePlugin;
import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.util.FlightCosts;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Philip
 * Date: 02.12.12 - 16:51
 * Description:
 */
public class ProcessEconomyStage extends Stage {
    private Map<Integer, Answer> answers = new HashMap<>();
    private DragonStation target;
    private Stage previousStageInstance;
    private boolean confirm;
    private boolean broke;
    private double price;
    
    public ProcessEconomyStage(Conversation conversation, Stage previousStageInstance, DragonStation target) {
        this(conversation, previousStageInstance, target, false);
    }

    public ProcessEconomyStage(Conversation conversation, Stage previousStageInstance, DragonStation target, boolean confirm) {
        super(conversation);

        this.target = target;
        this.previousStageInstance = previousStageInstance;
        this.confirm = confirm;

        price = FlightCosts.getPrice(getConversation().getDragonGuard().getDragonStation(), target);
        double balance = CorePlugin.get().getEconomy().getBalace(getConversation().getPlayer());

        broke = (price > balance);

        List<String> speech = new ArrayList<>();
        List<String> reply = new ArrayList<>();

        for(String line : DragonTravelPlusModule.inst.config.convProcessEconomyConfirmQuestion) {
            speech.add(line.replace("%sn", target.getName()).replace("%fp", String.valueOf(price)));
        }
        
        if(broke) {
            for(String line : DragonTravelPlusModule.inst.config.convProcessEconomyBroke) {
                speech.add(ChatColor.RED + line);
            }
            answers.put(1, Answer.BACK);
            reply.add(DragonTravelPlusModule.inst.config.convProcessEconomyGoBack);
            answers.put(2, Answer.EXIT);
            reply.add(DragonTravelPlusModule.inst.config.convProcessEconomyExit);
        }
        else {
            answers.put(1, Answer.CONFIRM);
            reply.add(DragonTravelPlusModule.inst.config.convProcessEconomyConfirm);
            answers.put(2, Answer.BACK);
            reply.add(DragonTravelPlusModule.inst.config.convProcessEconomyGoBack);
        }
        
        setTextToSpeak(speech.toArray(new String[speech.size()]));
        setPlayerReply(reply.toArray(new String[reply.size()]));
    }

    @Override
    public void speak() {

        if(broke ||confirm) {
            super.speak();
            showAnswers();
        }
        else {
            takeoff();
        }
    }

    @Override
    public boolean processAnswer(String input) {

        try {
            int choice = Integer.parseInt(input);
            
            Answer answer = answers.get(choice);
            if(answer == null) {
                wrongAnswerWarning();
                return true;
            }
            
            if(answer == Answer.BACK) {
                getConversation().setCurrentStage(previousStageInstance);
                return true;
            }
            
            if(answer == Answer.EXIT) {
                speak(new String[] {DragonTravelPlusModule.inst.config.convProcessEconomyGoodbye});
                getConversation().setCurrentStage(null);
                return true;
            }
            
            if(answer == Answer.CONFIRM) {
                takeoff();
                getConversation().setCurrentStage(null);
                return true;
            }
        }
        catch(Exception e) {
        }
        wrongAnswerWarning();
        return true;
    }

    private void takeoff() {

        CorePlugin.get().getEconomy().substract(getConversation().getPlayer(), price);

        List<String> replacedMsg = new ArrayList<>();
        for(String line : DragonTravelPlusModule.inst.config.convProcessEconomyTakeoff) {
            replacedMsg.add(line.replace("%sn", target.getName()));
        }
        speak(replacedMsg.toArray(new String[replacedMsg.size()]));

        DragonManager.INST.takeoff(getConversation().getPlayer(), target, 5);
    }

    public enum Answer {
        BACK,
        EXIT,
        CONFIRM
    }
}

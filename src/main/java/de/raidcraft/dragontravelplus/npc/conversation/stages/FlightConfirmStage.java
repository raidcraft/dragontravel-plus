package de.raidcraft.dragontravelplus.npc.conversation.stages;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.util.FlightCosts;
import de.raidcraft.dragontravelplus.util.FlightDistance;
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
public class FlightConfirmStage extends Stage {

    private Map<Integer, Answer> answers = new HashMap<>();
    private DragonStation target;
    private Stage previousStageInstance;
    private boolean confirm;
    private boolean broke;
    private double price;

    public FlightConfirmStage(Conversation conversation, Stage previousStageInstance, DragonStation target) {

        this(conversation, previousStageInstance, target, false);
    }

    public FlightConfirmStage(Conversation conversation, Stage previousStageInstance, DragonStation target, boolean confirm) {

        super(conversation);

        this.target = target;
        this.previousStageInstance = previousStageInstance;
        this.confirm = confirm;

        price = FlightCosts.getPrice(getConversation().getDragonGuard().getDragonStation(), target);
        double balance = RaidCraft.getEconomy().getBalance(getConversation().getPlayer().getName());

        broke = (price == 0 || price > balance);

        List<String> speech = new ArrayList<>();
        List<String> reply = new ArrayList<>();

        for (String line : RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convProcessEconomyConfirmQuestion) {
            speech.add(line.replace("%sn", target.getFriendlyName())
                    .replace("%fp", RaidCraft.getEconomy().getFormattedAmount(price))
                    .replace("%fd", FlightDistance.getPrintDistance(getConversation().getDragonGuard().getDragonStation(), target)));
        }

        if (broke) {
            for (String line : RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convProcessEconomyBroke) {
                speech.add(ChatColor.RED + line);
            }
            answers.put(1, Answer.BACK);
            reply.add(RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convProcessEconomyGoBack);
            answers.put(2, Answer.EXIT);
            reply.add(RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convProcessEconomyExit);
        } else {
            answers.put(1, Answer.CONFIRM);
            reply.add(RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convProcessEconomyConfirm);
            answers.put(2, Answer.BACK);
            reply.add(RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convProcessEconomyGoBack);
        }

        setTextToSpeak(speech.toArray(new String[speech.size()]));
        setPlayerReply(reply.toArray(new String[reply.size()]));
    }

    @Override
    public void speak() {

        if (broke || confirm) {
            super.speak();
            showAnswers();
        } else {
            takeoff();
            getConversation().setCurrentStage(null);
        }
    }

    @Override
    public boolean processAnswer(String input) {

        try {
            int choice = Integer.parseInt(input);

            Answer answer = answers.get(choice);
            if (answer == null) {
                wrongAnswerWarning();
                return true;
            }

            if (answer == Answer.BACK) {
                getConversation().setCurrentStage(previousStageInstance);
                getConversation().getCurrentStage().speak();
                return true;
            }

            if (answer == Answer.EXIT) {
                speak(new String[]{RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convProcessEconomyGoodbye});
                getConversation().setCurrentStage(null);
                return true;
            }

            if (answer == Answer.CONFIRM) {
                takeoff();
                getConversation().setCurrentStage(null);
                return true;
            }
        } catch (Exception e) {
        }
        wrongAnswerWarning();
        return true;
    }

    private void takeoff() {

        List<String> replacedMsg = new ArrayList<>();
        for (String line : RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.convProcessEconomyTakeoff) {
            replacedMsg.add(line.replace("%sn", target.getFriendlyName()));
        }
        speak(replacedMsg.toArray(new String[replacedMsg.size()]));

        DragonManager.INST.takeoff(getConversation().getPlayer(), getConversation().getDragonGuard().getDragonStation(), target, price);
    }

    public enum Answer {
        BACK,
        EXIT,
        CONFIRM
    }
}

package de.raidcraft.dragontravelplus.npc.conversation.stages;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 02.12.12 - 16:51
 * Description:
 */
public class FlightByNameStage extends Stage {

    public FlightByNameStage(Conversation conversation) {

        super(conversation);
        
        setTextToSpeak(DragonTravelPlusModule.inst.config.convFlightByNameSpeak);
    }

    @Override
    public void speak() {

        super.speak();
    }

    @Override
    public boolean processAnswer(String answer) {

        DragonStation station = StationManager.INST.getDragonStation(answer);

        if(station == null) {
            wrongAnswerWarning(DragonTravelPlusModule.inst.config.convFlightByNameUnknownStation);
            return true;
        }

        if(station.getName() == getConversation().getDragonGuard().getDragonStation().getName()) {
            wrongAnswerWarning(DragonTravelPlusModule.inst.config.convFlightByNameSameStation);
            return true;
        }

        List<String> replacedMsg = new ArrayList<>();
        for(String line : DragonTravelPlusModule.inst.config.convFlightByNameTakeoff) {
            replacedMsg.add(line.replace("%ds", station.getName()));
        }
        
        speak(replacedMsg.toArray(new String[replacedMsg.size()]));

        CommandBook.inst().getServer().getScheduler().scheduleSyncDelayedTask(CommandBook.inst(), new Runnable() {
            public void run() {

                Bukkit.broadcastMessage("dragon flight");
            }
        }, 3*20);

        return true;
    }
}

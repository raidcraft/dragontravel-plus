package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.rcconversations.api.action.*;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.util.ParseString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Philip
 */
@ActionInformation(name = "DTP_CONTROLLED")
public class FlyControlledAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        int delay = args.getInt("delay", 0);
        String stringDuration = args.getString("duration");
        stringDuration = ParseString.INST.parse(conversation, stringDuration);

        double duration;
        try {
            duration = Double.parseDouble(stringDuration);
        } catch (NumberFormatException e) {
            throw new WrongArgumentValueException("Wrong argument value in withAction '" + getName() + "': Duration must be a number!");
        }

        StartControlledFlightTask task = new StartControlledFlightTask(conversation.getPlayer(), (int) duration);
        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(DragonTravelPlusPlugin.class), task, delay);
    }

    public class StartControlledFlightTask implements Runnable {

        private Player player;
        private int duration;

        public StartControlledFlightTask(Player player, int duration) {

            this.player = player;
            this.duration = duration;
        }

        public void run() {

            // TODO: reimplement
        }
    }
}

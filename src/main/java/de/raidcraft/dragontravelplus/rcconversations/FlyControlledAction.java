package de.raidcraft.dragontravelplus.rcconversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.ControlledFlight;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightTravel;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Philip
 */
public class FlyControlledAction extends AbstractAction {

    public FlyControlledAction(String name) {

        super(name);
    }

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws Throwable {

        int delay = args.getInteger("delay", 0);
        int duration = args.getInteger("duration", 0);

        StartControlledFlightTask task = new StartControlledFlightTask(conversation.getPlayer(), duration);
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

            FlightTravel.flyControlled(new ControlledFlight(duration), player);
        }
    }
}

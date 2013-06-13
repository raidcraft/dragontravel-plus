package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import de.raidcraft.rcconversations.api.action.*;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Philip
 */
@ActionInformation(name = "DTP_STATION")
public class FlyToStationAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String startName = args.getString("start", null);
        String targetName = args.getString("target");
        int delay = args.getInt("delay");
        DragonStation target = StationManager.INST.getDragonStation(targetName);
        if(target == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Station '" + targetName + "' does not exists!");
        }

        DragonStation start;
        if(startName == null) {
            start = new DragonStation(conversation.getPlayer().getLocation());
        }
        else {
            start = StationManager.INST.getDragonStation(startName);
        }
        if(start == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Station '" + targetName + "' does not exists!");
        }

        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new TakoffDelayedTask(start, target, conversation.getPlayer()), delay);
    }

    public class TakoffDelayedTask implements Runnable {

        DragonStation start;
        DragonStation target;
        Player player;

        public TakoffDelayedTask(DragonStation start, DragonStation target, Player player) {

            this.start = start;
            this.target = target;
            this.player = player;
        }

        @Override
        public void run() {

            DragonManager.INST.takeoff(player, start, target, 0);
        }
    }
}

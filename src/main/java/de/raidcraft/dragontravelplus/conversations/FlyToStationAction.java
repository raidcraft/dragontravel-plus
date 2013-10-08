package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import de.raidcraft.rcconversations.RCConversationsPlugin;
import de.raidcraft.rcconversations.api.action.*;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.conversations.EndReason;
import de.raidcraft.rcconversations.util.MathHelper;
import de.raidcraft.rcconversations.util.ParseString;
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
        String targetName = args.getString("target", null);
        targetName = ParseString.INST.parse(conversation, targetName);
        startName = ParseString.INST.parse(conversation, startName);
        int delay = args.getInt("delay", 0);
        String priceString = args.getString("price", "0");
        priceString = ParseString.INST.parse(conversation, priceString);
        double price = MathHelper.solveDoubleFormula(priceString);

        DragonStation target = StationManager.INST.getDragonStation(targetName);
        if(target == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Station '" + targetName + "' does not exists!");
        }

        DragonStation start;
        if(startName == null) {
            start = new DragonStation(conversation.getName(), conversation.getPlayer().getLocation().clone());
        }
        else {
            start = StationManager.INST.getDragonStation(startName);
        }
        if(start == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Station '" + targetName + "' does not exists!");
        }

        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new TakeoffDelayedTask(start, target, conversation.getPlayer(), price), delay);
    }

    public class TakeoffDelayedTask implements Runnable {

        DragonStation start;
        DragonStation target;
        Player player;
        double price;

        public TakeoffDelayedTask(DragonStation start, DragonStation target, Player player, double price) {

            this.start = start;
            this.target = target;
            this.player = player;
            this.price = price;
        }

        @Override
        public void run() {

            RaidCraft.getComponent(RCConversationsPlugin.class).getConversationManager().endConversation(player.getName(), EndReason.SILENT);
            DragonManager.INST.takeoff(player, start, target, price);

        }
    }
}

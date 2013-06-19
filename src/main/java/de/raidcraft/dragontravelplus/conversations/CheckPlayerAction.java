package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import de.raidcraft.dragontravelplus.util.FlightCosts;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.util.ParseString;

/**
 * @author Philip
 */
@ActionInformation(name = "DTP_CHECK_PLAYER")
public class CheckPlayerAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String startName = args.getString("start", null);
        startName = ParseString.INST.parse(conversation, startName);
        String targetName = args.getString("target", null);
        targetName = ParseString.INST.parse(conversation, targetName);
        String success = args.getString("onsuccess", null);
        String failure = args.getString("onfailure", null);
        boolean checkPrice = args.getBoolean("price", false);
        boolean checkFamiliarity = args.getBoolean("familiarity", false);

        DragonStation start = StationManager.INST.getDragonStation(startName);
        DragonStation target = StationManager.INST.getDragonStation(targetName);

        if(start == null) {
            setErrorMsg(conversation, "Es ist ein Fehler aufgetreten! Bitte informiere das Raid-Craft Team!");
            changeStage(conversation, failure);
            return;
        }

        if(target == null) {
            setErrorMsg(conversation, "Die angegebene Station existiert nicht!");
            changeStage(conversation, failure);
            return;
        }

        if(start.equals(target)) {
            setErrorMsg(conversation, "Du befindest dich bereits an dieser Station!");
            changeStage(conversation, failure);
            return;
        }

        conversation.set("dtp_target_name", target.getName());
        conversation.set("dtp_target_friendlyname", target.getFriendlyName());
        conversation.set("dtp_target_distance", target.getDistance(start));

        Economy economy = RaidCraft.getEconomy();
        double price = FlightCosts.getPrice(start, target);
        conversation.set("dtp_target_price", price);
        conversation.set("dtp_target_price_formatted", economy.getFormattedAmount(price));
        if(checkPrice && !economy.hasEnough(conversation.getPlayer().getName(), price)) {
            setErrorMsg(conversation, "Du brauchst " + economy.getFormattedAmount(price) + " um dorthin zu fliegen!");
            changeStage(conversation, failure);
            return;
        }

        if(checkFamiliarity && !StationManager.INST.stationIsFamiliar(conversation.getPlayer(), target)) {
            setErrorMsg(conversation, "Du musst den Drachenmeister dieser Station erst noch kennen lernen!");
            changeStage(conversation, failure);
            return;
        }

        changeStage(conversation, success);
    }

    private void setErrorMsg(Conversation conversation, String msg) {

        conversation.set("dtp_target_error", msg);
    }

    private void changeStage(Conversation conversation, String failureStage) {

        if(failureStage != null) {
            conversation.setCurrentStage(failureStage);
            conversation.triggerCurrentStage();
        }
    }
}

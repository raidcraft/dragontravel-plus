package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.dragontravelplus.StationManager;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.util.ParseString;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.api.station.UnknownStationException;
import de.raidcraft.util.LocationUtil;

import java.util.UUID;

/**
 * @author Philip
 */
@ActionInformation(name = "DTP_CHECK_PLAYER")
public class CheckPlayerAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException, UnknownStationException {

        String success = args.getString("onsuccess", null);
        String failure = args.getString("onfailure", null);

        String startName = args.getString("start", null);
        startName = ParseString.INST.parse(conversation, startName);
        String targetName = conversation.getString("dtp_target_friendlyname", null);
        // hotfix for manual input
        if (targetName == null) {
            targetName = args.getString("target", null);
        }
        targetName = ParseString.INST.parse(conversation, targetName);
        if (targetName == null) {
            setErrorMsg(conversation, "Keine gültige Station");
            changeStage(conversation, failure);
            return;
        }
        boolean checkPrice = args.getBoolean("price", false);
        boolean checkFamiliarity = args.getBoolean("familiarity", false);

        StationManager stationManager = RaidCraft.getComponent(StationManager.class);
        Station start = null;
        Station target = null;
        try {
            start = stationManager.getStation(startName);
            target = stationManager.getStation(targetName);
        } catch (UnknownStationException e) {
            setErrorMsg(conversation, e.getMessage());
            changeStage(conversation, failure);
            return;
        }

        if (start == null) {
            setErrorMsg(conversation, "Es ist ein Fehler aufgetreten! Bitte informiere das Raid-Craft Team!");
            changeStage(conversation, failure);
            return;
        }

        if (target == null) {
            setErrorMsg(conversation, "Die angegebene Station existiert nicht!");
            changeStage(conversation, failure);
            return;
        }

        if (start.equals(target)) {
            setErrorMsg(conversation, "Du befindest dich bereits an dieser Station!");
            changeStage(conversation, failure);
            return;
        }

        conversation.set("dtp_target_name", target.getName());
        conversation.set("dtp_target_friendlyname", target.getDisplayName());
        conversation.set("dtp_target_distance", LocationUtil.getDistance(target.getLocation(), start.getLocation()));

        Economy economy = RaidCraft.getEconomy();
        double price = 0.0;
        if (start instanceof DragonStation && target instanceof DragonStation) {
            price = ((DragonStation) start).getPrice((DragonStation) target);
        }
        conversation.set("dtp_target_price", price);
        conversation.set("dtp_target_price_formatted", economy.getFormattedAmount(price));
        UUID player = conversation.getPlayer().getUniqueId();
        if (checkPrice && !economy.hasEnough(player, price)) {
            setErrorMsg(conversation, "Du brauchst " + economy.getFormattedAmount(price) + " um dorthin zu fliegen!");
            changeStage(conversation, failure);
            return;
        }

        if (checkFamiliarity && (target instanceof DragonStation && !((DragonStation) target).hasDiscovered(player))) {
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

        if (failureStage != null) {
            conversation.setCurrentStage(failureStage);
            conversation.triggerCurrentStage();
        }
    }
}

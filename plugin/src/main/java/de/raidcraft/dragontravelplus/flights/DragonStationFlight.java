package de.raidcraft.dragontravelplus.flights;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.FlightException;
import de.raidcraft.api.flight.flight.Path;
import de.raidcraft.api.flight.flight.RCStartFlightEvent;
import de.raidcraft.api.language.Translator;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.util.GUIUtil;
import de.raidcraft.rctravel.api.station.Chargeable;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.hero.Option;
import de.raidcraft.util.LocationUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Silthus
 */
public class DragonStationFlight extends RestrictedFlight {

    private final Station startStation;
    private final Station endStation;
    private int updateGUITaskID;

    public DragonStationFlight(Station startStation, Station endStation, Aircraft<?> aircraft, Path path) {

        super(aircraft, path, startStation.getLocation(), endStation.getLocation());
        this.startStation = startStation;
        this.endStation = endStation;
    }

    public Station getStartStation() {

        return startStation;
    }

    public Station getEndStation() {

        return endStation;
    }

    private double getPrice() {

        double price = 0.0;
        if (getStartStation() instanceof DragonStation && getEndStation() instanceof DragonStation) {
            price = ((DragonStation) startStation).getPrice((DragonStation) endStation);
        } else if (getStartStation() instanceof Chargeable) {
            price = ((Chargeable) getStartStation()).getPrice(
                    LocationUtil.getBlockDistance(getStartStation().getLocation(), getEndStation().getLocation()));
        }
        return price;
    }

    @Override
    public void onStartFlight() throws FlightException {

        if (getPassenger().getEntity() instanceof Player) {
            Economy economy = RaidCraft.getEconomy();
            if (!economy.hasEnough(getPassenger().getEntity().getUniqueId(), getPrice())) {
                throw new FlightException(Translator.tr(DragonTravelPlusPlugin.class, (Player) getPassenger().getEntity(),
                        "flight.no-money", "You dont have enough money to complete this flight!"));
            }
            RCStartFlightEvent event = new RCStartFlightEvent((Player) getPassenger().getEntity(), this);
            RaidCraft.callEvent(event);
            if (event.isCancelled()) {
                if (event.getMessage() != null)
                    throw new FlightException(event.getMessage());
                throw new FlightException("Flug konnte nicht gestartet werden.");
            }

            // start thread to update GUI (distance view in title bar)
            Hero hero = RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getHero((Player)getPassenger().getEntity());
            Option.ACTION_BAR.set(hero, false); // disable actionbar
            updateGUITaskID = Bukkit.getScheduler().runTaskTimer(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new UpdateGuiTask(), 10, 20).getTaskId();
        }
        super.onStartFlight();
    }

    @Override
    public void onEndFlight() throws FlightException {

        // lets substract the flight cost
        Economy economy = RaidCraft.getEconomy();
        double price = getPrice();
        if (getPassenger().getEntity() instanceof Player) {
            if (!economy.hasEnough(getPassenger().getEntity().getUniqueId(), price)) {
                // abort the flight
                abortFlight();
                throw new FlightException(Translator.tr(DragonTravelPlusPlugin.class, (Player) getPassenger().getEntity(),
                        "flight.no-money", "You dont have enough money to complete this flight!"));
            }
            economy.substract(getPassenger().getEntity().getUniqueId(), price);
        }
        super.onEndFlight();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private class UpdateGuiTask implements Runnable {

        private int arrivalTime;
        private double lastDistance;
        private int lastBlocksPerSecond;

        @Override
        public void run() {

            Player player = (Player)getPassenger().getEntity();

            // cancel this task if flight was aborted
            if(!isActive() || player.getVehicle() == null) {

                if(isActive()) {
                    abortFlight();
                    player.sendMessage(ChatColor.RED + "Du bist während des Fluges abgestiegen!");
                } // maybe player is not on dragon but flight is still active
                Hero hero = RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getHero(player);
                Option.ACTION_BAR.set(hero, true); // enable actionbar
                Bukkit.getScheduler().cancelTask(updateGUITaskID);
                return;
            }

            double newDistance = round(player.getLocation().distance(getEndLocation()), 2);
            int totalDistance = (int)round(getEndLocation().distance(getStartLocation()), 0);

            if(lastDistance == 0) {
                lastDistance = totalDistance;
            }

            // print welcome message if distance is short enough
            if(newDistance < 70) {
                GUIUtil.setTitleBarText(player,
                        ChatColor.DARK_GRAY + "*** " +
                                ChatColor.DARK_PURPLE + "Du hast dein Ziel erreicht: " +
                                ChatColor.GOLD + getEndStation().getDisplayName() +
                                ChatColor.DARK_GRAY + " ***");
                return;
            }

            // dragon is flying backwards!? ;)
            if(newDistance > lastDistance) {
                newDistance = lastDistance;
            }

            /**
             * Arrival Time Calculation
             */
            int blocksPerSeconds = (int)(lastDistance - newDistance);

            // first speed calculation
            if(lastBlocksPerSecond == 0) lastBlocksPerSecond = blocksPerSeconds;
            // check if delta is too high and correct it
            if((lastBlocksPerSecond - blocksPerSeconds) > 2) {
                blocksPerSeconds = lastBlocksPerSecond - 2;
            } else if((lastBlocksPerSecond - blocksPerSeconds) < -2){
                blocksPerSeconds = lastBlocksPerSecond + 2;
            }
            // we are slower than the value we measured
            blocksPerSeconds -= 3;

            if(blocksPerSeconds > 1) {
                arrivalTime = (int)newDistance / blocksPerSeconds;
            }
            String arrivalTimeString;
            if(arrivalTime < 60) {
                arrivalTimeString = ChatColor.GOLD.toString() + arrivalTime + "s";
            } else {
                arrivalTimeString = ChatColor.GOLD.toString() + (arrivalTime/60) + "min";
            }

            /**
             * Distance Calculation
             */

            Integer intDistance = new Integer((int)newDistance);
            String distanceString = ChatColor.GOLD.toString() + intDistance.toString() + "m";


            lastDistance = newDistance;

            GUIUtil.setTitleBarText(player,
                    ChatColor.DARK_GRAY + "*** " +
                            ChatColor.DARK_PURPLE + "Entfernung zum Ziel: " +
                            ChatColor.GOLD + distanceString +
                            ChatColor.DARK_GRAY + " | " +
                            ChatColor.DARK_PURPLE + "Ankunft in ca. " + arrivalTimeString +
                            ChatColor.DARK_GRAY + " ***");
        }
    }
}

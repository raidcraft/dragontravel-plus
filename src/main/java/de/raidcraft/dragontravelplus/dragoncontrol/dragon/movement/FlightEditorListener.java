package de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class FlightEditorListener implements Listener {

    public static HashMap<String, Flight> editors = new HashMap<>();

    public FlightEditorListener() {

    }

    public static void addPlayer(String player, String name) {

        editors.put(player, new Flight(name));
    }

    /**
     * Checks if the passed player is an editor
     *
     * @param player
     *
     * @return
     */
    public static boolean hasEditorMode(String player) {

        if (editors.containsKey(player))
            return true;
        else
            return false;
    }

    /**
     * Removes the passed player from editor mode
     *
     * @param player
     */
    public static void removePlayer(String player) {

        editors.remove(player);
    }

    @EventHandler
    public void onWP(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Location location = player.getLocation();

        if (!editors.containsKey(player.getName()))
            return;

        if (player.getItemInHand().getTypeId() != RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightEditorItem)
            return;

        Flight flight = editors.get(player.getName());

        //remove wp
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if(flight.waypointCount() != 0) {
                editors.get(player.getName()).removeWaypoint();
                ChatMessages.info(player, "Letzter Wegpunkt entfernt!");
                return;
            }
            else {
                ChatMessages.warn(player, "Der Flug hat noch keine Wegpunkte!");
            }
        }

        // add wp
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

            if(flight.waypointCount() != 0 && !flight.getFlightWorld().equalsIgnoreCase(location.getWorld().getName())) {
                ChatMessages.warn(player, "Du hast im Flugeditor die Welt gewechselt!");
                ChatMessages.warn(player, "Beende den Editor oder gehe zurück auf die Welt '" + flight.getFlightWorld() + "'!");
                return;
            }

            location.setY(location.getY() - 3);
            WayPoint wp = new WayPoint(location);
            flight.addWaypoint(wp);
            ChatMessages.success(player, flight.waypointCount() + ". Wegpunkt hinzugefügt!");
        }
    }

}

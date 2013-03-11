package de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class FlightEditor implements Listener {

    public static HashMap<Player, Flight> editors = new HashMap<Player, Flight>();

    public FlightEditor() {

    }

    public FlightEditor(Player player, String name) {

        editors.put(player, new Flight(name));
    }

    /**
     * Checks if the passed player is an editor
     *
     * @param player
     *
     * @return
     */
    public static boolean isEditor(Player player) {

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
    public static void removeEditor(Player player) {

        editors.remove(player);
    }

    @EventHandler
    public void onWP(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Location loc = player.getLocation();

        if (!editors.containsKey(player))
            return;

        if (player.getItemInHand().getTypeId() != 281)
            return;

        //remove wp
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            editors.get(player).removeWaypoint();
            return;
        }

        // add wp
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

            Flight flight = editors.get(player);
            WaypointFake wp = new WaypointFake(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
            flight.addWaypoint(wp);
        }
    }

}

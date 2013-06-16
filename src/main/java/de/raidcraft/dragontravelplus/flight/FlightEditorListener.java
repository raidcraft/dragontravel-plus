package de.raidcraft.dragontravelplus.flight;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class FlightEditorListener implements Listener {

    public static final Material MARKER_MATERIAL = Material.GLOWSTONE;

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

        Flight flight = editors.remove(player);
        if(flight != null) {
            flight.removeMarkers();
        }
    }

    @EventHandler
    public void onWP(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Location location = player.getLocation();

        if (!editors.containsKey(player.getName()))
            return;

        if (player.getItemInHand().getTypeId() != RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().flightEditorItem)
            return;

        Flight flight = editors.get(player.getName());

        //remove wp
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if(flight.size() != 0) {
                WayPoint wp = editors.get(player.getName()).removeWaypoint();
                if(wp != null && wp.getLocation().getBlock().getType() == MARKER_MATERIAL) {
                    wp.getLocation().getBlock().setType(Material.AIR);
                }
                ChatMessages.info(player, "Wegpunkt entfernt! (" + flight.size() + " übrig)");
                return;
            }
            else {
                ChatMessages.warn(player, "Der Flug hat noch keine Wegpunkte!");
            }
        }

        // add wp
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

            if(flight.size() != 0 && !flight.getFlightWorld().equalsIgnoreCase(location.getWorld().getName())) {
                ChatMessages.warn(player, "Du hast im Flugeditor die Welt gewechselt!");
                ChatMessages.warn(player, "Beende den Editor oder gehe zurück auf die Welt '" + flight.getFlightWorld() + "'!");
                return;
            }

            if(location.getBlock().getType() == Material.AIR) {
                Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(DragonTravelPlusPlugin.class),
                        new MarkerTask(location.getBlock()), 30);
            }
            WayPoint wp = new WayPoint(location);
            flight.addWaypoint(wp);
            ChatMessages.success(player, flight.size() + ". Wegpunkt hinzugefügt!");
        }
    }

    public class MarkerTask implements Runnable {

        Block block;

        public MarkerTask(Block block) {

            this.block = block;
        }

        public void run() {
            block.setType(MARKER_MATERIAL);
        }
    }

}

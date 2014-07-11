package de.raidcraft.dragontravelplus.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.api.flight.flight.Path;
import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.dragontravelplus.paths.StaticFlightPath;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

/**
 * @author Silthus
 */
// TODO: doku
// TODO: sollte player basiert sein, wie abstract flight und sich unregistern
public class FlightEditorListener implements Listener {

    public static final Material MARKER_MATERIAL = Material.GLOWSTONE;

    public static HashMap<Player, Path> editors = new HashMap<>();

    public FlightEditorListener() {

    }

    public static void addPlayer(Player player) {

        StaticFlightPath path = new StaticFlightPath();
        path.showWaypoints();
        editors.put(player, path);
    }

    /**
     * Checks if the passed player is an editor
     *
     * @param player
     *
     * @return
     */
    public static boolean hasEditorMode(Player player) {

        return editors.containsKey(player);
    }

    /**
     * Removes the passed player from editor mode
     *
     * @param player
     */
    public static void removePlayer(Player player) {

        Path path = editors.remove(player);
        if (path != null) {
            path.hideWaypoints();
        }
    }

    // TODO: playercheck
    @EventHandler
    public void onWP(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Location location = player.getLocation();

        if (!editors.containsKey(player)) return;

        if (player.getItemInHand().getTypeId() != RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().flightEditorItem) return;

        Path path = editors.get(player);

        // add wp
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

            if (path.getWaypointAmount() > 0 && !path.getFirstWaypoint().getWorld().equals(location.getWorld())) {
                player.sendMessage(ChatColor.RED + "Du hast im Flugeditor die Welt gewechselt!");
                player.sendMessage(ChatColor.RED + "Beende den Editor oder gehe zurück auf die Welt '"
                        + path.getFirstWaypoint().getWorld().getName() + "'!");
                return;
            }

            Waypoint waypoint = new Waypoint(location);
            path.addWaypoint(waypoint);
            player.sendMessage(ChatColor.GREEN + "" + path.getWaypointAmount() + ". Wegpunkt hinzugefügt!");
        }
    }

    // TODO: playercheck
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (!editors.containsKey(player)) return;

        if (event.getBlock().getType() != MARKER_MATERIAL) return;

        Path path = editors.get(player);
        Waypoint removeWaypoint = path.removeWaypoint(new Waypoint(event.getBlock().getLocation()));
        if (removeWaypoint != null) {
            event.getPlayer().sendMessage(ChatColor.GREEN + "Wegpunkt entfernt!");
        }
    }
}

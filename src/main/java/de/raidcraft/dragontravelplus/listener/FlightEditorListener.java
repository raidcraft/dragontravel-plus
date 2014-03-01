package de.raidcraft.dragontravelplus.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.api.flight.Path;
import de.raidcraft.dragontravelplus.api.flight.Waypoint;
import de.raidcraft.dragontravelplus.paths.StaticFlightPath;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

/**
 * @author Silthus
 */
public class FlightEditorListener {

    public static final Material MARKER_MATERIAL = Material.GLOWSTONE;

    public static HashMap<Player, Path> editors = new HashMap<>();

    public FlightEditorListener() {

    }

    public static void addPlayer(Player player) {

        editors.put(player, new StaticFlightPath());
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
        if(path != null) {
            path.hideWaypoints();
        }
    }

    @EventHandler
    public void onWP(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Location location = player.getLocation();

        if (!editors.containsKey(player)) return;

        if (player.getItemInHand().getTypeId() != RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().flightEditorItem) return;

        Path path = editors.get(player);

        // add wp
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

            if(path.getWaypointAmount() > 0 && !path.getFirstWaypoint().getWorld().equals(location.getWorld())) {
                player.sendMessage(ChatColor.RED + "Du hast im Flugeditor die Welt gewechselt!");
                player.sendMessage(ChatColor.RED + "Beende den Editor oder gehe zurück auf die Welt '"
                        + path.getFirstWaypoint().getWorld().getName() + "'!");
                return;
            }

            if(location.getBlock().getType() == Material.AIR) {
                Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(DragonTravelPlusPlugin.class),
                        new MarkerTask(location.getBlock()), 30);
            }
            Waypoint waypoint = new Waypoint(location);
            path.addWaypoint(waypoint);
            player.sendMessage(ChatColor.GREEN + "" + path.getWaypointAmount() + ". Wegpunkt hinzugefügt!");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (!editors.containsKey(player)) return;

        if(event.getBlock().getType() != MARKER_MATERIAL) return;

        Path path = editors.get(player);
        Waypoint removeWaypoint = path.removeWaypoint(new Waypoint(event.getBlock().getLocation()));
        if(removeWaypoint != null) {
            event.getPlayer().sendMessage(ChatColor.GREEN + "Wegpunkt entfernt!");
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

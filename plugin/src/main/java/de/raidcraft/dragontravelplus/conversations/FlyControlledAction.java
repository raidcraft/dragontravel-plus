package de.raidcraft.dragontravelplus.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Philip
 */
public class FlyControlledAction implements Action<Player> {

    @Override
    @Information(
            value = "flight.controlled",
            desc = "Starts a player controlled flight for the given amount of time.",
            conf = {
                    "duration: 10s - amount of time to allow the controlled flight",
                    "delay: 1s - how long to delay the takeoff"
            },
            aliases = "DTP_CONTROLLED"
    )
    public void accept(Player player, ConfigurationSection config) {

        long delay = TimeUtil.parseTimeAsTicks(config.getString("delay", "1s"));
        long duration = TimeUtil.parseTimeAsTicks(config.getString("duration"));

        StartControlledFlightTask task = new StartControlledFlightTask(player, (int) duration);
        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(DragonTravelPlusPlugin.class), task, delay);
    }

    public class StartControlledFlightTask implements Runnable {

        private Player player;
        private int duration;

        public StartControlledFlightTask(Player player, int duration) {

            this.player = player;
            this.duration = duration;
        }

        public void run() {

            // TODO: reimplement
        }
    }
}

package de.raidcraft.dragontravelplus.dragoncontrol.dragon.modules;

import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import net.minecraft.server.v1_4_5.World;
import org.bukkit.craftbukkit.v1_4_5.CraftWorld;
import org.bukkit.entity.Player;

/**
 * Handles all things related to stationary dragons,<br>
 * those dragons which are just on stations
 */
public class StationaryDragon {

	/**
	 * Creates a stationary dragon
	 */
	public static void createStatDragon(Player player) {
		World notchWorld = ((CraftWorld) player.getWorld()).getHandle();
        RCDragon dragon = new RCDragon(player.getLocation(), notchWorld);
		notchWorld.addEntity(dragon);
    }
}

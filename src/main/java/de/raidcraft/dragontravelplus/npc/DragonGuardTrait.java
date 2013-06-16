package de.raidcraft.dragontravelplus.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;

/**
 * Author: Philip
 * Date: 22.11.12 - 21:21
 * Description:
 */
public class DragonGuardTrait extends Trait {

    public DragonGuardTrait() {

        super("dragonguard");
    }

    @Override
    public void onSpawn() {

        super.onSpawn();

        // delete all old npcs
        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new Runnable() {
            @Override
            public void run() {
                npc.destroy();
            }
        }, 20);
    }
}

package de.raidcraft.dragontravelplus;

import com.silthus.raidcraft.util.component.database.ComponentDatabase;
import com.sk89q.commandbook.CommandBook;
import com.sk89q.worldedit.LocalConfiguration;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;
import de.raidcraft.dragontravelplus.npc.DragonGuardTrait;
import de.raidcraft.dragontravelplus.npc.NPCListener;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;

/**
 * Author: Philip
 * Date: 22.11.12 - 06:01
 * Description:
 */
@ComponentInformation(
        friendlyName = "Dragon Travel Plus Module",
        desc = "Sends the dragons into the air."
)
public class DragonTravelPlusModule extends BukkitComponent {

    public static DragonTravelPlusModule inst;
    public LocalConfiguration config;
    private int startTaskId;

    @Override
    public void enable() {
        inst = this;
        startTaskId = CommandBook.inst().getServer().getScheduler().scheduleSyncRepeatingTask(CommandBook.inst(), new Runnable() {
            public void run() {
                if(ComponentDatabase.INSTANCE.getConnection() != null) {

                    if(CommandBook.server().getPluginManager().getPlugin("Citizens") == null
                        || CommandBook.server().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
                        CommandBook.logger().warning("Citizens 2.0 not found or not enabled! Disabling DragonTravelPro!");
                        DragonTravelPlusModule.inst.disable();
                        CommandBook.server().getScheduler().cancelTask(startTaskId);
                        return;
                    }
                    else {
                        loadConfig();
                        CommandBook.registerEvents(new NPCListener());
                        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(DragonGuardTrait.class).withName("dragonguard"));
                        CommandBook.server().getScheduler().cancelTask(startTaskId);

                        CommandBook.logger().info("[DragonTravelPlus] Found DB connection, init DTPlus module...");
                }
                }
            }
        }, 0, 2*20);
    }

    public void loadConfig() {

        config = configure(new LocalConfiguration());
    }

    public class LocalConfiguration extends ConfigurationBase {

        @Setting("") public int n = 0;
    }
}

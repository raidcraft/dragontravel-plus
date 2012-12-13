package de.raidcraft.dragontravelplus.npc;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Philip
 * Date: 22.11.12 - 21:21
 * Description:
 */
public class DragonGuardTrait extends Trait {
    public static Map<String, DragonGuardTrait> dragonGuards = new HashMap<>();
    private DragonStation station;

    public DragonGuardTrait() {
        super("dragonguard");
    }

    @Override
    public void load(DataKey key) throws NPCLoadException {

        super.load(key);
    }

    @Override
    public void onAttach() {

        super.onAttach();
    }

    @Override
    public void onCopy() {

        super.onCopy();
    }

    @Override
    public void onDespawn() {

        super.onDespawn();
    }

    @Override
    public void onRemove() {
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        NPC npc = getNPC();
        npc.addTrait(LookClose.class);
        npc.getTrait(LookClose.class).lookClose(true);
        npc.data().set(NPC.DEFAULT_PROTECTED_METADATA, true);
        npc.addTrait(Equipment.class);
        npc.getTrait(Equipment.class).set(1, new ItemStack(Material.LEATHER_HELMET));
        npc.getTrait(Equipment.class).set(2, new ItemStack(Material.LEATHER_CHESTPLATE));
        npc.getTrait(Equipment.class).set(3, new ItemStack(Material.LEATHER_LEGGINGS));
        npc.getTrait(Equipment.class).set(4, new ItemStack(Material.LEATHER_BOOTS));

        if(!npc.getName().equalsIgnoreCase(DragonTravelPlusModule.inst.config.npcDefaultName)) {
            npc.setName(DragonTravelPlusModule.inst.config.npcDefaultName);
        }

        station = StationManager.INST.getNearbyStation(npc.getBukkitEntity().getLocation());
        if(station == null) {
            LivingEntity entity = npc.getBukkitEntity();
            CommandBook.logger().warning("[DTP] NPC despawned at"
                    + " x:" + entity.getLocation().getBlockX()
                    + " y:" + entity.getLocation().getBlockY()
                    + " z:" + entity.getLocation().getBlockZ()
                    + "! Station not found!");
            npc.destroy();
            return;
        }
        dragonGuards.remove(station.getName());
        dragonGuards.put(station.getName(), this);
    }

    @Override
    public void run() {

        super.run();
    }

    public void setDragonStation(DragonStation station) {
        this.station = station;
    }

    public DragonStation getDragonStation() {

        return station;
    }
}

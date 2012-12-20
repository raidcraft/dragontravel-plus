package de.raidcraft.dragontravelplus.npc;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.api.trait.trait.Spawned;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

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
    public void onDespawn() {

        station = StationManager.INST.getNearbyStation(npc.getBukkitEntity().getLocation());
        if(station != null) {
            dragonGuards.remove(station.getName());
        }
        
        super.onDespawn();
    }

    @Override
    public void onRemove() {
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        NPC npc = getNPC();

        // change name
        if(!npc.getName().equalsIgnoreCase(DragonTravelPlusModule.inst.config.npcDefaultName)) {
            npc.setName(DragonTravelPlusModule.inst.config.npcDefaultName);
        }

        // link station
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
    
    public static void createDragonGuard(Location location, DragonStation station) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, DragonTravelPlusModule.inst.config.npcDefaultName);
        npc.addTrait(DragonGuardTrait.class);
        npc.getTrait(DragonGuardTrait.class).setDragonStation(station);


        // add traits
        npc.addTrait(Spawned.class);
        npc.addTrait(LookClose.class);
//        npc.addTrait(Equipment.class);
        npc.addTrait(Owner.class);


        // configure traits
        npc.getTrait(Spawned.class).setSpawned(true);
        npc.getTrait(LookClose.class).toggle();
        npc.getTrait(Owner.class).setOwner("raidcraft");
        npc.data().set(NPC.DEFAULT_PROTECTED_METADATA, true);

        // add equipment
//        npc.getTrait(Equipment.class).set(0, new ItemStack(Material.SADDLE));
//        npc.getTrait(Equipment.class).set(1, new ItemStack(Material.LEATHER_HELMET));
//        npc.getTrait(Equipment.class).set(2, new ItemStack(Material.LEATHER_CHESTPLATE));
//        npc.getTrait(Equipment.class).set(3, new ItemStack(Material.LEATHER_LEGGINGS));
//        npc.getTrait(Equipment.class).set(4, new ItemStack(Material.LEATHER_BOOTS));

        npc.spawn(location);
    }
}

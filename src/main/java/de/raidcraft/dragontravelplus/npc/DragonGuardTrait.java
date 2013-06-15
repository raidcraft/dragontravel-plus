package de.raidcraft.dragontravelplus.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.command.CommandContext;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.api.trait.trait.Spawned;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
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
    public void onDespawn() {

        station = StationManager.INST.getNearbyStation(npc.getBukkitEntity().getLocation(), RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.npcStationSearchRadius);
        if (station != null) {
            dragonGuards.remove(station.getName());
        }

        super.onDespawn();
    }

    @Override
    public void onSpawn() {

        super.onSpawn();

        updateDragonGuardNPC();
    }

    @Override
    public void run() {

        super.run();
    }

    public void reloadDragonStation() {

        station = StationManager.INST.getNearbyStation(getNPC().getBukkitEntity().getLocation(), RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.npcStationSearchRadius);
    }

    public void setDragonStation(DragonStation station) {

        this.station = station;
    }

    public DragonStation getDragonStation() {

        return station;
    }

    public void updateDragonGuardNPC() {

        NPC npc = getNPC();
        // change name
        if (!npc.getName().equalsIgnoreCase(RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.npcDefaultName)) {
            npc.setName(RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.npcDefaultName);
        }

        // add equipment
        if (!npc.hasTrait(Equipment.class)) {
            npc.addTrait(Equipment.class);
        }
        npc.getTrait(Equipment.class).set(0, new ItemStack(Material.SADDLE, 1));
        npc.getTrait(Equipment.class).set(1, new ItemStack(Material.LEATHER_HELMET, 1));
        npc.getTrait(Equipment.class).set(2, new ItemStack(Material.LEATHER_CHESTPLATE, 1));
        npc.getTrait(Equipment.class).set(3, new ItemStack(Material.LEATHER_LEGGINGS, 1));
        npc.getTrait(Equipment.class).set(4, new ItemStack(Material.LEATHER_BOOTS, 1));

        //look close
        if (!npc.hasTrait(LookClose.class)) {
            npc.addTrait(LookClose.class);
        }
        npc.getTrait(LookClose.class).lookClose(true);

        // link station
        reloadDragonStation();
        if (station == null) {
            LivingEntity entity = npc.getBukkitEntity();
            RaidCraft.LOGGER.warning("[DTP] No Station found at"
                    + " x:" + entity.getLocation().getBlockX()
                    + " y:" + entity.getLocation().getBlockY()
                    + " z:" + entity.getLocation().getBlockZ()
                    + "!");
            npc.setName("Alter Drachenmeister");
            return;
        }
        else {
            dragonGuards.put(station.getName(), this);
        }
    }

    public static DragonGuardTrait getDragonGuard(String name) {

        for (Map.Entry<String, DragonGuardTrait> entry : dragonGuards.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static void createDragonGuard(Location location, DragonStation station) {

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.npcDefaultName);
        npc.addTrait(DragonGuardTrait.class);
        npc.getTrait(DragonGuardTrait.class).setDragonStation(station);


        // add traits
        npc.addTrait(Spawned.class);
        npc.addTrait(LookClose.class);
        npc.addTrait(Owner.class);
        npc.addTrait(Equipment.class);

        // configure traits
        npc.getTrait(Spawned.class).setSpawned(true);
        npc.getTrait(Owner.class).setOwner("raidcraft");
        npc.data().set(NPC.DEFAULT_PROTECTED_METADATA, true);

        npc.spawn(location);

        RaidCraft.getComponent(DragonTravelPlusPlugin.class).citizens.storeNPCs(new CommandContext(new String[]{}));
    }
}

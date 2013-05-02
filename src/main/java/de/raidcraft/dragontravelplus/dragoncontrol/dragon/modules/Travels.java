package de.raidcraft.dragontravelplus.dragoncontrol.dragon.modules;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import net.minecraft.server.v1_5_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class Travels {

    /**
     * Spawns a RCDragon and mounts the player on it
     *
     * @param player
     */
    public static boolean mountDragon(Player player) {

        if (!DragonManager.INST.flyingPlayers.containsKey(player)) {
            return false;
        }

        RCDragon dragon;
        FlyingPlayer flyingPlayer = DragonManager.INST.flyingPlayers.get(player);
        // Removing dragon if already mounted
        if (flyingPlayer.getDragon() != null) {
            dragon = flyingPlayer.getDragon();
            Entity dra = dragon.getBukkitEntity();
            removePlayerAndDragon(flyingPlayer);
        }

        // log
        RaidCraft.LOGGER.info("[DTP] Spawn RCDragon for player: " + player.getName());

        // Spawning RCDragon
        World notchWorld = ((CraftWorld) player.getWorld()).getHandle();
        dragon = new RCDragon(player.getLocation(), notchWorld);
        notchWorld.addEntity(dragon, SpawnReason.CUSTOM);
        LivingEntity dragonEntity = (LivingEntity) dragon.getBukkitEntity();

        // Set the player as passenger to the RCDragon
        dragonEntity.setPassenger(player);
        // Adding RCDragon and Player to static hashmaps
        flyingPlayer.setDragon(dragon);
        return true;
    }

    /**
     * Removes the player of the HashMap "TravelInformation" and removes the
     * dragon out of the world, also dismounts the player.
     *
     */
    public static void removePlayerAndDragon(FlyingPlayer flyingPlayer) {

        if(flyingPlayer.getDragon() == null) {
            return;
        }

        Entity entity = flyingPlayer.getDragon().getBukkitEntity();

        // Getting player
        Player player = null;
        if (entity.getPassenger() instanceof Player) {
            player = (Player) entity.getPassenger();
        }

        if (player != null) {
            // Teleport player to safe location
            Location clone = player.getLocation().clone();
            int offset = 1;

            for (; ; ) {

                while (clone.getBlock().isEmpty() && clone.getY() != 0) {
                    clone.setY(clone.getY() - offset);
                }

                if (clone.getY() != 0)
                    break;

                clone.setY(256);
            }

            clone.setY(clone.getY() + 2);
            player.teleport(clone);

            clone.setY(clone.getY() + 2);
            player.teleport(clone);
        }

        // Remove dragon from world
        entity.eject();
        entity.remove();
        flyingPlayer.getDragon().cancelTasks();
        flyingPlayer.setDragon(null);
//        flyingPlayer.setInAir(false);
    }

    /**
     * Removes all enderdragons in the same world as the command executor, which
     * do not have players as passengers
     */
    public static void removeDragons(Player player) {

        int passed = 0;

        for (Entity entity : player.getWorld().getEntities()) {

            // Check if EnderDragon
            if (!(entity instanceof EnderDragon))
                continue;

            // Check if EnderDragon has a player as passenger
            if (entity.getPassenger() instanceof Player)
                continue;

            // Remove entity/dragon
            entity.remove();
            passed++;
        }
    }

    /**
     * Removes all enderdragons in a given world, which do not have players as
     * passengers. Reports back to log
     */
    public static void removeDragons(org.bukkit.World world) {

        if (world == null)
            return;

        int passed = 0;

        for (Entity entity : world.getEntities()) {

            // Check if EnderDragon
            if (!(entity instanceof EnderDragon))
                continue;

            // Check if EnderDragon has a player as passenger
            if (entity.getPassenger() instanceof Player)
                continue;

            // Remove entity/dragon
            entity.remove();
            passed++;
        }
    }

    /**
     * Travels the player to the passed chordinates typed in on the command.
     * Also charges player if "Economy" is set to true.
     *
     * @param player entity which is mounted on the dragon, fyling to the
     *               coordinates.
     * @param x      x coordinate
     * @param y      y coordinate
     * @param z      z coordinates
     */
    public static void travelChord(Player player, double x, double y, double z) {

        mountDragon(player);

        FlyingPlayer flyingPlayer = DragonManager.INST.flyingPlayers.get(player);
        RCDragon dragon = DragonManager.INST.flyingPlayers.get(player).getDragon();
        if (dragon == null)
            return;

        Location location = new Location(player.getWorld(), x, y, z);

        dragon.startTravel(flyingPlayer, location);
    }
}

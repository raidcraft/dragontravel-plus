package de.raidcraft.dragontravelplus.dragoncontrol;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Author: Philip
 * Date: 15.12.12 - 19:05
 * Description:
 */
public class FlyingPlayer {

    private static final int ENTITY_NAME_INVISIBLE = 0x0;

    private final Player player;
    private final Location start;
    private RCDragon dragon = null;
    private Location destination;
    private boolean inAir = false;
    private long startTime = 0;
    private double price = 0;
    private BukkitTask waitingTask;
    private BukkitTask checkerTask;

    public FlyingPlayer(Player player, Location start, Location destination, double price) {

        this(player, start);
        this.destination = destination;
        this.price = price;
    }

    public FlyingPlayer(Player player, Location start) {

        this.player = player;
        this.start = start;
/*        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                RaidCraft.getComponent(DragonTravelPlusPlugin.class),
                ConnectionSide.SERVER_SIDE,
                Packets.Server.ENTITY_METADATA
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {

                Packet28EntityMetadata packet = new Packet28EntityMetadata(event.getPacket());
                Entity entity = packet.getEntity(event);

                if (getDragon() != null && getDragon().getBukkitEntity().equals(entity)) {

                    // Clone and update it
                    packet = new Packet28EntityMetadata(packet.getHandle().deepClone());
                    WrappedDataWatcher watcher = new WrappedDataWatcher(packet.getEntityMetadata());
                    watcher.setObject(6, (byte) ENTITY_NAME_INVISIBLE);

                    event.setPacket(packet.getHandle());
                }
            }
        });*/
    }

    public boolean hasIncorrectState() {

        return inAir
                && !player.isInsideVehicle()
                || inAir
                && (System.currentTimeMillis() - startTime) > RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().flightTimeout * 1000 * 60;

    }

    public Player getPlayer() {

        return player;
    }

    public RCDragon getDragon() {

        return dragon;
    }

    public void setDragon(RCDragon dragon) {

        this.dragon = dragon;
    }

    public Location getStart() {

        return start;
    }

    public Location getDestination() {

        return destination;
    }

    public boolean isInAir() {

        return inAir;
    }

    public void setInAir(boolean inAir) {

        this.inAir = inAir;
    }

    public void setWaitingTaskID(BukkitTask waitingTask) {

        this.waitingTask = waitingTask;
    }

    public BukkitTask getWaitingTaskID() {

        return waitingTask;
    }

    public void cancelWaitingTask() {

        if(this.waitingTask != null) {
            this.waitingTask.cancel();
        }
    }

    public void setStartTime(long startTime) {

        this.startTime = startTime;
    }

    public long getStartTime() {

        return startTime;
    }

    public double getPrice() {

        return price;
    }

    public BukkitTask getCheckerTask() {

        return checkerTask;
    }

    public void setCheckerTask(BukkitTask checkerTask) {

        cancelCheckerTask();
        this.checkerTask = checkerTask;
    }

    public void cancelCheckerTask() {

        if(this.checkerTask != null) {
            this.checkerTask.cancel();
        }
    }
}

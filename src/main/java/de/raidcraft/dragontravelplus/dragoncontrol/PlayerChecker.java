package de.raidcraft.dragontravelplus.dragoncontrol;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public class PlayerChecker implements Runnable {

    private FlyingPlayer flyingPlayer;

    public PlayerChecker(FlyingPlayer flyingPlayer) {

        this.flyingPlayer = flyingPlayer;
    }

    @Override
    public void run() {

        if(!flyingPlayer.isInAir()) return;

        Entity entity = flyingPlayer.getDragon().getBukkitEntity();
        if(entity.getPassenger() == null || !(entity.getPassenger() instanceof Player)) {

            entity.eject();
            entity.setPassenger(flyingPlayer.getPlayer());
        }
    }
}

package de.raidcraft.dragontravelplus.dragoncontrol.dragon.modules;

import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;

/**
 * @author Philip Urban
 */
public class SaveLandPlayerTask implements Runnable {

    private FlyingPlayer flyingPlayer;

    public SaveLandPlayerTask(FlyingPlayer flyingPlayer) {

        this.flyingPlayer = flyingPlayer;
    }

    @Override
    public void run() {

        flyingPlayer.setInAir(false);
    }
}
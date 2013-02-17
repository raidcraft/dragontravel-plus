package de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement;

/**
 * @author Philip
 */
public class ControlledFlight {
    private int duration;
    private int maxHeight;

    public ControlledFlight(int duration, int maxHeight) {

        this.duration = duration;
        this.maxHeight = maxHeight;
    }

    public int getDuration() {

        return duration;
    }

    public int getMaxHeight() {

        return maxHeight;
    }
}

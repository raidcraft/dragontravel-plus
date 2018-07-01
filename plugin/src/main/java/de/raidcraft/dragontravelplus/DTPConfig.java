package de.raidcraft.dragontravelplus;

import de.raidcraft.api.config.Comment;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.MultiComment;
import de.raidcraft.api.config.Setting;

/**
 * @author Dragonfire
 */
public class DTPConfig extends ConfigurationBase<DragonTravelPlusPlugin> {

    @Setting("migrate")
    public boolean migrate = true;
    @Setting("disabled")
    public boolean disabled = false;
    @Setting("aircraft.type")
    public String aircraftType = "VANILLA";
    @Setting("error-prevention-flight-timeout")
    public int flightTimeout = 30;
    @Setting("flight-cost-per-block")
    public double pricePerBlock = 0.1;
    @Setting("flight-warmup-time")
    public int flightWarmup = 1;
    @Setting("controlled-flight-speed")
    public double controlledFlightSpeed = 0.3;
    @Setting("dynamic-flight-speed")
    public double dynamicFlightSpeed = 0.7;
    @Setting("controlled-target-distance")
    public int controlledTargetDistance = 30;
    @Setting("dynamic-flight-route")
    public boolean useDynamicRouting = true;

    // waypoint settings
    @Setting("flight.speed")
    @Comment("buggy - flight speed of the dragon")
    public double flightSpeed = 0.3;
    @Setting("flight.waypoint-radius")
    @Comment("fly to new waypoint if in this radius")
    public int waypointRadius = 3;
    @Setting("flight.min-ground-distance")
    @Comment("min distance between dragon and ground")
    public int fligthMinGroundDistance = 3;
    @Setting("flight.max-ground-distance")
    @Comment("max distance between dragon and ground")
    public int fligthMaxGroundDistance = 32;
    @Setting("flight.dragon-falling")
    @MultiComment({"Dragon lose height on flights",
            "to avoid circle simulate it"})
    public int flightDragonFalling = 0;
    @Setting("flight.flight-task-interval")
    @Comment("ticks between is at next waypoint check")
    public int flightTaskInterval = 5;
    @Setting("flight.teleport-fallback")
    public boolean flightTeleportFallback = false;
    @Setting("flight.waypoint-distance")
    @Comment("distance between waypoints")
    public int wayPointDistance = 32;
    @Setting("flight.max-y-delta-before-lift-up")
    @Comment("if the y delta is heighter then this dragon lift up")
    public double flightLiftUpDelta = 16;
    @Setting("flight.max-y-delta-before-lift-down")
    @Comment("if the y delta is lower then this dragon lift down")
    public double flightLiftDownDelta = 16;

    @Setting("flight.gap-iteration")
    @MultiComment({"How often the post process try to find 'H_H' pattern #1313",
            "1: finds 'H__H', 2: finds 'H___H'"})
    public int flightGapIteration = 1;
    @Setting("flight.use-citizens-pathfinding")
    public boolean useCitizensPathFinding = true;

    @Setting("flight.vanilla.speed-x")
    @Comment("move speed per tick")
    public double speedX = 0.8;
    @Setting("flight.vanilla.speed-y")
    public double speedY = 0.6;
    @Setting("flight.vanilla.speed-z")
    public double speedZ = 0.8;
    @Setting("flight.vanilla.player-pitch")
    @Comment("down: -90; up: 90; normal: 0")
    public float playerPitch = 30;

    @Setting("flight-editor-item")
    public int flightEditorItem = 122;
    @Setting("forbidden-commands")
    public String[] forbiddenCommands = new String[]{
            "spawn",
            "home",
            "cast",
            "town spawn",
            "tutorial",
            "tp"
    };

    @Setting("npc-search-radius")
    public int npcStationSearchRadius = 3;
    @Setting("npc-displayName")
    public String npcDefaultName = "Drachenmeister";
    @Setting("npc-conversation-auto-exit-distance")
    public int autoExitDistance = 10;
    @Setting("npc-conversation-max-stations-per-page")
    public int maxStationPerPage = 5;
    @Setting("npc-conversation-chat-delimiter")
    public String chatDelimiter = " ";
    @Setting("npc-conversation-exit-words")
    public String[] exitWords = new String[]{
            "exit",
            "ende",
            "beenden",
            "stop"
    };

    @Setting("conversation-displayName")
    public String conversationName = "drachenmeister";

    public DTPConfig(DragonTravelPlusPlugin plugin) {

        super(plugin, "config.yml");
    }
}

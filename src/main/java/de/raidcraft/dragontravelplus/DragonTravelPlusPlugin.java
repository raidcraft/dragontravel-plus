package de.raidcraft.dragontravelplus;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.dragontravelplus.commands.DTPCommands;
import de.raidcraft.dragontravelplus.commands.FlightCommands;
import de.raidcraft.dragontravelplus.conversations.CheckPlayerAction;
import de.raidcraft.dragontravelplus.conversations.FindDragonstationAction;
import de.raidcraft.dragontravelplus.conversations.FlyControlledAction;
import de.raidcraft.dragontravelplus.conversations.FlyFlightAction;
import de.raidcraft.dragontravelplus.conversations.FlyToStationAction;
import de.raidcraft.dragontravelplus.conversations.ListStationsAction;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import de.raidcraft.dragontravelplus.flight.FlightEditorListener;
import de.raidcraft.dragontravelplus.listener.ChunkListener;
import de.raidcraft.dragontravelplus.listener.DragonListener;
import de.raidcraft.dragontravelplus.listener.PlayerListener;
import de.raidcraft.dragontravelplus.station.StationManager;
import de.raidcraft.dragontravelplus.tables.FlightWayPointsTable;
import de.raidcraft.dragontravelplus.tables.PlayerStationsTable;
import de.raidcraft.dragontravelplus.tables.StationTable;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import de.raidcraft.rcconversations.actions.ActionManager;
import net.minecraft.server.v1_7_R1.EntityTypes;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Author: Philip
 * Date: 22.11.12 - 06:01
 * Description:
 */
public class DragonTravelPlusPlugin extends BasePlugin {

    private LocalDTPConfiguration config;

    @Override
    public void enable() {

        if (!registerEntity("RCDragon", 63, RCDragon.class)) {
            getLogger().severe("Failed to register RCDragon entity! DISABLING PLUGIN");
            disable();
            return;
        }

        loadConfig();

        registerEvents(new PlayerListener());
        registerEvents(new ChunkListener());
        registerEvents(new DragonListener());
        registerEvents(new FlightEditorListener());

        registerCommands(DTPCommands.class);
        registerCommands(FlightCommands.class);

        registerTable(StationTable.class, new StationTable());
        registerTable(PlayerStationsTable.class, new PlayerStationsTable());
        registerTable(FlightWayPointsTable.class, new FlightWayPointsTable());

        StationManager.INST.loadExistingStations();

        // lets trigger a delayed load to make sure all plugins are loaded
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {

                try {
                    ActionManager.registerAction(new FlyFlightAction());
                    ActionManager.registerAction(new FlyControlledAction());
                    ActionManager.registerAction(new FlyToStationAction());
                    ActionManager.registerAction(new ListStationsAction());
                    ActionManager.registerAction(new FindDragonstationAction());
                    ActionManager.registerAction(new CheckPlayerAction());
                } catch (Exception e) {
                    RaidCraft.LOGGER.warning("[DTP] Can't load Actions! RCConversations not found!");
                }
            }
        }, 1L);
    }

    @Override
    public void disable() {
        // remove all dragons in the world
        for (FlyingPlayer flyingPlayer : DragonManager.INST.getFlyingPlayers()) {
            if (flyingPlayer.isInAir()) {
                DragonManager.INST.abortFlight(flyingPlayer.getPlayer());
            }
        }
    }

    public void loadConfig() {

        config = configure(new LocalDTPConfiguration(this));
    }

    @Override
    public void reload() {

        config.reload();
        // remove all dragons in the world
        for (FlyingPlayer flyingPlayer : DragonManager.INST.getFlyingPlayers()) {
            if (flyingPlayer.isInAir()) {
                DragonManager.INST.abortFlight(flyingPlayer.getPlayer());
                ChatMessages.warn(flyingPlayer.getPlayer(), "Der Flug musste aus technischen Gruenden abgebrochen werden!");
            }
        }

        DragonManager.INST.clearFlyingPlayers();
        StationManager.INST.loadExistingStations();
    }

    @SuppressWarnings("unchecked")
    private boolean registerEntity(String name, int id, Class<?> dragonClass) {

        try {
            Class entityTypeClass = EntityTypes.class;

            Field c = entityTypeClass.getDeclaredField("c");
            c.setAccessible(true);
            HashMap c_map = (HashMap)c.get(null);
            c_map.put(name, dragonClass);

            Field d = entityTypeClass.getDeclaredField("d");
            d.setAccessible(true);
            HashMap d_map = (HashMap)d.get(null);
            d_map.put(dragonClass, name);

            Field e = entityTypeClass.getDeclaredField("e");
            e.setAccessible(true);
            HashMap e_map = (HashMap)e.get(null);
            e_map.put(id, dragonClass);

            Field f = entityTypeClass.getDeclaredField("f");
            f.setAccessible(true);
            HashMap f_map = (HashMap)f.get(null);
            f_map.put(dragonClass, id);

            Field g = entityTypeClass.getDeclaredField("g");
            g.setAccessible(true);
            HashMap g_map = (HashMap)g.get(null);
            g_map.put(name, id);

            return true;
        }
        catch (Exception e) {

            Class<?>[] paramTypes = new Class[] { Class.class, String.class, int.class };

            // MCPC+ compatibility
            // Forge Dev environment; names are not translated into func_foo
            try {
                Method method = EntityTypes.class.getDeclaredMethod("addMapping", paramTypes);
                method.setAccessible(true);
                method.invoke(null, dragonClass, name, id);
                return true;
            }
            catch (Exception ex) {
                e.addSuppressed(ex);
            }
            // Production environment: search for the method
            // This is required because the seargenames could change
            // LAST CHECKED FOR VERSION 1.6.4
            try {
                for (Method method : EntityTypes.class.getDeclaredMethods()) {
                    if (Arrays.equals(paramTypes, method.getParameterTypes())) {
                        method.invoke(null, dragonClass, name, id);
                        return true;
                    }
                }
            }
            catch (Exception ex) {
                e.addSuppressed(ex);
            }

            getLogger().severe("Could not register the " + name + "[" + dragonClass + "] dragon entity!");
            e.printStackTrace();
        }
        return false;
    }


    public LocalDTPConfiguration getConfig() {

        return config;
    }

    public class LocalDTPConfiguration extends ConfigurationBase<DragonTravelPlusPlugin> {

        @Setting("disabled")
        public boolean disabled = false;
        @Setting("error-prevention-flight-timeout")
        public int flightTimeout = 30;
        @Setting("flight-cost-per-block")
        public double pricePerBlock = 0.1;
        @Setting("flight-warmup-time")
        public int flightWarmup = 1;
        @Setting("flight-height")
        public int flightHeight = 15;
        @Setting("flight-speed")
        public double flightSpeed = 0.3;
        @Setting("controlled-flight-speed")
        public double controlledFlightSpeed = 0.3;
        @Setting("dynamic-flight-speed")
        public double dynamicFlightSpeed = 0.7;
        @Setting("controlled-target-distance")
        public int controlledTargetDistance = 30;
        @Setting("dynamic-flight-route")
        public boolean useDynamicRouting = true;
        @Setting("flight-waypoint-distance")
        public int wayPointDistance = 10;
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
        @Setting("npc-name")
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

        @Setting("conversation-name")
        public String conversationName = "drachenmeister";

        public LocalDTPConfiguration(DragonTravelPlusPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}

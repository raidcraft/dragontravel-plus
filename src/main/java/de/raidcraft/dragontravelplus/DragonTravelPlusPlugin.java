package de.raidcraft.dragontravelplus;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.dragontravelplus.commands.DTPCommands;
import de.raidcraft.dragontravelplus.commands.FlightCommands;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import de.raidcraft.dragontravelplus.flight.FlightEditorListener;
import de.raidcraft.dragontravelplus.listener.DragonListener;
import de.raidcraft.dragontravelplus.listener.PlayerListener;
import de.raidcraft.dragontravelplus.npc.DragonGuardTrait;
import de.raidcraft.dragontravelplus.npc.NPCListener;
import de.raidcraft.dragontravelplus.npc.conversation.Conversation;
import de.raidcraft.dragontravelplus.rcconversations.FlyControlledAction;
import de.raidcraft.dragontravelplus.rcconversations.FlyFlightAction;
import de.raidcraft.dragontravelplus.station.StationManager;
import de.raidcraft.dragontravelplus.tables.FlightWayPointsTable;
import de.raidcraft.dragontravelplus.tables.PlayerStations;
import de.raidcraft.dragontravelplus.tables.StationTable;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import de.raidcraft.rcconversations.actions.ActionManager;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.minecraft.server.v1_5_R1.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Author: Philip
 * Date: 22.11.12 - 06:01
 * Description:
 */
public class DragonTravelPlusPlugin extends BasePlugin {

    public Citizens citizens;
    public LocalDTPConfiguration config;

    @Override
    public void enable() {

        // Add our new entity to minecraft entities
        try {
            Method method = EntityTypes.class.getDeclaredMethod("a", new Class[]{Class.class, String.class, int.class});
            method.setAccessible(true);
            method.invoke(EntityTypes.class, RCDragon.class, "RCDragon", 63);
            getLogger().warning("[DragonTravelPlus] Successfully registered RCDragon entity!");
        } catch (Exception e) {
            getLogger().warning("[DragonTravelPlus] Error registering Entity! DISABLING!");
            disable();
            return;
        }

        loadConfig();

        registerEvents(new PlayerListener());
        registerEvents(new DragonListener());
        registerEvents(new FlightEditorListener());

        registerCommands(DTPCommands.class);
        registerCommands(FlightCommands.class);

        registerTable(StationTable.class, new StationTable());
        registerTable(PlayerStations.class, new PlayerStations());
        registerTable(FlightWayPointsTable.class, new FlightWayPointsTable());

        StationManager.INST.loadExistingStations();

        try {
            registerEvents(new NPCListener());
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(DragonGuardTrait.class).withName("dragonguard"));
            citizens = (Citizens)Bukkit.getPluginManager().getPlugin("Citizens");
        } catch (Exception e) {
            RaidCraft.LOGGER.warning("[DTP] Can't load NPC stuff! Citizens not found!");
        }

        try {
            ActionManager.registerAction(new FlyFlightAction("DTP_FLIGHT"));
            ActionManager.registerAction(new FlyControlledAction("DTP_CONTROLLED"));
        } catch (Exception e) {
            RaidCraft.LOGGER.warning("[DTP] Can't load Actions! RCConversations not found!");
        }
    }

    @Override
    public void disable() {
        // remove all dragons in the world
        for (Map.Entry<Player, FlyingPlayer> entry : DragonManager.INST.flyingPlayers.entrySet()) {
            if (entry.getValue().isInAir()) {
                DragonManager.INST.abortFlight(entry.getKey());
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
        for (Map.Entry<Player, FlyingPlayer> entry : DragonManager.INST.flyingPlayers.entrySet()) {
            if (entry.getValue().isInAir()) {
                DragonManager.INST.abortFlight(entry.getKey());
                ChatMessages.warn(entry.getKey(), "Der Flug musste aus technischen Gruenden abgebrochen werden!");
            }
        }

        // end all conversations
        Conversation.conversations.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Conversation.conversations.put(player.getName(), new Conversation(player));
        }

        DragonManager.INST.flyingPlayers.clear();
        StationManager.INST.loadExistingStations();
        // reload assigned stations
        for (Map.Entry<String, DragonGuardTrait> entry : DragonGuardTrait.dragonGuards.entrySet()) {
            // check if npc still exists
            if (entry.getValue().getNPC() != null && entry.getValue().getNPC().getBukkitEntity() != null) {
                entry.getValue().reloadDragonStation();
            }
        }
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
        @Setting("use-visible-wapoint-marker")
        public boolean useVisibleWaypoints = false;
        @Setting("visible-marker-duration")
        public int markerDuration = 30;
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

        
        /*  NPC CONVERSATION */

        @Setting("conv-wrong-answer-warning")
        public String[] convWrongAnswerWarning = new String[]{
                "Ich habe deine Antwort nicht verstanden!"
        };

        @Setting("conv-stage-disabled-speak")
        public String[] convDisabledSpeak = new String[]{
                "Ich habe meinen Drachen schon seit Tagen nichtmehr gesehen.",
                "Schaue spaeter nochmal vorbei vielleicht kann ich dir dann weiterhelfen!"
        };

        @Setting("conv-no-stations-discovered")
        public String[] convNoStationsDiscovered = new String[]{
                "Du musst erst einmal andere Drachenmeister kennen lernen!",
                "Geh in die Welt und suche nach ihnen."
        };

        @Setting("conv-stage-first-meet-speak")
        public String[] convFirstMeetSpeak = new String[]{
                "Hallo in '%sn' %pn!",
                "Ich sehe dich hier zum ersten Mal.",
                "Gerne kannst du in Zukunft mit meinem Drachen reisen!"
        };

        @Setting("conv-stage-no-permission-speak")
        public String[] convNoPermissionSpeak = new String[]{
                "Ich rede nicht mit leuten die ich nicht kenne!",
                "Geh und stelle dich zuerst in der Stadt vor!"
        };

        @Setting("conv-stage-select-dial-mode-speak")
        public String[] convSelectDialModeSpeak = new String[]{
                "Hallo in '%sn' %pn!",
                "Du moechtest bestimmt mit meinem Drachen reisen!",
                "Kann ich dir bei der Zielwahl helfen?"
        };

        @Setting("conv-stage-select-dial-mode-answers")
        public String[] convSelectDialModeAnswers = new String[]{
                "Ja gerne!",
                "Nein ich kenne den Name meines Ziels!"

        };

        @Setting("conv-stage-flight-by-name-speak")
        public String[] convFlightByNameSpeak = new String[]{
                "Ok dann nenne mir den Namen:"
        };

        @Setting("conv-stage-flight-by-name-unknown-station")
        public String[] convFlightByNameUnknownStation = new String[]{
                "Du kennst in dieser Welt eine solche Drachenstation nicht!"
        };

        @Setting("conv-stage-flight-by-name-same-station")
        public String[] convFlightByNameSameStation = new String[]{
                "Du bist bereits bei deiner gewaehlten Station!"
        };

        @Setting("conv-stage-target-assist-region-speak")
        public String[] convTargetAssistRegionSpeak = new String[]{
                "In welche Himmelrichtung willst du reisen?"
        };

        @Setting("conv-stage-target-assist-list-speak")
        public String[] convTargetAssistListSpeak = new String[]{
                "Folgende Ziele gibt es dort:"
        };

        @Setting("conv-stage-target-assist-list-tell-more")
        public String convTargetAssistListTellMore =
                "Nenn mir weitere!";

        @Setting("conv-stage-target-assist-list-go-back")
        public String convTargetAssistListGoBack =
                "Ich moechte doch eine andere Himmelsrichtung waehlen!";

        @Setting("conv-stage-process-economy-back")
        public String convProcessEconomyGoBack =
                "Ich moechte ein anderes Ziel waehlen.";

        @Setting("conv-stage-process-economy-exit")
        public String convProcessEconomyExit =
                "Schade, dann nicht.";

        @Setting("conv-stage-process-economy-confirm")
        public String convProcessEconomyConfirm =
                "Ja, ist in Ordnung!";

        @Setting("conv-stage-process-economy-broke")
        public String[] convProcessEconomyBroke = new String[]{
                "Du hast leider nicht genug Geld fuer diesen Flug!"
        };

        @Setting("conv-stage-process-economy-confirm-question")
        public String[] convProcessEconomyConfirmQuestion = new String[]{
                "Die Reise nach %sn kostet dich $%fp (%fd)."
        };

        @Setting("conv-stage-process-economy-goodbye")
        public String convProcessEconomyGoodbye =
                "Tschuess, bis zum naechsten mal!";

        @Setting("conv-stage-process-economy-takeoff")
        public String[] convProcessEconomyTakeoff = new String[]{
                "Mein Drache wird in Kuerze mit dir nach %sn fliegen!"
        };

        public LocalDTPConfiguration(DragonTravelPlusPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}

package de.raidcraft.dragontravelplus;

import com.silthus.raidcraft.util.component.database.ComponentDatabase;
import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;
import de.raidcraft.dragontravelplus.commands.Commands;
import de.raidcraft.dragontravelplus.npc.DragonGuardTrait;
import de.raidcraft.dragontravelplus.npc.NPCListener;
import de.raidcraft.dragontravelplus.station.StationManager;
import de.raidcraft.dragontravelplus.tables.PlayerStations;
import de.raidcraft.dragontravelplus.tables.StationTable;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;

/**
 * Author: Philip
 * Date: 22.11.12 - 06:01
 * Description:
 */
@ComponentInformation(
        friendlyName = "Dragon Travel Plus",
        desc = "Sends the dragons into the air."
)
public class DragonTravelPlusModule extends BukkitComponent {

    public static DragonTravelPlusModule inst;
    public LocalDTPConfiguration config;
    private int startTaskId;

    @Override
    public void enable() {
        inst = this;
        loadConfig();
        startTaskId = CommandBook.inst().getServer().getScheduler().scheduleSyncRepeatingTask(CommandBook.inst(), new Runnable() {
            public void run() {
                if(ComponentDatabase.INSTANCE.getConnection() != null) {

                    CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(DragonGuardTrait.class).withName("dragonguard"));
                    CommandBook.registerEvents(new NPCListener());
                    registerCommands(Commands.class);
                    ComponentDatabase.INSTANCE.registerTable(StationTable.class, new StationTable());
                    ComponentDatabase.INSTANCE.registerTable(PlayerStations.class, new PlayerStations());
                    StationManager.INST.loadExistingStations();
                    CommandBook.server().getScheduler().cancelTask(startTaskId);

                    CommandBook.logger().info("[DragonTravelPlus] Found DB connection, init DTPlus module...");
                }
            }
        }, 0, 2*20);
    }

    public void loadConfig() {

        config = configure(new LocalDTPConfiguration());
    }

    public class LocalDTPConfiguration extends ConfigurationBase {

        @Setting("disabled") public boolean disabled = true;
        @Setting("npc-search-radius") public int npcStationSearchRadius = 10;
        @Setting("npc-name") public String npcDefaultName = "Drachenmeister";
        @Setting("npc-conversation-auto-exit-distance") public int autoExitDistance = 10;
        @Setting("npc-conversation-max-stations-per-page") public int maxStationPerPage = 5;
        @Setting("npc-conversation-exit-words") public String[] exitWords = new String[] {
                "exit",
                "ende",
                "beenden",
                "stop"
        };

        
        /*  NPC CONVERSATION */
        
        @Setting("conv-wrong-answer-warning") public String[] convWrongAnswerWarning = new String[] {
                "Ich habe deine Antwort nicht verstanden!"
        };
        
        @Setting("conv-stage-disabled-speak") public String[] convDisabledSpeak = new String[] {
                "Ich habe meinen Drachen schon seit Tagen nichtmehr gesehen.",
                "Schaue später nochmal vorbei vielleicht kann ich dir dann weiterhelfen!"
        };

        @Setting("conv-no-stations-discovered") public String[] convNoStationsDiscovered = new String[] {
                "Du musst erst einmal andere Drachenmeister kennen lernen!",
                "Geh in die Welt und suche nach ihnen."
        };

        @Setting("conv-stage-first-meet-speak") public String[] convFirstMeetSpeak = new String[] {
                    "Hallo %s, ich sehe dich hier zum ersten Mal!",
                    "Gerne kannst du in Zukunft mit meinem Drachen reisen!"
        };

        @Setting("conv-stage-no-permission-speak") public String[] convNoPermissionSpeak = new String[]{
                "Ich rede nicht mit leuten die ich nicht kenne!",
                "Geh und stelle dich zuerst in der Stadt vor!"
        };

        @Setting("conv-stage-select-dial-mode-speak") public String[] convSelectDialModeSpeak = new String[]{
                "Hallo %s!",
                "Du möchtest bestimmt mit meinem Drachen auf Reise gehen!",
                "Kann ich dir bei der Zielwahl helfen?"
        };

        @Setting("conv-stage-select-dial-mode-answers") public String[] convSelectDialModeAnswers = new String[]{
                "Ja gerne!",
                "Nein ich kenne den Name meines Ziels!"

        };

        @Setting("conv-stage-flight-by-name-speak") public String[] convFlightByNameSpeak = new String[]{
                "Ok dann nenne mir den Namen:"
        };

        @Setting("conv-stage-flight-by-name-unknown-station") public String[] convFlightByNameUnknownStation = new String[]{
                "Du hast noch keine Drachenstation mit diesem Namen entdeckt!"
        };

        @Setting("conv-stage-flight-by-name-same-station") public String[] convFlightByNameSameStation = new String[]{
                "Du bist bereits bei deiner gewählten Station!"
        };

        @Setting("conv-stage-flight-by-name-takeoff") public String[] convFlightByNameTakeoff = new String[]{
                "Ok mein Drache wird in Kürze mit dir nach %ds fliegen!"
        };

        @Setting("conv-stage-target-assist-region-speak") public String[] convTargetAssistRegionSpeak = new String[]{
                "In welche Himmelrichtung willst du reisen?"
        };

        @Setting("conv-stage-target-assist-list-speak") public String[] convTargetAssistListSpeak = new String[]{
                "Folgende Ziele gibt es dort:"
        };

        @Setting("conv-stage-target-assist-list-tell-more") public String convTargetAssistListTellMore =
                "Nenn mir weitere!";

        @Setting("conv-stage-target-assist-list-go-back") public String convTargetAssistListGoBack =
                "Ich möchte doch eine andere Himmelsrichtung wählen!";

        @Setting("conv-stage-target-assist-takeoff") public String[] convTargetAssistListTakeoff = new String[]{
                "Ok mein Drache wird in Kürze mit dir nach %ds fliegen!"
        };
    }
}

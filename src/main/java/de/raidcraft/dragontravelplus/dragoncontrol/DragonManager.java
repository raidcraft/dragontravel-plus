package de.raidcraft.dragontravelplus.dragoncontrol;

import de.raidcraft.dragontravelplus.dragoncontrol.dragon.modules.Travels;
import de.raidcraft.dragontravelplus.navigator.Navigator;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Author: Philip
 * Date: 02.12.12 - 21:27
 * Description:
 */
public class DragonManager {

    private Map<String, FlyingPlayer> flyingPlayers = new CaseInsensitiveMap<>();

    public final static DragonManager INST = new DragonManager();

    public void takeoff(Player player, DragonStation start, DragonStation destination, double price) {

        Navigator navigator = new Navigator(player, start, destination, price);
        navigator.startFlight();
    }

    public void abortFlight(Player player) {

        if (!flyingPlayers.containsKey(player.getName())) {
            return;
        }

        FlyingPlayer flyingPlayer = flyingPlayers.get(player.getName());


        if (flyingPlayer.isInAir()) {
            Travels.removePlayerAndDragon(flyingPlayer);
        } else {
            flyingPlayer.cancelWaitingTask();
        }
        player.teleport(flyingPlayer.getStart());   // teleport to start
        DragonManager.INST.flyingPlayers.remove(player.getName());
    }

    public FlyingPlayer getFlyingPlayer(String name) {

        for (Map.Entry<String, FlyingPlayer> entry : DragonManager.INST.flyingPlayers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public Set<FlyingPlayer> getFlyingPlayers() {

        return new HashSet<>(flyingPlayers.values());
    }

    public void setFlyingPlayer(FlyingPlayer flyingPlayer) {

        flyingPlayers.put(flyingPlayer.getPlayer().getName(), flyingPlayer);
    }

    public void clearFlyingPlayers() {

        flyingPlayers.clear();
    }
}

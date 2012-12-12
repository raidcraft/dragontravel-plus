package de.raidcraft.dragontravelplus.util;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.dragontravelplus.station.DragonStation;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

/**
 * Author: Philip
 * Date: 12.12.12 - 22:16
 * Description:
 */
public class DynmapComponent {
    public static final DynmapComponent INST = new DynmapComponent();

    private DynmapAPI api;
    private MarkerAPI markerAPI;
    private MarkerSet dragonSet;

    public DynmapComponent() {

        api = (DynmapAPI)CommandBook.server().getPluginManager().getPlugin("dynmap");
        markerAPI = api.getMarkerAPI();
        dragonSet = markerAPI.getMarkerSet("drachenmeister");
    }

    public void addStationMarker(DragonStation station) {
        dragonSet.createMarker(station.getName().toLowerCase().replace(" ", "_")
                , station.getName()
                , station.getLocation().getWorld().getName()
                , station.getLocation().getBlockX()
                , station.getLocation().getBlockY()
                , station.getLocation().getBlockZ()
                , markerAPI.getMarkerIcon("sign")
                , true);
    }
}

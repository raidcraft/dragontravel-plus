package de.raidcraft.dragontravelplus.util;

import de.raidcraft.dragontravelplus.station.DragonStation;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

/**
 * Author: Philip
 * Date: 12.12.12 - 22:16
 * Description:
 */
public class DynmapManager {
    public static final DynmapManager INST = new DynmapManager();

    private DynmapAPI api;
    private MarkerAPI markerAPI = null;
    private MarkerSet dragonSet = null;

    public DynmapManager() {

//        api = (DynmapAPI)CommandBook.server().getPluginManager().getPlugin("dynmap");
//        if(api == null) {
//            return;
//        }
//        markerAPI = api.getMarkerAPI();
//        dragonSet = markerAPI.getMarkerSet("drachenmeister");
    }

    public void addStationMarker(DragonStation station) {
        if(markerAPI == null) {
            return;
        }
        dragonSet.createMarker(station.getName().toLowerCase().replace(" ", "_")
                , station.getName()
                , station.getLocation().getWorld().getName()
                , station.getLocation().getBlockX()
                , station.getLocation().getBlockY()
                , station.getLocation().getBlockZ()
                , markerAPI.getMarkerIcon("sign")
                , true);
    }
    
    public void removeMarker(DragonStation station) {
        if(dragonSet == null) {
            return;
        }
        for(Marker marker : dragonSet.getMarkers()) {
            if(marker.getLabel().equalsIgnoreCase(station.getName())) {
                marker.deleteMarker();
            }
        }
    }
}

package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.dragontravelplus.api.flight.Path;
import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum PathType {

    DYNAMIC(DynamicFlightPath.class),
    DYNAMIC_INTERPOLATED(DynamicInterpolatedFlightPath.class),
    SAVED(SavedFlightPath.class);

    private final Class<? extends Path> pathClass;

    PathType(Class<? extends Path> pathClass) {

        this.pathClass = pathClass;
    }

    public Class<? extends Path> getPathClass() {

        return pathClass;
    }

    public static PathType fromString(String name) {

        return EnumUtils.getEnumFromString(PathType.class, name);
    }
}

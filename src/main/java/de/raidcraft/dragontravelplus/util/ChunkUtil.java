package de.raidcraft.dragontravelplus.util;

import org.bukkit.Chunk;

/**
 * @author Philip Urban
 */
public class ChunkUtil {

    public static boolean equalsChunk(Chunk c1, Chunk c2) {

        return (c1.getX() == c2.getX() && c1.getZ() == c2.getZ());
    }
}

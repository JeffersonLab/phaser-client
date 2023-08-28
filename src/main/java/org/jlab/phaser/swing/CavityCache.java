package org.jlab.phaser.swing;

import java.util.LinkedHashSet;

/**
 * A global thread-safe repository of cavities.
 * 
 * @author ryans
 */
public final class CavityCache {
    private static final LinkedHashSet<String> CAVITIES = new LinkedHashSet<>();
    
    private CavityCache() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Returns the list of cavities.
     * 
     * @return The cavities list
     */
    public static synchronized LinkedHashSet<String> getCavities() {
        return new LinkedHashSet<>(CAVITIES);
    }
    
    /**
     * Sets the cavities list.
     * 
     * @param cavities The cavities list
     */
    public static synchronized void setCavities(LinkedHashSet<String> cavities) {
        CavityCache.CAVITIES.clear();
        CavityCache.CAVITIES.addAll(cavities);
    }
}

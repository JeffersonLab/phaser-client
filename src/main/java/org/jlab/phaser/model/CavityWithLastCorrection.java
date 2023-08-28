package org.jlab.phaser.model;

import java.util.Date;
import java.util.Objects;

/**
 * An immutable record for a cavity with last phase correction date.
 *
 * When selecting which cavities to phase, it is often useful to know the last
 * time a cavity had it's phase corrected.
 *
 * @author ryans
 */
public class CavityWithLastCorrection {

    private final String name;
    private final Date lastCorrection;

    /**
     * Create a new CavityWithLastCorrection given the cavity name and last
     * correction date.
     *
     * @param name The cavity name
     * @param lastCorrection The last phase correction date
     */
    public CavityWithLastCorrection(String name, Date lastCorrection) {
        this.name = name;
        this.lastCorrection = lastCorrection;
    }

    /**
     * Return the cavity name.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the last phase correction date.
     *
     * @return The last correction date
     */
    public Date getLastCorrection() {
        return lastCorrection;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CavityWithLastCorrection other = (CavityWithLastCorrection) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
}

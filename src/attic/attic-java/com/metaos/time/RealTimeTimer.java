/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.time;

import java.util.*;
import java.util.logging.Logger;

/**
 * Timer synchronization and orchestration.
 */
public class RealTimeTimer implements Timer {
    private final long resolution;

    /**
     * Creates a real time timer, with time descritized.
     * @param resolution milliseconds of resolution to discretize.
     */
    public RealTimeTimer(final long resolution) {
        this.resolution = resolution;
    }


    /**
     * Gets the current moment.
     */
    public Calendar now() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis((System.currentTimeMillis() / resolution) 
                * resolution);
        return cal;
    }

    
    /**
     * Returns a new date with added clicks of current timer.
     */
    public Calendar addClicks(final long clicks, final Calendar date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(((cal.getTimeInMillis() / resolution) + clicks) 
                * resolution);
        return cal;
    }


    /**
     * Number of <i>resolution</i> blocks of milliseconds between dates.
     * <i>resolution</i> is given at construction time.
     */
    public long clicksOfDifference(final Calendar max, final Calendar min) {
        final long t1 = max.getTimeInMillis();
        final long t0 = min.getTimeInMillis();
        return (t1-t0) / resolution;
    }
}

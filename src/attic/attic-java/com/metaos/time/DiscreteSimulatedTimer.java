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
 * Timer synchronization for simulations.
 */
public class DiscreteSimulatedTimer implements Timer {
    private long currentTime;
    private final long resolution;


    /**
     * Creates a simulated timer with given resolution of milliseconds.
     * @param resolution number of milliseconds of resolution.
     */
    public DiscreteSimulatedTimer(final long resolution) {
        this.resolution = resolution;
    }


    /**
     * Increments clicks in <i>resolution</i> units.
     */
    public void incrementClick() {
        currentTime += resolution;
    }

    /**
     * Sets current simulated time;
     */
    public void setClick(final long currentTime) {
        this.currentTime = currentTime;
    }

    //
    // Interface implementation ------------------------------
    //

    /**
     * Gets the current moment.
     */
    public Calendar now() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(this.currentTime);
        return cal;
    }


    /**
     * Returns a new date with substracted clicks of current timer.
     */
    public Calendar addClicks(final long clicks, final Calendar date) {
        final long millis = date.getTimeInMillis() + clicks*resolution;
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return cal;
    }



    /**
     * Number of clicks (whatever it means) of difference between dates
     * taken into account this timer.
     */
    public long clicksOfDifference(final Calendar max, final Calendar min) {
          final long t1 = max.getTimeInMillis();
          final long t0 = min.getTimeInMillis();
          return (t1-t0) / resolution;
    }
}

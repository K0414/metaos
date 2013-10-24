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
public interface Timer {
    /**
     * Gets the current moment.
     */
    public Calendar now();


    /**
     * Returns a new date with added clicks of current timer.
     */
    public Calendar addClicks(final long clicks, final Calendar date);


    /**
     * Number of clicks (whatever it means) of difference between dates
     * taken into account this timer.
     */
    public long clicksOfDifference(final Calendar max, final Calendar min);
}

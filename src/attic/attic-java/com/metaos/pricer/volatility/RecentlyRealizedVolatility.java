/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.pricer.volatility;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.Calendar;
import java.util.logging.Logger;
import com.metaos.Instrument;
import com.metaos.time.Timer;

import org.apache.commons.math.stat.descriptive.moment.*;

/**
 * Calculates realized volatility over a set of prices. 
 */
public class RecentlyRealizedVolatility implements VolatilityCalculator {
    private static final Logger log = Logger.getLogger(
            RecentlyRealizedVolatility.class.getPackage().getClass().getName());

    private final long clicks;
    private final Timer timer;
    private final Variance variance;
    private final Calendar when;


    /**
     * Creates calculator.
     * @param clicks number of clicks (referenced to timer) to evaluate prices.
     * @param timer timer to consider prices.
     * @param when time of reference (last moment to consider volatility).
     */
    public RecentlyRealizedVolatility(final long clicks, final Timer timer, 
                final Calendar when) {
        this.clicks = clicks;
        this.timer = timer;
        this.when = when;
        this.variance = new Variance();
    }


    /**
     * Gets volatility for given instrument.
     */
    public double calculate(final Instrument instrument) {
        final double[] values = this.getValues(instrument);
        return Math.sqrt(this.variance.evaluate(values));
    }


    /**
     * Gets the list of values used to make calculi.
     */
    public double[] getValues(final Instrument instrument) {
        final double values[] = new double[(int) this.clicks];
        for (int i=0; i<this.clicks; i++) {
            values[i] = instrument.getPrice(
                    this.timer.addClicks(-this.clicks+i, this.when));
        }
        return values;
    }
}

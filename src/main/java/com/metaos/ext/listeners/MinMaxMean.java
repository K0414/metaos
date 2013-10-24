/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.ext.listeners;

import com.metaos.datamgt.*;
import java.util.Calendar;
import java.util.Map;

/**
 * Listener to take note of minimum, maximum and average notified values.
 */
public class MinMaxMean implements Listener {
    private String symbol;
    private Field field;
    private int resolution;
    private final double maxs[], mins[], totals[];
    private final int numOfValues[];

    /**
     * Creates the listener with a time resolution, in minutes.
     *
     * @param resolution minutes of resolution.
     */
    public MinMaxMean(final String symbol, final Field field,
            final int resolution) {
        this.maxs = new double[(60*24)/resolution];
        this.mins = new double[(60*24)/resolution];
        this.totals = new double[(60*24)/resolution];
        this.numOfValues = new int[(60*24)/resolution];
        this.resolution = resolution;
        this.symbol = symbol;
        this.field = field;
    }

    public void notify(final ParseResult result) {
        final Calendar when = result.getLocalTimestamp(this.symbol);
        final int minuteInDay = (when.get(Calendar.HOUR_OF_DAY)*60
                                + when.get(Calendar.MINUTE)) / resolution;
        if(result.values(symbol)==null 
                || result.values(symbol).get(field)==null) {
            return;
        }

        final double val = result.values(symbol).get(field);
        if(Double.isNaN(val)) return;
        if(val>this.maxs[minuteInDay]) this.maxs[minuteInDay] = val;
        if(val<this.mins[minuteInDay] || this.mins[minuteInDay]==0) {
            this.mins[minuteInDay] = val;
        }
        this.totals[minuteInDay] += val;
        this.numOfValues[minuteInDay]++;
    }


    //
    // Extra Methods ----------------------------------------------
    //

    public double[] getAccumulatedMinimum() {
        return this.mins;
    }

    public double[] getAccumulatedMaximum() {
        return this.maxs;
    }

    public double[] getAccumulatedAverage() {
        final double[] avgs = new double[this.totals.length];
        for(int i=0; i<avgs.length; i++) {
            if(this.numOfValues[i]!=0) {
                avgs[i] = this.totals[i] / this.numOfValues[i];
            }
        }
        return avgs;
    }
}

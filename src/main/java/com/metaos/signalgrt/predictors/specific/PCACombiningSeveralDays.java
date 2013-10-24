/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.signalgrt.predictors.specific;

import java.util.*;
import java.util.logging.*;
import com.metaos.signalgrt.predictors.*;
import com.metaos.signalgrt.predictors.simple.*;
import com.metaos.util.*;
import com.metaos.datamgt.*;

/**
 * Uses a PCA as predictor to forecast the next daily values based on the set
 * of values from previous days.
 *
 * TODO: Rename to PCAUsingOneValueSeveralDays
 *
 * <br/>
 * Note to developers: overwrite <code>cleanData</code> method to perform
 * some modifications on data before learning. 
 */
public abstract class PCACombiningSeveralDays extends StaticDayByDayPredictor {
    private static final Logger log = Logger.getLogger(PCACombiningSeveralDays
            .class.getPackage().getName());
    private final String symbol;
    private final CalUtils.InstantGenerator instantGenerator;
    private final double[] vals;
    private boolean hasLearnt = false;
    private Calendar previousWhen = null;
    private int previousInstant = -1;

    //
    // Public methods ---------------------------------------
    //


    /**
     * Creates a day-by-day predictor which uses data from several previous 
     * days to forecast data for next day.
     *
     * @param scale value to scale daily predictions, 
     *      0 or less if no scale is wanted.
     * @param memory number of days to store into memory
     */
    public PCACombiningSeveralDays(
            final CalUtils.InstantGenerator instantGenerator,
            final Field field, final double minimumVariance, final double scale,
            final String symbol, final int memory) {
        super(new PCAPredictorTransposed(instantGenerator, memory, 
                minimumVariance,scale), instantGenerator, symbol, field, scale);
        this.symbol = symbol;
        this.instantGenerator = instantGenerator;
        this.vals = new double[this.instantGenerator.maxInstantValue()];
        for(int k=0; k<vals.length; k++) this.vals[k] = Double.NaN;
    }


    public void notify(final ParseResult parseResult) {
        final Map<Field, Double> x = parseResult.values(this.symbol);
        final double val = (x!=null && x.get(field)!=null) 
                ? x.get(field) : Double.NaN;
        final Calendar when = parseResult.getLocalTimestamp(this.symbol);
        this.learnValue(when, val);
        log.finest("Memorizing value " + val + " for " + when.get(
                Calendar.HOUR_OF_DAY) + ":" + when.get(Calendar.MINUTE)
                + ":" + when.get(Calendar.SECOND));
    }


    public void learnValue(final Calendar when, final double val) {
        final int i = this.instantGenerator.generate(when);
        if(i<this.previousInstant && this.hasLearnt) {
            this.cleanData(this.vals);
            log.finest("Learning " + when + " " + this.vals.length + " values");
            this.predictor.learnVector(this.previousWhen, this.vals);
            this.hasLearnt = false;
        }


        this.vals[i] = val;
        if(val != Double.NaN) {
            this.previousWhen = when;
            this.hasLearnt = true;
            this.previousInstant = i;
        }
    }


    public double[] predictVector(final Calendar when) {
        final int i = this.instantGenerator.generate(when);
        if(this.hasLearnt) {
            this.cleanData(this.vals);
            log.finest("Learning " + when + " " + this.vals.length + " values");
            this.predictor.learnVector(this.previousWhen, this.vals);
            this.hasLearnt = false;
            for(int k=0; k<vals.length; k++) {
                this.vals[k] = Double.NaN;
            }
        }
        log.finest("Made prediction for moment " + when);

        return super.predictVector(when);
    }
    
    /**
     * Returns the human name of the predictor.
     */
    public String toString() {
        return this.scale>0 ? "Not Normalized PCA Combining Several Days "
                + "Predictor" : "Normalized to " + this.scale 
                + " PCA Combining Several Days Predictor"; 
    }


    //
    // Hook methods --------------
    //

    /**
     * Performs some cleaning over given data before learning predictor.
     */
    abstract protected void cleanData(final double vals[]);
}

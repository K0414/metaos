/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.signalgrt.predictors.specific;

import java.util.*;
import com.metaos.signalgrt.predictors.*;
import com.metaos.signalgrt.predictors.simple.*;
import com.metaos.util.*;
import com.metaos.datamgt.*;

/**
 * Uses a predictor to forecast the daily data for the "typical" stock
 * based on data from all stocks of previous day.
 *
 * TODO: Rename to PCAUsingSeveralValuesOneDay
 *
 * Prepared to be used for markets with opening and closing times, but not
 * with pauses during the session.
 * <br/>
 * When predicting, unknown data is represented as NaN.
 */
public abstract class PCADayByDayPredictor extends StaticDayByDayPredictor {
    private final String symbols[];

    //
    // Public methods ---------------------------------------
    //


    /**
     * Creates a day-by-day predictor which uses data from each day to
     * forecast data for next day.
     *
     * @param scale value to scale daily predictions, 
     *      0 or less if no scale is wanted.
     * @param minimumVariance minimum explained variance by model.
     * @param symbols list of market symbols to consider.
     * @param patternSymbol symbol to project onto PCA.
     */
    public PCADayByDayPredictor(
            final CalUtils.InstantGenerator instantGenerator,
            final Field field, final double minimumVariance, final double scale,
            final String[] symbols, final String patternSymbol) {
        super(new PCAPredictor(instantGenerator, minimumVariance, scale), 
                instantGenerator, patternSymbol, field, scale);
        this.symbols = symbols;
    }


    public void notify(final ParseResult parseResult) {
        final Calendar when = parseResult.getLocalTimestamp(this.symbols[0]);
        final double[] vals = new double[symbols.length];
        for(int i=0; i<this.symbols.length; i++) {
            final Map<Field, Double> x = parseResult.values(this.symbols[i]);
            if(x!=null && x.get(field)!=null) {
                vals[i] = x.get(field);
            } else {
                vals[i] = Double.NaN;
            }
        }
        this.learnVector(when, vals);
    }


    public void learnValue(final Calendar when, final double val) {
        throw new UnsupportedOperationException(
                "Cannot learn values one by one");
    }

    /**
     * Returns the human name of the predictor.
     */
    public String toString() {
        return this.scale>0 ? "Not Normalized PCA DayByDay Predictor"
                : "Normalized to " + this.scale + " PCA DayByDay Predictor"; }
}

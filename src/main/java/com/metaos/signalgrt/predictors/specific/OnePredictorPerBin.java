/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.signalgrt.predictors.specific;

import java.util.*;
import java.util.logging.Logger;
import com.metaos.signalgrt.predictors.*;
import com.metaos.signalgrt.predictors.simple.*;
import com.metaos.util.*;
import com.metaos.datamgt.*;

/**
 * Set of predictors for daily forecasting, one for each bin of day, and the
 * same set of predictors for all days.
 *
 * <br/>
 * To developers: reimplement <code>cleanData</code> method to modify
 * daily data before learning.
 * <br/>
 * TODO: Simplify usage. Remove scale, field and simply perform a distribution
 * of "notify" and "learn" events to suitable internal predictors.
 */
public abstract class OnePredictorPerBin implements PredictorListener {
    private static final Logger log = Logger.getLogger(OnePredictorPerBin
            .class.getPackage().getName());

    private final Predictor[] predictors;
    private final CalUtils.InstantGenerator instantGenerator;
    private final String symbol;
    private final Field field;
    private final PredictorSelectionStrategy predictorSelectionStrategy;
    private final List<Pair> learnedValues;
    private final double scale;
    private Calendar lastLearningTime = CalUtils.getZeroCalendar();


    //
    // Public methods ---------------------------------------
    //


    /**
     * Creates a combined predictor: 5 days (labour days) + third friday,
     * time-series predictors with defined strategy to change predictors
     * and scaling learned and predicted values.
     *
     * @param scale value to scale daily predictions, 
     *      0 or less if no scale is wanted.
     */
    public OnePredictorPerBin(final Predictor.PredictorSelectionStrategy 
                predictorSelectionStrategy,
            final CalUtils.InstantGenerator instantGenerator,
            final String symbol, final Field field, final double scale) {
        this.scale = scale;
        this.predictorSelectionStrategy = predictorSelectionStrategy;
        this.field = field;
        this.symbol = symbol;
        this.instantGenerator = instantGenerator;
        this.predictors = new Predictor[instantGenerator.maxInstantValue()];
        for(int j=0; j<predictors.length; j++) {
            predictors[j] = predictorSelectionStrategy.buildPredictor();
            if(predictors[j]==null) {
                throw new RuntimeException("PredictorSelectionStrategy used "
                        + "(" + predictorSelectionStrategy.getClass() + ") "
                        + "does not implement correctly buildPredictor() "
                        + "method: it returns a null!");
            }
        }

        this.learnedValues = new ArrayList<Pair>();
        for(int i=0; i<this.instantGenerator.maxInstantValue(); i++) {
            this.learnedValues.add(null);
        }
        log.fine("Created set of " + this.instantGenerator.maxInstantValue()
                + " predictors, one per daily bin but the same set for every "
                + "days but different");
    }


    public void notify(final ParseResult parseResult) {
        final Calendar when = parseResult.getLocalTimestamp(this.symbol);
        if(parseResult.values(this.symbol) != null 
                && parseResult.values(this.symbol).get(field)!=null) {
            final double val = parseResult.values(this.symbol).get(field);
            this.learnValue(when, val);
        }
    }


    /**
     * Emits a single forecast based on learned values.
     */
    public double predict(final Calendar when) {
        throw new UnsupportedOperationException("Predictor only "
                + "generates a vector of values");
    }


    /**
     * Emits a forecast based on learned values.
     */
    public double[] predictVector(final Calendar when) {
        this.learnInsidePredictors();

        final double prediction[] = new double[
                this.instantGenerator.maxInstantValue()];
        for(int i=0; i<predictors.length; i++) {
            this.predictorSelectionStrategy.injectKernel(predictors[i]);
            prediction[i] = predictors[i].predict(when);
        }

        // Normalizes prediction
        if(this.scale>0) {
            double sum = 0;
            for(int i=0; i<prediction.length; i++) {
                if(Double.isNaN(prediction[i])) continue;
                sum += prediction[i];
            }
            double s = scale/sum;
            for(int i=0; i<prediction.length; i++) prediction[i] *= s;
        }

        return prediction;
    }


    /**
     * Memorizes several values at the same time.
     */
    public void learnVector(final Calendar when, final double[] vals) {
        if(this.instantGenerator.maxInstantValue()!=vals.length) {
            throw new IllegalArgumentException("Size of vector to learn "
                    + "must be equals to maximum number of instants for each "
                    + "training period (" 
                    + this.instantGenerator.maxInstantValue() 
                    + " in this case, and not " + vals.length + ")");
        }
        for(int j=0; j<vals.length; j++) {
            this.learnValue(when, vals[j]);
        }
    }


    /**
     * Memorizes several values at the same time.
     */
    public void learnVector(final Calendar when, final List<Double> vals) {
        if(this.instantGenerator.maxInstantValue()!=vals.size()) {
            throw new IllegalArgumentException("Size of vector to learn "
                    + "must be equals to maximum number of instants for each "
                    + "training period (" 
                    + this.instantGenerator.maxInstantValue() 
                    + " in this case, and not " + vals.size() + ")");
        }
        for(int j=0; j<vals.size(); j++) {
            this.learnValue(when, vals.get(j));
        }
    }


    /**
     * Memorizes one single value.
     */
    public void learnValue(final Calendar when, final double val) {
        // Date Control: noitified date should not be before previous date
        assert( ! when.before(this.lastLearningTime) );

        if(when.get(Calendar.DAY_OF_YEAR) !=
                        this.lastLearningTime.get(Calendar.DAY_OF_YEAR) 
                || when.get(Calendar.YEAR) !=
                        this.lastLearningTime.get(Calendar.YEAR)) {
            learnInsidePredictors();
        }

        this.lastLearningTime = when;

        final int j = this.instantGenerator.generate(when);
        this.learnedValues.set(j, new Pair(when, val));
    }


    /**
     * Empties memory and predictions restoring initial state.
     */
    public void reset() {
        for(int j=0; j<this.predictors.length; j++) {
            this.predictors[j].reset();
        }
        this.learnedValues.clear();
        for(int i=0; i<this.instantGenerator.maxInstantValue(); i++) {
            this.learnedValues.add(null);
        }
    }


    /**
     * Returns the human name of the predictor.
     */
    public String toString() {
        return this.scale>0 ? "Not Normalized Daily Predictors for bin"
                : "Normalized to " + this.scale + " Daily Predictors for bin";
    }



    //
    // Hook methods ---------------------------------------------
    //

    /**
     * Cleans data before learning.
     */
    protected abstract void cleanData(final double[] vals);

    //
    // Private stuff -----------------------
    //



    /**
     * Learns inside predictors with memorized values for day, normalizing
     * values (if it's needed) and forgeting values for next learning cycle.
     */
    private void learnInsidePredictors() {
        // Clean data
        final double vals[] = new double[this.learnedValues.size()];
        for(int i=0; i<vals.length; i++) {
            final Pair p = this.learnedValues.get(i);
            vals[i] = p!=null ? p.val : Double.NaN;
        }
 
        this.cleanData(vals);

        for(int i=0; i<vals.length; i++) {
            final Pair p = this.learnedValues.get(i);
            if(p!=null) {
                this.learnedValues.set(i, new Pair(p.when, vals[i]));
            }
        }

        // Normalize
        if(this.scale>0) {
            double total = 0;
            for(int moment=0; moment<this.learnedValues.size(); moment++) {
                final Pair p = this.learnedValues.get(moment);
                if(p!=null && !Double.isNaN(p.val)) {
                    total += p.val;
                }
            }
            for(int moment=0; moment<this.learnedValues.size(); moment++) {
                final Pair p = this.learnedValues.get(moment);
                if(p!=null) {
                    this.learnedValues.set(moment, 
                            new Pair(p.when, this.scale * p.val / total));
                }
            }
        }



        // Learn
        for(int moment=0; moment<this.learnedValues.size(); moment++) {
            final Pair p = this.learnedValues.get(moment);
            if(p!=null) {
                this.predictors[moment].learnValue(p.when, p.val);
            }
        }

        // Reset memorized values
        this.learnedValues.clear();
        for(int i=0; i<this.instantGenerator.maxInstantValue() ;i++) {
            this.learnedValues.add(null);
        }
    }



    /**
     * Struct-C emulation.
     */
    private class Pair {
        private final double val;
        private final Calendar when;
        private Pair(final Calendar when, final double val) {
            this.when = (Calendar) when.clone();
            this.val = val;
        }
    }
}

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
 * Set of predictors for daily forecasting, one for each day of week and
 * one more for third friday in month.
 *
 * <br/>
 * To developers: Class designed following Decorator Pattern: combine
 * several predictors, one for each type of day of week wrapping them with
 * an object of this class.
 * <br/>
 */
public class OnePredictorPerTypeOfDayOfWeek implements PredictorListener {
    private static final Logger log = Logger.getLogger(
            OnePredictorPerTypeOfDayOfWeek.class.getPackage().getName());

    private final PredictorListener[] predictors;
    private final PredictorSelectionStrategy predictorSelectionStrategy;
    private final TypeOfDaysStrategy typeOfDays;



    //
    // Public methods ---------------------------------------
    //


    /**
     * Creates a combined time-series predictors one for each different type
     * of day.
     *
     * @param predictorSelectionStrategy strategy in the sense of design 
     * patterns, to decide which predictor create and how to inject the core
     * before predicting. Must create <code>PredictorListener</code>s, not
     * only <code>Predictor</code>s, since their <code>notify</code>
     * will be invoked.
     * @param typeOfDays how is defined each type of day.
     */
    public OnePredictorPerTypeOfDayOfWeek(final Predictor
            .PredictorSelectionStrategy predictorSelectionStrategy,
            final TypeOfDaysStrategy typeOfDays) {
        this.predictorSelectionStrategy = predictorSelectionStrategy;
        this.predictors = new PredictorListener[typeOfDays.numberOfDays()];
        this.typeOfDays = typeOfDays;
        for(int i=0; i<typeOfDays.numberOfDays(); i++) {
            predictors[i] = predictorSelectionStrategy.buildPredictor();
        }
        log.fine("Created set of " + typeOfDays.numberOfDays()
                + "predictors, one for each type of day");
    }




    public void notify(final ParseResult parseResult) {
        final Calendar when = parseResult.getLocalTimestamp(0);
        final int index = typeOfDays.typeOfDay(when);
        if(index==-1) return;
        this.predictors[index].notify(parseResult);
    }


    /**
     * Emits a single forecast based on learned values.
     */
    public double predict(final Calendar when) {
        final int index = typeOfDays.typeOfDay(when);
        return predictors[index].predict(when);
    }


    /**
     * Emits a forecast based on learned values.
     */
    public double[] predictVector(final Calendar when) {
        final int index = typeOfDays.typeOfDay(when);
        this.predictorSelectionStrategy.injectKernel(predictors[index]);
        return predictors[index].predictVector(when);
    }


    /**
     * Memorizes several values at the same time.
     */
    public void learnVector(final Calendar when, final double[] vals) {
        final int index = typeOfDays.typeOfDay(when);
        predictors[index].learnVector(when, vals);
    }


    /**
     * Memorizes several values at the same time.
     */
    public void learnVector(final Calendar when, final List<Double> vals) {
        final int index = typeOfDays.typeOfDay(when);
        predictors[index].learnVector(when, vals);
    }


    /**
     * Memorizes one single value.
     */
    public void learnValue(final Calendar when, final double val) {
        final int index = typeOfDays.typeOfDay(when);
        predictors[index].learnValue(when, val);
    }


    /**
     * Empties memory and predictions restoring initial state.
     */
    public void reset() {
        for(int i=0; i<this.predictors.length; i++) {
            this.predictors[i].reset();
        }
    }


    /**
     * Returns the human name of the predictor.
     */
    public String toString() {
        return "Weekly Predictor " + this.predictors[0].toString();
    }
}

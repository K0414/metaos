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
 * Adaptor for a predictor specifically designed for dynamic intraday forecast
 * using all previous data only in the current day.
 *
 * The difference with <i>Dynamic</i>IntradayPredictorListener is that
 * <i>QuasiDynamic</i> predicts the whole day from the first N daily data
 * and <i>Dynamic</i> predicts next intrady value taken into account all
 * previous values in the day.
 */
public class DynamicIntradayPredictorListener implements PredictorListener {
    private final Predictor predictor;
    private final Field field;
    private final double scale;
    private final CalUtils.InstantGenerator instantGenerator;
    private final double[] predictedIntraday;
    private Calendar lastLearningTime = CalUtils.getZeroCalendar();


    //
    // Public methods ---------------------------------------
    //


    /**
     * Creates a coverage over a predictor to perform the next functions:
     *  <ul>
     *    <li>Listening to trading data events</li>
     *    <li>Reseting when the end of the day is reached</li>
     *  </ul>
     *
     * @param predictor adpateed predictor.
     * @param field field to deal with.
     * @param scale 0 or less to not scale, or the positive value 
     * to scale predictions (i.e, the sum of all daily predictions
     * will be <i>scale</i>).
     */
    public DynamicIntradayPredictorListener(final Predictor predictor,
            final Field field, final double scale, 
            final CalUtils.InstantGenerator instantGenerator) {
        this.field = field;
        this.predictor = predictor;
        this.scale = scale;
        this.instantGenerator = instantGenerator;
        this.predictedIntraday = new double[instantGenerator.maxInstantValue()];
    }


    public void notify(final ParseResult parseResult) {
        // Date Control: noitified date should not be before previous date
        final Calendar when = parseResult.getLocalTimestamp();
        assert( ! when.before(this.lastLearningTime) );

        if(when.get(Calendar.DAY_OF_YEAR) !=
                        this.lastLearningTime.get(Calendar.DAY_OF_YEAR) 
                || when.get(Calendar.YEAR) !=
                        this.lastLearningTime.get(Calendar.YEAR)) {
            this.reset();
        }

        this.lastLearningTime = when;

        if(parseResult.values(0) != null 
                && parseResult.values(0).get(field)!=null) {
            final double val = parseResult.values(0).get(field);
            this.learnValue(when, val);
        }
    }


    /**
     * Emits a single forecast based on learned values.
     */
    public double predict(final Calendar when) {
        final int index = this.instantGenerator.generate(when);
        final double val = this.predictor.predict(when);
        this.predictedIntraday[index] = val;
        return val;
    }


    /**
     * Emits a forecast based on learned values.
     */
    public double[] predictVector(final Calendar when) {
        final double newVal = this.predictor.predict(when);
        final int index = this.instantGenerator.generate(when);
        this.predictedIntraday[index] = newVal;

        final double prediction[] = new double[this.predictedIntraday.length];
        for(int i=0; i<prediction.length; i++) {
            prediction[i] = this.predictedIntraday[i];
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
        throw new UnsupportedOperationException("Don't know what to do....");
    }


    /**
     * Memorizes several values at the same time.
     */
    public void learnVector(final Calendar when, final List<Double> vals) {
        throw new UnsupportedOperationException("Don't know what to do....");
    }


    /**
     * Memorizes one single value.
     */
    public void learnValue(final Calendar when, final double val) {
        this.predictor.learnValue(when, val);
        final int index = this.instantGenerator.generate(when);
        this.predictedIntraday[index] = val;
    }


    /**
     * Empties memory and predictions restoring initial state.
     */
    public void reset() {
        this.predictor.reset();
        for(int i=0; i<this.predictedIntraday.length; i++) {
            this.predictedIntraday[i] = 0;
        }
    }


    /**
     * Returns the human name of the predictor.
     */
    public String toString() {
        return this.scale>0 ? "Not Normalized Intraday Dynamic Predictor"
                : "Normalized to " + this.scale + " Intraday Dynamic Predictor";
    }
}

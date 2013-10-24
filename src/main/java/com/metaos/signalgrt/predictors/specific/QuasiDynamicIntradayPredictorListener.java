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
 * using data only at the begining of the current day.
 *
 * The difference with <i>Dynamic</i>IntradayPredictorListener is that
 * <i>QuasiDynamic</i> predicts the whole day from the first N daily data
 * and <i>Dynamic</i> predicts next intrady value taken into account all
 * previous values in the day.
 *
 */
public class QuasiDynamicIntradayPredictorListener 
        implements PredictorListener {
    private final Predictor predictor;
    private final Field field;
    private final double scale;
    private final List<Double> predictedIntraday;
    private Calendar lastLearningTime = CalUtils.getZeroCalendar();
    private boolean hasPredicted = false;


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
    public QuasiDynamicIntradayPredictorListener(final Predictor predictor,
            final Field field, final double scale) {
        this.field = field;
        this.predictor = predictor;
        this.scale = scale;
        this.predictedIntraday = new ArrayList<Double>();
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
        
        if(this.hasPredicted) return;

        if(parseResult.values(0) != null 
                && parseResult.values(0).get(field)!=null) {
            final double val = parseResult.values(0).get(field);
            this.learnValue(when, val);
            this.predictedIntraday.add(val);
        }
    }


    /**
     * Emits a single forecast based on learned values.
     */
    public double predict(final Calendar when) {
        return this.predictor.predict(when);
    }


    /**
     * Emits a forecast based on learned values.
     */
    public double[] predictVector(final Calendar when) {
        if(!this.hasPredicted) {
            this.hasPredicted = true;
            final double tmp[] = this.predictor.predictVector(when);
            for(int i=0; i<tmp.length; i++) {
                this.predictedIntraday.add(tmp[i]);
            }
        }

        final double prediction[] = new double[this.predictedIntraday.size()];
        for(int i=0; i<prediction.length; i++) {
            prediction[i] = this.predictedIntraday.get(i);
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
        this.predictor.learnVector(when, vals);
    }


    /**
     * Memorizes several values at the same time.
     */
    public void learnVector(final Calendar when, final List<Double> vals) {
        this.predictor.learnVector(when, vals);
    }


    /**
     * Memorizes one single value.
     */
    public void learnValue(final Calendar when, final double val) {
        this.predictor.learnValue(when, val);
    }


    /**
     * Empties memory and predictions restoring initial state.
     */
    public void reset() {
        this.predictor.reset();
        this.predictedIntraday.clear();
        this.hasPredicted = false;
    }


    /**
     * Returns the human name of the predictor.
     */
    public String toString() {
        return this.scale>0 ? "Not Normalized Weekly Predictor"
                : "Normalized to " + this.scale + " Weekly Predictor";
    }
}

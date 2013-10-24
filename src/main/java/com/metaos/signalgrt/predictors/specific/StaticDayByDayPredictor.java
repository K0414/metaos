/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.signalgrt.predictors.specific;

import java.text.*;
import java.util.*;
import java.util.logging.*;
import com.metaos.signalgrt.predictors.*;
import com.metaos.signalgrt.predictors.simple.*;
import com.metaos.util.*;
import com.metaos.datamgt.*;

/**
 * Uses a predictor to forecast each day based on learnt data from 
 * previous day.
 * <br/>
 * Note to developrs: reimplement <code>cleanData</code> protected method
 * to clean data before learning.
 */
public abstract class StaticDayByDayPredictor implements PredictorListener {
    private static final Logger log = Logger.getLogger(StaticDayByDayPredictor
            .class.getPackage().getName());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd");
    protected final Predictor predictor;
    protected final CalUtils.InstantGenerator instantGenerator;
    protected final Field field;
    protected final List<Calendar> memorizedMoments;
    protected final List<Object> memorizedValues;
    protected final double scale;
    protected final String symbol;
    protected Calendar lastLearningTime = CalUtils.getZeroCalendar();


    //
    // Public methods ---------------------------------------
    //


    /**
     * Creates a day-by-day predictor which uses data from each day to
     * forecast data for next day.
     *
     * @param scale value to scale daily predictions, 
     *      0 or less if no scale is wanted.
     */
    public StaticDayByDayPredictor(final Predictor predictor,
            final CalUtils.InstantGenerator instantGenerator,
            final String symbol, final Field field, final double scale) {
        this.scale = scale;
        this.field = field;
        this.instantGenerator = instantGenerator;
        this.predictor = predictor;
        this.symbol = symbol;
        this.memorizedValues = new ArrayList<Object>();
        this.memorizedMoments = new ArrayList<Calendar>();
    }



    /**
     * Emits a single forecast based on learned values.
     */
    public double predict(final Calendar when) {
        this.learnMemorizedValues();
        return this.predict(when);
    }


    /**
     * Emits a forecast based on learned values.
     */
    public double[] predictVector(final Calendar when) {
        this.learnMemorizedValues();

        preparePredictorBeforePredictingAfterLearning();

        final double[] prediction = this.predictor.predictVector(when);
        if(this.scale>0) {
            double sum = 0;
            for(int i=0; i<prediction.length; i++) {
                if(Double.isNaN(prediction[i])) continue;
                sum += prediction[i];
            }
            if(sum==0) return prediction;
            for(int i=0; i<prediction.length; i++) {
                prediction[i] = this.scale * prediction[i] / sum;
            }
        }

        return prediction;
    }


    /**
     * Memorizes several values at the same time.
     */
    public void learnVector(final Calendar when, final double[] vals) {
        // Date Control: notified date should not be before previous date
        assert( ! when.before(this.lastLearningTime) );
        this.memorizedValues.add(vals);
        this.memorizedMoments.add(when);
    }


    /**
     * Memorizes several values at the same time.
     */
    public void learnVector(final Calendar when, final List<Double> vals) {
        // Date Control: notified date should not be before previous date
        assert( ! when.before(this.lastLearningTime) );
        this.memorizedValues.add(vals);
        this.memorizedMoments.add(when);
    }


    /**
     * Memorizes one single value.
     */
    public void learnValue(final Calendar when, final double val) {
        // Date Control: notified date should not be before previous date
        assert( ! when.before(this.lastLearningTime) );
        this.memorizedValues.add(val);
        this.memorizedMoments.add(when);
    }


    /**
     * Empties memory and predictions restoring initial state.
     */
    public void reset() {
        this.memorizedValues.clear();
        this.memorizedMoments.clear();
        this.predictor.reset();
    }


    /**
     * Returns the human name of the predictor.
     */
    public String toString() {
        return this.scale>0 ? "Not Normalized Static DayByDay Predictor"
                : "Normalized to " + this.scale + " Static DayByDay Predictor";
    }


    //
    // Hook method ------------
    //
    protected abstract void cleanData(final double[] vals);


    //
    // Private methods -------------
    //

    private void learnMemorizedValues() {
        if(this.memorizedValues.size()==0) return;

        if(this.memorizedValues.get(0) instanceof double[]) {
            for(int j=0;j<((double[])this.memorizedValues.get(0)).length;j++) {
                final double vals[] = new double[this.memorizedValues.size()];
                for(int i=0; i<vals.length; i++) {
                    vals[i] = ((double[]) this.memorizedValues.get(i))[j];
                }

                this.cleanData(vals);

                for(int i=0; i<vals.length; i++) {
                    ((double[]) this.memorizedValues.get(i))[j] = vals[i];
                }
            }

        } else if(this.memorizedValues.get(0) instanceof List) {
            throw new UnsupportedOperationException(
                    "Not implemented yet: clean data "
                    + "transversaly to memorized data");
        } else {
            final double vals[] = new double[this.memorizedValues.size()];
            for(int i=0; i<this.memorizedValues.size(); i++) {
                vals[i] = ((Double) this.memorizedValues.get(i)).doubleValue();
            }
            this.cleanData(vals);
            for(int i=0; i<this.memorizedValues.size(); i++) {
                this.memorizedValues.set(i, vals[i]);
            }
        }

        // Remove columns without values
        final Object aRow = this.memorizedValues.get(0);
        final Set<Integer> avoidIndexes = new HashSet<Integer>();
        if(aRow instanceof double[]) {
            final List<Integer> tmpAvoidIndexes = new ArrayList<Integer>();
            for(int i=0; i<((double[]) aRow).length; i++) {
                int numberOfNaNs = 0;
                double previousValue = -1;
                for(int j=0; j<this.memorizedValues.size(); j++) {
                    if(Double.isNaN(((double[]) 
                            this.memorizedValues.get(j))[i])) {
                        numberOfNaNs++;
                    } else {
                        if(((double[])this.memorizedValues.get(j))[i] 
                                == previousValue) {
                            numberOfNaNs++;
                        }
                        previousValue = ((double[])
                                        this.memorizedValues.get(j))[i];
                    }

                }
                if(numberOfNaNs>this.memorizedValues.size()*0.5) {
                    avoidIndexes.add(i);
                }
            }
        } else if(aRow instanceof List) {
            throw new UnsupportedOperationException("Not implemented yet, "
                    + " but easy to implement...");
        }
        if(avoidIndexes.size()>0) {
            log.info("Removing " + avoidIndexes.size() + " unuseful columns "
                + dateFormat.format(this.memorizedMoments.get(0).getTime()));
        }

        for(int i=0; i<this.memorizedValues.size(); i++) {
            final Object obj = this.memorizedValues.get(i);
            if(obj instanceof double[]) {
                final double[] obj2 = (double[]) obj;
                final double[] tmp = new double[
                                        obj2.length - avoidIndexes.size()];
                for(int k=0,j=0; k<obj2.length; k++) {
                    if(avoidIndexes.contains(k)) continue;
                    tmp[j] = obj2[k];
                    j++;
                }
                this.predictor.learnVector(this.memorizedMoments.get(i), tmp);
            } else if(obj instanceof List) {
                final List obj2 = (List) obj;
                final List<Double> tmp = new ArrayList<Double>();
                for(int k=0; k<obj2.size(); k++) {
                    if(avoidIndexes.contains(k)) continue;
                    tmp.add((Double) obj2.get(k));
                }
                this.predictor.learnVector(this.memorizedMoments.get(i), tmp);
            } else {
                this.predictor.learnValue(this.memorizedMoments.get(i),
                        ((Double) obj).doubleValue());
            }
        }
        this.memorizedValues.clear();
        this.memorizedMoments.clear();
    }


    //
    // Hook method ---------------------------------------
    //

    /**
     * Prepares predictor (maybe injecting kernel for injectable ones)
     * after learning values but before predicting.
     */
    protected void preparePredictorBeforePredictingAfterLearning() {
    }
}

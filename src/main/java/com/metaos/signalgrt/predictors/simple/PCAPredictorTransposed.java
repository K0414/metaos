/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.signalgrt.predictors.simple;

import com.metaos.signalgrt.predictors.*;
import com.metaos.engine.*;
import com.metaos.util.*;
import java.util.*;

/**
 * Almost the same as PCA but calculates principal values on transposed matrix.
 * <br/>
 * It's useful when PCA is tought with sequential data in time, and each
 * row represents the same profile for the same symbol in different days,
 * for instance.
 * <br/>
 * See <code>PCAPredictor</code> for information on how R engine is used.
 * <br/>
 * Usage: learn with vectors with daily (or weekly, monthly..) values for a 
 * given product. If there are no data in some moments (for instance in daily
 * data before market opens or after market ends) place NaN values at the
 * begining and at the end of vector of data. This predictor, using PCAPredictor
 * facilities, will ignore the previous and last block of unknown data,
 * creating a prediction that reprocduces these unkown data, with NaN at
 * the begining and at the end.
 * <br/>
 * But it cannot deal with NaN <b>among</b> valid block of data.
 * <br/>
 * For example:
 * <ul>
 *  <li>These data are valid: [NaN,NaN,1,2,3,4,5,6,NaN,NaN,NaN,NaN]</li>
 *  <li>But these data are not: [NaN,NaN,1,2,3,NaN,5,6,NaN,NaN,NaN,NaN]</li>
 * </ul>
 * <br/>
 * ATTENTION: Not thread safe.
 */
public class PCAPredictorTransposed extends PCAPredictor {
    private int jIndex = -1;
    private final int memorySize;
    private long learningMoments;
    private boolean initialized;

    /**
     * Creates an PCAPredictor predictor with given parameters.
     *
     * @param minimumExplainedVariance quantity of variance that should be
     * explained by eigenvectors.
     */
    public PCAPredictorTransposed(
            final CalUtils.InstantGenerator instantGenerator,
            final int memorySize, final double minimumExplainedVariance,
            final double scale) {
        super(instantGenerator, minimumExplainedVariance, scale);
        log.finest("Created PCAPredictorTransposed memorySize=" + memorySize);
        this.memorySize = memorySize;
        this.initialized = false;
    }


    /**
     * Creates an PCAPredictor predictor with given parameters.
     * Only first eigenvector will be used to explain variance.
     */
    public PCAPredictorTransposed(
            final CalUtils.InstantGenerator instantGenerator, 
            final int memorySize) {
        super(instantGenerator, 0.0d);
        log.finest("Created PCAPredictorTransposed memorySize=" + memorySize);
        this.memorySize = memorySize;
        this.initialized = false;
    }


    /**
     * @param when only day is important to test if time goes in advance.
     */
    public void learnVector(final Calendar when, final double[] vals) {
        // Checks for, at least, one valid number to learn
        boolean thereAreNumbers = false;
        for(int i=0; i<vals.length; i++) {
            if(!Double.isNaN(vals[i])) {
                thereAreNumbers=true;
                break;
            }
        }
        if( ! thereAreNumbers ) return;

        if(!this.initialized) {
            // Initializes matrix
            for(int i=0; i<vals.length; i++) {
                this.matrix[i] = new double[this.memorySize];
            }
            this.initialized = true;
        }

        final int currentInstant = this.instantGenerator.generate(when);
        assert(currentInstant>this.lastInstant);

        this.jIndex += 1;
        if(this.jIndex>=this.memorySize) this.jIndex = 0;

        for(int i=0; i<vals.length; i++) {
            this.matrix[i][jIndex] = vals[i];
        }

        this.lastInstant = currentInstant;
        this.learningMoments ++;
        if(this.learningMoments<0) this.learningMoments = this.memorySize;
        super.lastLearningTime = when;
    }


    /**
     * A little less efficient than the other version, since this function 
     * calls to the other one with the same name and an array of doubles as
     * argument..
     */
    public void learnVector(final Calendar when, final List<Double> vals) {
        final double vals2[] = new double[vals.size()];
        for(int i=0; i<vals2.length; i++) {
            vals2[i] = vals.get(i);
        }
        this.learnVector(when, vals2);
    }


    public double[] predictVector(final Calendar when) {
        if(this.learningMoments<this.memorySize) {
            return new double[this.instantGenerator.maxInstantValue()];
        } else {
            return super.predictVector(when);
        }
    }

    public String toString() {
        return "PCA Predictor Transposed";
    }
}

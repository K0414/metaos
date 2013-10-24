/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.signalgrt.predictors.simple;

import com.metaos.signalgrt.predictors.*;
import java.util.*;

/**
 * Moving median as predictor for the next value.
 * ATTENTION: Not thread safe.
 */
public class MovingMedian implements Predictor {
    private final com.metaos.signalgrt.indicators.traditional.MovingMedian
            movingMedian;

    public MovingMedian(final int memorySize) {
        this.movingMedian = new com.metaos.signalgrt.indicators.traditional
                .MovingMedian(memorySize);
    }


    /**
     * Returns the median of learnt values, of NaN if not enough values have
     * been learnt.
     */
    public double predict(final Calendar ignored) {
        return this.movingMedian.calculate();
    }


    public double[] predictVector(final Calendar ignored) {
        return new double[] { this.movingMedian.calculate() };
    }


    public void learnVector(final Calendar ignored, final double[] vals) {
        this.movingMedian.addValues(vals);
    }


    public void learnVector(final Calendar ignored, final List<Double> vals) {
        this.movingMedian.addValues(vals);
    }


    public void learnValue(final Calendar ignored, final double val) {
        this.movingMedian.addValue(val);
    }


    public void reset() {
        this.movingMedian.reset();
    }


    public String toString() {
        return "Predictor-" + this.movingMedian.toString();
    }
}

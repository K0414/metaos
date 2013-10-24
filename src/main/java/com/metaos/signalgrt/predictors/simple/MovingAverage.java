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
 * Moving average as predictor for the next value.
 * ATTENTION: Not thread safe.
 */
public class MovingAverage implements Predictor {
    private final com.metaos.signalgrt.indicators.traditional.MovingAverage
            movingAverage;

    public MovingAverage(final int memorySize) {
        this.movingAverage = new com.metaos.signalgrt.indicators
                .traditional.MovingAverage(memorySize);
    }


    public double predict(final Calendar ignored) {
        return this.movingAverage.calculate();
    }


    public double[] predictVector(final Calendar ignored) {
        return new double[] { predict(ignored) };
    }


    public void learnVector(final Calendar ignored, final double[] vals) {
        this.movingAverage.addValues(vals);
    }


    public void learnVector(final Calendar ignored, final List<Double> vals) {
        this.movingAverage.addValues(vals);
    }


    public void learnValue(final Calendar ignored, final double val) {
        this.movingAverage.addValue(val);
    }


    public void reset() {
        this.movingAverage.reset();
    }


    public String toString() {
        return "Predictor-" + this.movingAverage.toString();
    }
}

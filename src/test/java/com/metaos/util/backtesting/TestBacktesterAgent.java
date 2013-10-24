/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.util.backtesting;

import java.util.*;
import com.metaos.datamgt.*;
import com.metaos.util.*;
import com.metaos.signalgrt.predictors.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Test of backtesting functionallity.
 */
public class TestBacktesterAgent {
    @Test
    public void checkPredictionAndLearningMoments() throws Exception {
        final ForecastingTime forecastingTime = new ForecastingTime() {
            public boolean shouldOnlyEvaluatePrediction(final Calendar when) {
                return false;
            }
            public boolean shouldOnlyPredict(final Calendar when) {
                return false;
            }
            public boolean shouldPredictAndEvaluate(final Calendar when) {
                return false;
            }

            public boolean shouldEvaluatePreviousPredictionAndPredict(
                            final Calendar when) {
                return false;
            }
            public void notify(final ParseResult result) { }
        };

        final ForecastingTest forecastingTest = new ForecastingTest() {
            public void evaluate(final Calendar when, 
                    final double[] prediction) {
            }
            public void notify(final ParseResult result) { }
        };

        final PredictorListener dummyPredictor = new PredictorListener() {
            public double predict(final Calendar when) { return 0; }
            public double[] predictVector(final Calendar when) { return null; }
            public void learnVector(final Calendar when, final double[] vals) {}
            public void learnVector(final Calendar when, 
                    final List<Double> vals) { }
            public void learnValue(final Calendar when, final double val) { }
            public void reset() { }
            public String toString() { return null; }
            public void notify(final ParseResult result) { }
        };

        final BacktesterAgent agent = new BacktesterAgent(dummyPredictor,
                forecastingTime, forecastingTest);
        final ParseResult[] events = new ParseResult[0];

        notifyToAgent(agent, events);
    }


    //
    // Private stuff ------
    //

    private void notifyToAgent(final BacktesterAgent agent, 
            final ParseResult[] events) {
        for(int i=0; i<events.length; i++) {
            agent.notify(events[i]);
        }
    }



    public static void main(final String args[]) throws Exception {
        final TestBacktesterAgent target = new TestBacktesterAgent();
        target.checkPredictionAndLearningMoments();
    }
}

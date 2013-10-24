/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.signalgrt.predictors;

import com.metaos.engine.*;
import com.metaos.util.*;
import com.metaos.signalgrt.predictors.simple.*;
import com.metaos.signalgrt.predictors.specific.*;
import java.text.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * Testing MovingMedian.
 */
public class TestMovingMedian {
    private static class LocalTimeMinutes implements CalUtils.InstantGenerator {
        private final int minutes;
        public LocalTimeMinutes(final int minutes) { this.minutes = minutes; }
        public int generate(final Calendar when) {
            return when.get(Calendar.HOUR_OF_DAY)*60+when.get(Calendar.MINUTE);
        }
        public int maxInstantValue() { return 60*24/minutes; }
    }

    @Test
    public void testNaivePrediction() throws Exception {
        final Predictor predictor = new MovingMedian(15);

        for(int i=0; i<50; i++) {
            predictor.learnValue(null, 7);
        }
        assertEquals(7, predictor.predict(null), 0.000001);


         for(int i=0; i<50; i++) {
            predictor.learnValue(null, Double.NaN);
        }

        assertEquals(Double.NaN, predictor.predict(null), 0.000001);
    }



    public static void main(final String args[]) throws Exception {
        try {
            new Engine();
            final TestMovingMedian test = new TestMovingMedian();
            test.testNaivePrediction();
        } finally {
            Engine.getR().end();
        }
    }
}

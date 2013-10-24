/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.signalgrt.predictors;

import java.util.*;
import com.metaos.datamgt.*;

/**
 * Predictor Decorated with a Listener interface.
 */
public interface PredictorListener extends Predictor, Listener {
    /**
     * Implementation to wrap a predictor acting only over a symbol and
     * a field.
     */
    public static class SingleSymbolField implements PredictorListener {
        private final Predictor internal;
        private final String symbol;
        private final Field field;
        public SingleSymbolField(final Predictor predictor, final String symbol,
                        final Field field) {
                this.internal = predictor;
                this.symbol = symbol;
                this.field = field;
        }

        /**
         * Receives notification signals.
         */
        public void notify(final ParseResult result) {
            final Calendar when = result.getLocalTimestamp(0);
            if(result.values(0)!=null
                            && result.values(0).get(field)!=null) {
                internal.learnValue(when, result.values(symbol).get(field));
            }
        }
                 
        /**
         * Emits a single forecast based on learned values.
         */
        public double predict(final Calendar when) {
            return internal.predict(when);
        }
        
        /**
         * Emits a forecast based on learned values.
         */
        public double[] predictVector(final Calendar when) {
            return internal.predictVector(when);
        }

        /**
         * Memorizes several values at the same time.
         */
        public void learnVector(final Calendar when, final double[] vals) {
            internal.learnVector(when, vals);
        }

        /**
         * Memorizes several values at the same time.
         */
        public void learnVector(final Calendar when, final List<Double> vals) {
            internal.learnVector(when, vals);
        }


        /**
         * Memorizes one single value.
         */
        public void learnValue(final Calendar when, final double val) {
            internal.learnValue(when, val);
        }

        /**
         * Empties memory and predictions restoring initial state.
         */
        public void reset() {
            internal.reset();
        }

        /**
         * Returns the human name of the predictor.
         */
        public String toString() {
            return internal.toString();
        }

        /**
         * Gets the internal wrapped predictor.
         */
        public Predictor getPredictor() {
            return internal;
        }
    }
}
